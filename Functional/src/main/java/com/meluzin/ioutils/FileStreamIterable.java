package com.meluzin.ioutils;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.function.Predicate;


public class FileStreamIterable implements Iterable<Path> {
	private Predicate<Path> fileMatcher = p -> true;
	private boolean parallel = true;
	private Path start ;
	private boolean recursive = true;
	private FileStreamIterable(Path start, boolean parallel, Predicate<Path> fileMatcher, boolean recursive) {
		this.parallel = parallel;
		this.fileMatcher = fileMatcher;	
		this.start = start;
		this.recursive = recursive;
	}
	public static FileStreamIterable searchFiles(Path start, String pattern, boolean parallel, boolean recursive) {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(pattern);
		return searchFiles(start, pathMatcher::matches, parallel, recursive);
	}
	public static FileStreamIterable searchFiles(Path start, Predicate<Path> fileMatcher, boolean parallel, boolean recursive) {
		FileStreamIterable iterable = new FileStreamIterable(start, parallel, fileMatcher, recursive);
		return iterable;
	}
	@Override
	public Iterator<Path> iterator() {
		return new FileStreamIterator(start, parallel, fileMatcher, recursive);
	}
	
}