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
		return iterateFiles(path, filePattern, recursive).collect(Collectors.toList());
	}
	public Stream<Path> iterateFiles(Path path, String filePattern, boolean recursive) {
	    return StreamSupport.stream(FileStreamIterable.searchFiles(path, filePattern, true, recursive).spliterator(), false);
	}
	public Stream<Path> iterateFiles(Path path, String filePattern, String excludeFilePattern, boolean recursive) {
		PathMatcher excludeMatcher = FileSystems.getDefault().getPathMatcher(excludeFilePattern);
	    return iterateFiles(path, filePattern, recursive).filter(p -> !excludeMatcher.matches(p));
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
