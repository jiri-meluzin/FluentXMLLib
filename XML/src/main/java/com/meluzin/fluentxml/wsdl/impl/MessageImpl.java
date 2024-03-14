package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl.Message;
import com.meluzin.fluentxml.wsdl.Wsdl.Part;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.functional.BuilderAction;

public class MessageImpl extends NamedEntityImpl<Message> implements Message {
	private static final String MESSAGE = "message";
	private List<Part> parts = new ArrayList<>();
	
	MessageImpl(NodeBuilder messageXml, com.meluzin.fluentxml.wsdl.Wsdl wsdl) {
		super(messageXml, wsdl);
		if (com.meluzin.fluentxml.wsdl.Wsdl.WSDL_NAMESPACE.equals(messageXml.getNamespace()) && MESSAGE.equals(messageXml.getName())) {
			parts = messageXml.search(false, n -> "part".equals(n.getName())).map(n -> new PartImpl(n)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+com.meluzin.fluentxml.wsdl.Wsdl.WSDL_NAMESPACE+"}"+MESSAGE + ", but it was {" + messageXml.getNamespace() + "}" + messageXml.getName());
		}
	}
	
	@Override
	public Collection<Part> getParts() {
		return parts;
	}

	@Override
	public XmlNode<?> getParent() {
		throw new UnsupportedOperationException();
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
		return this;
	}

	@Override
	public Message loadFromNode(NodeBuilder node) {
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
		return getWsdl().getTargetNamespace();
	}

	@Override
	public <I> Message bulk(Iterable<I> items, BuilderAction<Message, I> action) {
		throw new UnsupportedOperationException();
	}

}
