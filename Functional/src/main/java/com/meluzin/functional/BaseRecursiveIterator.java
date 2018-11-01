package com.meluzin.functional;

import java.util.Iterator;
import java.util.function.Predicate;

@SuppressWarnings("hiding")
public class BaseRecursiveIterator<T> implements Iterator<T> {
	private boolean recursive;
	private Predicate<T> criterium;
	private ChildrenAccessor<T> childrenAccessor;
	private T startItem;
	private T currentItem;
	int index = -1;
	int nextIndex = -1;
	boolean hasNext = false;
	boolean searched = false;
	boolean hasChildIterator = false;
	Iterator<T> currentItemIterator = null;
	Iterator<T> childIterator = null;
	public BaseRecursiveIterator(ChildrenAccessor<T> childrenAccessor, Predicate<T> criterium, boolean recursive, T startItem) {
		assert childrenAccessor != null : "ChildrenAccessor cannot be null";
		this.childrenAccessor = childrenAccessor;
		this.criterium = criterium;
		this.recursive = recursive;
		this.startItem = startItem;
		this.currentItem = startItem;
		if (!recursive) {
			this.startItem = null;
		}
	}
	protected T getCurrentItem() {
		return currentItem;
	}
	protected Iterator<T> getChildren() {
		return this.childrenAccessor.getChildren(getCurrentItem()).iterator();
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove is not support in search");
	}
	
	@Override
	public T next() {
		if (hasNext()) {
			index = nextIndex;
			searched = false;
			if (hasChildIterator) {							
				return childIterator.next();
			}
			else {
				return currentItem;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasNext() {
		if (!searched) {
			if (hasChildIterator && childIterator.hasNext()) {
				hasNext = true;								
			}
			else {
				hasChildIterator = false;
				hasNext = false;
				if (startItem != null && criterium.test(startItem)) {
					searched = true;
					hasNext = true;
					startItem = null;
					return true;
				}
				if (currentItemIterator == null) currentItemIterator = getChildren();
				while (currentItemIterator.hasNext()) {
					currentItem = currentItemIterator.next();
					if (recursive) {
						childIterator = new BaseRecursiveIterator<T>(childrenAccessor, criterium, recursive, currentItem);
						if (childIterator.hasNext()) {
							searched = true;
							hasNext = true;
							hasChildIterator = true;										
							break;
						}
					}
					if (criterium.test(currentItem)) {
						searched = true;
						hasNext = true;
						break;
					}
				}
			}
		}		
		return hasNext;
	}
}
