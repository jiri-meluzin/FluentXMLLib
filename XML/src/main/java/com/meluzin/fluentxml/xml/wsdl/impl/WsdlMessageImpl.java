package com.meluzin.fluentxml.xml.wsdl.impl;

import java.util.ArrayList;
import java.util.List;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage;
import com.meluzin.functional.BuilderAction;

public class WsdlMessageImpl implements WsdlMessage {
	private String name;
	private Wsdl wsdl;
	private List<WsdlMessagePart> parts = new ArrayList<>();
	public WsdlMessageImpl(Wsdl wsdl) {
		this.wsdl = wsdl;
	}
	@Override
	public List<com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart> getParts() {
		return parts;
	}
	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart getPartByName(String name) {
		return parts.stream().filter(p -> name == p.getName() || (name != null && name.equals(p.getName()))).findFirst().orElse(null);
	}
	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessagePart addPart(String name) {
		WsdlMessagePartImpl wsdlMessagePartImpl = new WsdlMessagePartImpl(this);
		parts.add(wsdlMessagePartImpl);
		return wsdlMessagePartImpl.setName(name);
	}
	@Override
	public com.meluzin.fluentxml.xml.xsd.XmlNode.Wsdl getWsdl() {
		return wsdl;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public WsdlMessage setName(String name) {
		this.name = name;
		return this;
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
	public com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage loadFromNode(NodeBuilder node) {
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
		return wsdl.getTargetNamespace();
	}

	@Override
	public <I> com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage bulk(Iterable<I> items,
			BuilderAction<com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage, I> action) {
		throw new UnsupportedOperationException();
	}

}
