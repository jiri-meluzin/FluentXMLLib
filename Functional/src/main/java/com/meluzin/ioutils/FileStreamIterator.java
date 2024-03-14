package com.meluzin.ioutils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.meluzin.functional.FileSearcher;
import com.meluzin.functional.Log;
import com.meluzin.functional.T;
import com.meluzin.functional.T.V1;
import com.meluzin.functional.T.V2;


public class FileStreamIterator implements Iterator<Path> {
	private static Logger log = Log.get();
	private boolean finished = false;
	private volatile int processing = 0;
	private Predicate<Path> fileMatcher = p -> true;
	private boolean parallel = true;
	private boolean recursive = true;
	private boolean error = false;
	private ForkJoinPool commonPool;
	private ConcurrentLinkedQueue<T.V2<Path, Throwable>> foundFiles = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Path> dirsToProcess = new ConcurrentLinkedQueue<>();
	public FileStreamIterator(Path start, boolean parallel, Predicate<Path> fileMatcher, boolean recursive) {
		this.parallel = parallel;
		this.fileMatcher = fileMatcher;
		this.recursive = recursive;
		if (parallel) {
			commonPool = new ForkJoinPool();		
			commonPool.submit(() -> {
				try {
					searchDir(start, commonPool);
				} catch (Exception ex) {
					log.log(Level.SEVERE, "Could not search files in " + start, ex);
					insertThrowable(ex);
				}
			});
		} else {
			searchDir(start, null);
		}
		
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	private synchronized void increase() {
		processing ++;
	}
	private synchronized void decrease() {
		processing --;
	}
	
	private synchronized int getProcessing() {
		return processing;
	}
	public boolean isRecursive() {
		return recursive;
	}
	private void searchDir(Path bwSourcePath, ForkJoinPool commonPool )  {
		try {
			if (isError()) {
				onFinished();
				return;
			}
			increase();
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(bwSourcePath)) {
				dirStream.forEach(f -> {
					if (isError()) {
						return;
					}
					if (Files.isDirectory(f) && isRecursive()) {
						increase();
						if (isParallel()) {
							commonPool.submit(() -> {
								try {
									processSubdir(commonPool, f);
								} catch (Exception e) {
									insertThrowable(e);
								}
							});
						} else {
							if (foundFiles.size() > 300) {
								dirsToProcess.add(f);
							} else {
								processSubdir(commonPool, f);
							}
						}
					}
					if (fileMatcher.test(f)) {
						insertPath(f);
					}
				});
			}
			decrease();
			onFinished();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not read files in " + bwSourcePath, e);
			throw new RuntimeException("Could not read files in " + bwSourcePath, e);
		}
	}
	public boolean isParallel() {
		return parallel;
	}
	private void onFinished() {
		int processing2 = getProcessing();
		if (processing < 0) log.severe("Processing is bellow 0");
		if (processing2 == 0)
		setFinished(true);
		//if (isParallel() && getProcessing() == 0) commonPool.shutdown();
	}
	private void processSubdir(ForkJoinPool commonPool, Path f) {
		try {
			searchDir(f, commonPool);
		} catch (Exception e) {
			insertThrowable(e);
		}
		decrease();
		onFinished();
	}
	public void insertPath(Path path) {
		if (isParallel()) {
			while (foundFiles.size() > 300) {
				synchronized (this) {
					FileStreamIterator.this.notifyAll();	
				}
				synchronized (this) {
					try {
						FileStreamIterator.this.wait(10);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		synchronized (this) {
			foundFiles.add(T.V(path, null));
			FileStreamIterator.this.notifyAll();	
		}
	}
	public synchronized void insertThrowable(Throwable throwable) {
		throwable.printStackTrace();
		setError(true);
		foundFiles.add(T.V(null, throwable));
		FileStreamIterator.this.notifyAll();
	}
	public synchronized boolean isFinished() {
		return finished;
	}
	public synchronized void setFinished(boolean finished) {
		this.finished = finished;
		FileStreamIterator.this.notifyAll();
		if (finished && this.commonPool!=null) this.commonPool.shutdown();
	}
	
	private synchronized boolean isQueueEmpty() {
		return foundFiles.isEmpty();
	}
	
	@Override
	public boolean hasNext() {
		if (isParallel()) {
			while (!isFinished() && isQueueEmpty()) {
				synchronized (this) {
					if (!isFinished() && isQueueEmpty()) {
						try {
							FileStreamIterator.this.wait();
						} catch (InterruptedException e) {
							throw new RuntimeException("Wait interrupted", e);
						}
					}
				}
			}
		} else {
			while (isQueueEmpty() && dirsToProcess.size() > 0)  {
				processSubdir(null, dirsToProcess.poll());
			}
		}
		return !isQueueEmpty();
	}

	@Override
	public Path next() {
		V2<Path, Throwable> poll = foundFiles.poll();
		if (poll.getB() != null) {
			log.log(Level.SEVERE, "A error occured while iterating files", poll.getB());
			throw new RuntimeException("Could not finish searching", poll.getB());
		}
		return poll.getA();
	}

	public static void main(String[] args) {
		while (true) {
			V1<Integer> v = T.V(0);
			Instant now = Instant.now();
			new FileSearcher().iterateFiles(Paths.get(args[0]), "glob:**/*", true, true).forEach(p ->{
				v.setA(v.getA()+1);
			});
			Instant minusMillis = now.minusMillis(Instant.now().toEpochMilli());
			System.out.println(minusMillis);
			System.out.println(v.getA());
		}
	}
}
