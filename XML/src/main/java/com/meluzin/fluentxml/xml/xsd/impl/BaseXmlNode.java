package com.meluzin.fluentxml.xml.xsd.impl;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.functional.BaseBuilder;
import com.meluzin.functional.BaseBuilderImpl;
import com.meluzin.functional.BuilderAction;

public abstract class BaseXmlNode<T extends BaseBuilder<T>> implements BaseBuilder<T>, XmlNode<T> {
	@SuppressWarnings("unchecked")
	private BaseBuilderImpl<T> baseBuilderImpl = new BaseBuilderImpl<T>((T)this);
	private XmlNode<?> parent;
	public BaseXmlNode(XmlNode<?> parent) {
		this.parent = parent;
	}
	@Override
	public XmlNode<?> getParent() {
		return parent;
	}

	@Override
	public XmlElementImpl asElement() {
		return (XmlElementImpl) this;
	}

	@Override
	public XmlAttributeImpl asAttribute() {
		return (XmlAttributeImpl) this;
	}
	@Override
	public XmlSequence asSequence() {
		return (XmlSequence)this;
	}
	@Override
	public XmlComplexType asComplexType() {
		return (XmlComplexType)this;
	}

	@Override
	public XmlChoice asChoice() {		
		return (XmlChoice)this;
	}
	@Override
	public XmlSchema asSchema() {		
		return (XmlSchema)this;
	}
	@Override
	public XmlExceptionType asException() {
		return (XmlExceptionType) this;
	}
	@Override
	public <I> T bulk(Iterable<I> items, BuilderAction<T, I> action) {
		return baseBuilderImpl.bulk(items, action);
	}
	@Override
	public XmlNode<?> getRoot() {
		return getParent() == null ? this : getParent().getRoot();
	}
	@Override
	public String getSchemaTargetNamespace() {
		return getRoot().asSchema().getTargetNamespace();
	}
	@Override
	public String getQualifiedName(String namespace, String localName, NodeBuilder context) {
		return new ReferenceInfoImpl(namespace, localName).createQualifiedName(context);
	}
	@Override
	public ReferenceInfo parseQualifiedName(final String qualifiedName, NodeBuilder context) {		
		return new ReferenceInfoImpl(qualifiedName, context);
	}
}
