package com.meluzin.functional;

@SuppressWarnings("hiding")
public interface BuilderAction<T extends BaseBuilder<T>, I> {
	public void exec(T builder, I item); 
}
