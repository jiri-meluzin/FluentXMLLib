package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAll;

public class XmlAllImpl extends BaseXmlNode<XmlAll> implements XmlAll {
	private List<XmlNode<?>> elements = new ArrayList<>();
	private String minOccurs;
	public XmlAllImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder all = parent.addChild("all").addAttribute("minOccurs", minOccurs);
		all.addChildren(elements, (e,n) -> e.render(n));
		return all;
	}

	@Override
	public XmlAll loadFromNode(NodeBuilder node) {
		minOccurs = node.getAttribute("minOccurs");
		node.search(n -> "element".equals(n.getName())).forEach(n -> addElement(n.getAttribute("name")).loadFromNode(n));
		return this;
	}

	@Override
	public XmlElement addElement(String name) {
		XmlElement e = new XmlElementImpl(this).setName(name);
		elements.add(e);
		return e;
	}

	@Override
	public List<XmlNode<?>> getChildren() {
		return elements;
	}
	@Override
	public String getMinOccurs() {
		return minOccurs;
	}
	@Override
	public XmlAll setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}

	@Override
	public void duplicateInSchema(XmlAll targetElement, Set<String> changeToTargetNamespace) {
		targetElement.setMinOccurs(getMinOccurs());
		elements.forEach(e -> e.asElement().duplicateInSchema(targetElement.addElement(e.asElement().getName()), changeToTargetNamespace));
	}

}
