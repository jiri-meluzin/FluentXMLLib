package com.meluzin.fluentxml.xml.xsd.impl;

import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlType;

public abstract class XmlTypeImpl<T extends XmlType<T>> extends BaseXmlNode<T> implements XmlType<T>{

	private String name;
	private String baseType;
	private String baseTypeNamespace;
	public XmlTypeImpl(XmlNode<?> parent) {
		super(parent);
	}
	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setName(String name) {
		this.name = name != null ? name.trim() : null;
		return (T)this;
	}

	@Override
	public String getBaseType() {
		return baseType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setBaseType(String baseType) {
		this.baseType = baseType;
		return (T)this;
	}

	@Override
	public String getBaseTypeNamespace() {
		return baseTypeNamespace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setBaseTypeNamespace(String baseTypeNamespace) {
		this.baseTypeNamespace = baseTypeNamespace;
		return (T)this;
	}
}
