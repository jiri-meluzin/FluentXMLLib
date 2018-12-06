package com.meluzin.ioutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;


public class FileStreamIterator implements Iterator<Path> {
	private boolean finished = false;
	private volatile int processing = 0;
	private Predicate<Path> fileMatcher = p -> true;
	private boolean parallel = true;
	private boolean recursive = true;
	private ConcurrentLinkedQueue<Path> queue = new ConcurrentLinkedQueue<>();
	public FileStreamIterator(Path start, boolean parallel, Predicate<Path> fileMatcher, boolean recursive) {
		this.parallel = parallel;
		this.fileMatcher = fileMatcher;
		this.recursive = recursive;
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		commonPool.submit(() -> searchDir(start, commonPool));
		
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
			increase();
			Files.newDirectoryStream(bwSourcePath).forEach(f -> {
				if (Files.isDirectory(f) && isRecursive()) {
					increase();
					if (isParallel()) {
						commonPool.submit(() -> processSubdir(commonPool, f));
					} else {
						processSubdir(commonPool, f);
					}
				}
				if (fileMatcher.test(f)) {
					insertPath(f);
				}
			});
			decrease();
			onFinished(commonPool);
		} catch (IOException e) {
			throw new RuntimeException("Could not read files in " + bwSourcePath, e);
		}
	}
	public boolean isParallel() {
		return parallel;
	}
	private void onFinished(ForkJoinPool commonPool) {
		if (getProcessing() == 0)
		setFinished(true);
		//if (isParallel() && getProcessing() == 0) commonPool.shutdown();
	}
	private void processSubdir(ForkJoinPool commonPool, Path f) {
		searchDir(f, commonPool);
		decrease();
		onFinished(commonPool);
	}
	public synchronized void insertPath(Path path) {
		queue.add(path);
		FileStreamIterator.this.notifyAll();
	}
	public boolean isFinished() {
		return finished;
	}
	public synchronized void setFinished(boolean finished) {
		this.finished = finished;
		FileStreamIterator.this.notifyAll();
	}
	@Override
	public synchronized boolean hasNext() {
		if (!finished && queue.isEmpty()) {
			try {
				FileStreamIterator.this.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException("Wait interrupted", e);
			}
		}
		return !queue.isEmpty() || !finished;
	}

	@Override
	public Path next() {
		return queue.poll();
	}

	
}
