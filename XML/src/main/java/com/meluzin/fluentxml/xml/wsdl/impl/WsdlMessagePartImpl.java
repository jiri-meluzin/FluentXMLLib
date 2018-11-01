package com.meluzin.fluentxml.xml.wsdl.impl;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart;
import com.meluzin.functional.BuilderAction;

public class WsdlMessagePartImpl implements WsdlMessagePart {
	private String name;
	private String type;
	private String typeNamespace;
	private boolean isPartElementRef = true;
	private WsdlMessage message;
	public WsdlMessagePartImpl(WsdlMessage message) {
		this.message = message;
	}
	@Override
	public XmlNode<?> getParent() {
		return message;
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlElement asElement() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAttribute asAttribute() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlComplexType asComplexType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSequence asSequence() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlChoice asChoice() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema asSchema() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.XmlExceptionType asException() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public XmlNode<?> getRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart loadFromNode(NodeBuilder node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQualifiedName(String namespace, String localName, NodeBuilder context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo parseQualifiedName(String qualifiedName, NodeBuilder context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchemaTargetNamespace() {
		return message.getSchemaTargetNamespace();
	}

	@Override
	public <I> com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart bulk(Iterable<I> items,
			BuilderAction<com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart, I> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getTypeNamespace() {
		return typeNamespace;
	}


	@Override
	public boolean isPartElementRef() {
		return isPartElementRef;
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart setPartElementRef(boolean isPartElementRef) {
		this.isPartElementRef = isPartElementRef;
		return this;
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart setTypeNamespace(String typeNamespace) {
		this.typeNamespace = typeNamespace;
		return this;
	}

}
