package com.meluzin.functional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.meluzin.ioutils.FileStreamIterable;
import com.meluzin.ioutils.FileStreamIterator;

public class FileSearcher {

	public List<Path> searchFiles(Path path, String filePattern, boolean recursive) {
		return StreamSupport.stream(FileStreamIterable.searchFiles(path, filePattern, true, recursive).spliterator(), false).collect(Collectors.toList());
	}
	public Stream<Path> iterateFiles(Path path, String filePattern, boolean recursive) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(filePattern);
	    return asStream(new FilesIterator(path, recursive)).filter(p -> matcher.matches(p));
	}
	public Stream<Path> iterateFiles(Path path, String filePattern, String excludeFilePattern, boolean recursive) {
		PathMatcher excludeMatcher = FileSystems.getDefault().getPathMatcher(excludeFilePattern);
	    return iterateFiles(path, filePattern, recursive).filter(p -> !excludeMatcher.matches(p));
	}
	public class FilesIterator implements Iterator<Path> {
    	Deque<Path> stack = new ArrayDeque<Path>();
    	Iterator<Path> iterator;
    	boolean recursive;
    	Path currentPath;
    	DirectoryStream<Path> files; 
		public FilesIterator(Path root, boolean recursive) {
			this.recursive = recursive;
			initStream(root);
		}
		private void initStream(Path path) {
			try {
				if (files != null) {
					try {
						files.close();
					} catch (IOException e) {
						throw new RuntimeException("Cannot close files reader", e);
					}
					files = null;
				}
				files = Files.newDirectoryStream(path);
				iterator = files.iterator();
				currentPath = path;
			} catch (IOException e) {
				throw new RuntimeException("Cannot iterate files", e);
			}
		}
		@Override
		public boolean hasNext() {
			if (iterator.hasNext()) return true;
			else if (recursive) {
				try {
					DirectoryStream<Path> dirs = Files.newDirectoryStream(currentPath);
					try {
						asStream(dirs.iterator()).filter(p -> p.toFile().isDirectory()).forEach(p -> stack.addLast(p));
					} finally {
						dirs.close();
					}
					
				} catch (IOException e) {
					throw new RuntimeException("Cannot read sub dirs", e);
				}
				while (!stack.isEmpty()) {
					initStream(stack.removeFirst());
					if (iterator.hasNext()) return true;
				}
			}
			if (files != null) {
				try {
					files.close();
				} catch (IOException e) {
					throw new RuntimeException("Cannot close files reader", e);
				}
				files = null;
			}
			return false;
		}

		@Override
		public Path next() {
			return iterator.next();
		}
		
	}
	@SuppressWarnings("hiding")
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

	@SuppressWarnings("hiding")
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
	
	public boolean fileContain(Path file, String pattern) {
		return fileContain(file, Pattern.compile(pattern));
    }
	
	public boolean fileContain(Path file, Pattern pattern) {
		try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if (pattern.matcher(line).matches()) return true;
		    }
		} catch (IOException e) {
			throw new RuntimeException("Cannot search file " + file, e);
		}
		return false;
	}
}
