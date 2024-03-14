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
	private String maxOccurs;
	public XmlAllImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder all = parent.addChild("all").addAttribute("minOccurs", minOccurs).addAttribute("maxOccurs", maxOccurs);
		all.addChildren(elements, (e,n) -> e.render(n));
		return all;
	}

	@Override
	public XmlAll loadFromNode(NodeBuilder node) {
		minOccurs = node.getAttribute("minOccurs");
		maxOccurs = node.getAttribute("maxOccurs");
		for (NodeBuilder child : node.getChildren()) {
			if ("element".equals(child.getName())) {
				addElement(child.getAttribute("name")).loadFromNode(child);
			}
			else if ("any".equals(child.getName())) {
				addAny().loadFromNode(child);
			}
			else if ("group".equals(child.getName())) {
				addGroup().loadFromNode(child);
			}
			else if ("choice".equals(child.getName())) {
				addChoice().loadFromNode(child);
			}
			else if ("annotation".equals(child.getName())) {
				setDocumentation(child.search("documentation").map(n -> n.getTextContent()).filter(n -> n != null).findFirst());
			}
			else if (child.isTextNode()) {	// annotation are ignored
			}
			else throw new RuntimeException("not support node name: " + child);
		}
		return this;
	}

	@Override
	public XmlElement addElement(String name) {
		XmlElement e = new XmlElementImpl(this).setName(name);
		elements.add(e);
		return e;
	}
	@Override
	public XmlAny addAny() {
		XmlAny e = new XmlAnyImpl(this);
		elements.add(e);
		return e;
	}

	@Override
	public XmlChoice addChoice() {
		XmlChoice e = new XmlChoiceImpl(this);
		elements.add(e);
		return e;
	}

	@Override
	public XmlGroup addGroup() {
		XmlGroup e = new XmlGroupImpl(this);
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
	public String getMaxOccurs() {
		return maxOccurs;
	}
	@Override
	public XmlAll setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}

	@Override
	public void duplicateInSchema(XmlAll targetElement, Set<String> changeToTargetNamespace) {
		targetElement.setMinOccurs(getMinOccurs());
		targetElement.setMaxOccurs(getMaxOccurs());
		elements.forEach(e -> e.asElement().duplicateInSchema(targetElement.addElement(e.asElement().getName()), changeToTargetNamespace));
	}
}
