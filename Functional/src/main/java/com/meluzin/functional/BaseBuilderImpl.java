package com.meluzin.functional;


@SuppressWarnings("hiding")
public class BaseBuilderImpl<T extends BaseBuilder<T>> implements BaseBuilder<T> {
	private T returnRef;
	public BaseBuilderImpl(T returnRef) {
		this.returnRef = returnRef;
	}

	@Override
	public <I> T bulk(Iterable<I> items, BuilderAction<T, I> action) {
		for (I i: items) {
			action.exec((T)getReturnRef(), i);
		}
		return (T) getReturnRef();
	}
	public T getReturnRef() {
		return returnRef;
	}

}
