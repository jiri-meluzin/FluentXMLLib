package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAny;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlChoice;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlElement;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlGroup;

public class XmlChoiceImpl extends BaseXmlNode<XmlChoice> implements XmlChoice {
	private List<XmlNode<?>> elements = new ArrayList<>();
	private String minOccurs;
	private String maxOccurs;
	public XmlChoiceImpl(XmlNode<?> parent) {
		super(parent);
	}
	public List<XmlNode<?>> getChildren() {
		return elements;
	}
	public XmlElement addElement(String name) {
		XmlElement el = new XmlElementImpl(this);
		elements.add(el.setName(name));
		return el;
	}
	public String getMaxOccurs() {
		return maxOccurs;
	}
	public String getMinOccurs() {
		return minOccurs;
	}
	public XmlChoice setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}
	public XmlChoice setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}
	@Override
	public XmlSequence addSequence() {
		XmlSequence sequence = new XmlSequenceImpl(this);
		elements.add(sequence);
		return sequence;
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

	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder choice = parent.
			addChild("choice").
				addAttribute("minOccurs", getMinOccurs()).
				addAttribute("maxOccurs", getMaxOccurs());
		for (XmlNode<?> el : getChildren()) {
			el.render(choice);
		}
		return choice;
	}
	@Override
	public XmlGroup addGroup() {
		XmlGroup group = new XmlGroupImpl(this);
		elements.add(group);
		return group;
	}
	@Override
	public XmlChoice loadFromNode(NodeBuilder node) {
		setMaxOccurs(node.getAttribute("maxOccurs"));
		setMinOccurs(node.getAttribute("minOccurs"));
		for (NodeBuilder child : node.getChildren()) {
			if ("element".equals(child.getName())) {
				addElement(child.getAttribute("name")).loadFromNode(child);
			}
			else if ("sequence".equals(child.getName())) {
				addSequence().loadFromNode(child);
			}
			else if ("group".equals(child.getName())) {
				addGroup().loadFromNode(child);
			}
			else if ("annotation".equals(child.getName()) || child.isTextNode()) {
				// annotation are ignored
			}
			else throw new RuntimeException("not support node name: " + child);
		}
		return this;
	}
	@Override
	public void duplicateInSchema(XmlChoice targetElement, Set<String> changeToTargetNamespace) {
		for (XmlNode<?> node : getChildren()) {
			if (node instanceof XmlElement) {
				XmlElement e = (XmlElement)node;
				e.duplicateInSchema(targetElement.addElement(e.getName()), changeToTargetNamespace);
			}
			else {
				throw new UnsupportedOperationException("Not supported XmlNode type: " + node);
			}
		}		
	}
}