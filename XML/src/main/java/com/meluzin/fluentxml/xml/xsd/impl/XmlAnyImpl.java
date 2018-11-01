package com.meluzin.fluentxml.xml.xsd.impl;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAny;

public class XmlAnyImpl extends BaseXmlNode<XmlAny> implements XmlAny {
	private String minOccurs;
	private String maxOccurs;
	private String namespace;
	private String processContents;
	public XmlAnyImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		return parent.addChild("any").
				addAttribute("namespace", namespace).
				addAttribute("minOccurs", minOccurs).
				addAttribute("maxOccurs", maxOccurs).
				addAttribute("processContents", processContents);
	}

	@Override
	public XmlAny loadFromNode(NodeBuilder node) {
		setNamespace(node.getAttribute("namespace"));
		setMinOccurs(node.getAttribute("minOccurs"));
		setMaxOccurs(node.getAttribute("maxOccurs"));
		setProcessContents(node.getAttribute("processContents"));
		return this;
	}

	@Override
	public String getMinOccurs() {
		return minOccurs;
	}

	@Override
	public XmlAny setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}

	@Override
	public String getMaxOccurs() {
		return maxOccurs;
	}

	@Override
	public XmlAny setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public XmlAny setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	@Override
	public String getProcessContents() {
		return processContents;
	}

	@Override
	public XmlAny setProcessContents(String processContents) {
		this.processContents = processContents;
		return this;
	}

}
