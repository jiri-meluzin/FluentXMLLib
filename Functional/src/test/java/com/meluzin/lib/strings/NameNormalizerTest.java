package com.meluzin.lib.strings;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.meluzin.strings.NameNormalizer;

public class NameNormalizerTest {

	@Test
	public void testNormalize() {
		assertEquals("abcDef", new NameNormalizer().normalize("abc_def"));
		assertEquals("abcDef", new NameNormalizer().normalize("abc_def_"));
		assertEquals("abcDef", new NameNormalizer().normalize("abc_def__"));
		assertEquals("abcDef", new NameNormalizer().normalize("abc___def_"));
		assertEquals("abcDef", new NameNormalizer().normalize("abC___def_"));
	}

}
