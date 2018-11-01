package com.meluzin.functional;

@SuppressWarnings("hiding")
public interface Function<T, V> {
	public V exec(T t);
}
