package com.meluzin.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameNormalizer {
	public String normalize(String underscoreSeparatedName) {
		underscoreSeparatedName = underscoreSeparatedName.replaceAll("_+", "_").replaceAll("_*$", "").toLowerCase();
		Matcher m  = Pattern.compile("_(.)").matcher(underscoreSeparatedName);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, m.group().substring(1).toUpperCase());
		}
		m.appendTail(sb);
 		return sb.toString();
	}
}
