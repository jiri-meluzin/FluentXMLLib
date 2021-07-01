package com.meluzin.functional;

import java.util.HashMap;
import java.util.Map;

public class Maps<K, V> {

	private Map<K, V> m = new HashMap<>();

	private Maps(K key, V value) {
		this.m.put(key, value);
	}

	private Maps(Maps<K, V> orig) {
		this.m.putAll(orig.m);
	}

	public static <K, V> Maps<K, V> of(K key, V value) {
		return new Maps<>(key, value);
	}

	public Maps<K, V> and(K key, V value) {
		m.put(key, value);
		return this;
	}

	public Map<K, V> build() {
		return new HashMap<>(m);
	}

	public static <K, V> Map<K, V> empty() {
		return new HashMap<>();
	}
}