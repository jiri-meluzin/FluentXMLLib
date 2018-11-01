package com.meluzin.functional;

public class Utils {
	public static int compareIgnoreCase(String a, String b) {
		return a==b ? 0 : a == null ? 1 : b == null ? -1 : a.compareToIgnoreCase(b);
	}
	public static int compare(String a, String b) {
		return a==b ? 0 : a == null ? 1 : b == null ? -1 : a.compareTo(b);
	}
	public static boolean stringEndsWith(String what, String... endsWith) {
		if (what == null) return false;
		for (int i = 0; i < endsWith.length; i++) {
			String string = endsWith[i];
			if (what.endsWith(string)) return true;
		}
		return false;
	}
}
