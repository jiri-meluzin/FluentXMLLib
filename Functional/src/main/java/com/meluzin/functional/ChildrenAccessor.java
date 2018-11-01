package com.meluzin.functional;

@SuppressWarnings("hiding")
public interface ChildrenAccessor<T> {
	public Iterable<T> getChildren(T currentItem);
}