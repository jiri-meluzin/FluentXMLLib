package com.meluzin.functional;

@SuppressWarnings("hiding")
public interface BaseBuilder<T extends BaseBuilder<T>> {
	public <I> T bulk(Iterable<I> items, BuilderAction<T, I> action);
}
