package com.meluzin.stream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.meluzin.functional.Log;

public class StreamUtils {
	private static Logger log = Log.get();
	public static String convertStreamToString(InputStream is)  {
		return convertStreamToString(is, StandardCharsets.UTF_8);
	}

	public static String convertStreamToString(InputStream is, Charset charset)  {
		if (is == null) return null;
		StringBuilder sb = new StringBuilder(2048); // Define a size if you have
													// an idea of it.
		char[] read = new char[2048]; // Your buffer size.		
		try(InputStreamReader ir = new InputStreamReader(is, charset)) {
			for (int i; -1 != (i = ir.read(read)); sb.append(read, 0, i))
				;
		} catch (IOException e) {
			throw new RuntimeException("Cannot convert stream to string", e);
		}
		return sb.toString();
	}
	public static String convertStreamToString(BufferedReader is)  {
		if (is == null) return null;
		StringBuilder sb = new StringBuilder(2048); // Define a size if you have
													// an idea of it.
		char[] read = new char[2048]; // Your buffer size.		
		try {
			for (int i; -1 != (i = is.read(read)); sb.append(read, 0, i))
				;
		} catch (IOException e) {
			log.severe("Cannot convert stream to string (read until now: " + sb + ")");
			throw new RuntimeException("Cannot convert stream to string", e);
		}
		return sb.toString();
	}
	
	public static InputStream readFile(Path path) {
		try {
			return new FileInputStream(path.toFile());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find file " + path, e);
		}
	}
}
