package com.meluzin.fluentxml.wsdl.impl;

import java.util.Optional;

import com.meluzin.fluentxml.wsdl.Wsdl.Part;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.functional.BuilderAction;

public class PartImpl implements Part {
	private static final String PART = "part";

	private String name;
	private Optional<ReferenceInfo> element = Optional.empty();
	private Optional<ReferenceInfo> type = Optional.empty();
	private Optional<String> documentation = Optional.empty();
	
	PartImpl(NodeBuilder partXml) {
		if (com.meluzin.fluentxml.wsdl.Wsdl.WSDL_NAMESPACE.equals(partXml.getNamespace()) && PART.equals(partXml.getName())) {
			name = partXml.getAttribute("name");		
			if (partXml.hasAttribute("element")) {
				element = Optional.of(new ReferenceInfoImpl(partXml.getAttribute("element"), partXml));
			}		
			if (partXml.hasAttribute("type")) {
				type = Optional.of(new ReferenceInfoImpl(partXml.getAttribute("type"), partXml));
			}
			this.documentation = partXml.search("annotation").map(a -> a.search("documentation").map(d -> d.getTextContent()).filter(n -> n != null)).flatMap(a -> a).findFirst();
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+com.meluzin.fluentxml.wsdl.Wsdl.WSDL_NAMESPACE+"}"+PART + ", but it was {" + partXml.getNamespace() + "}" + partXml.getName());
		}
	}
	@Override
	public Optional<String> getDocumentation() {
		return documentation;
	}
	@Override
	public Part setDocumentation(Optional<String> documentation) {
		this.documentation = documentation;
		return this;
	}
	@Override
	public String getName() { 
		return name;
	}

	@Override
	public Optional<ReferenceInfo> getElement() {
		return element;
	}

	@Override
	public Optional<ReferenceInfo> getType() {
		return type;
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Part loadFromNode(NodeBuilder node) {
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
		throw new UnsupportedOperationException();
	}

	@Override
	public <I> Part bulk(Iterable<I> items, BuilderAction<Part, I> action) {
		throw new UnsupportedOperationException();
	}

}
