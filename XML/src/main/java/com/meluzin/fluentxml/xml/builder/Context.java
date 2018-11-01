package com.meluzin.fluentxml.xml.builder;

public class Context<T> {
	private T value;
	public Context(T value) {
		this.value = value;
	}	
	public T getValue() {
		return value;
	}
	public Context<T> setValue(T newValue) {
		this.value = newValue;
		return this;
	}
}
