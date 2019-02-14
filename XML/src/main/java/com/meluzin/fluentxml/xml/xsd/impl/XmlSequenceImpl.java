package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSequence;

public class XmlSequenceImpl extends BaseXmlNode<XmlSequence> implements XmlSequence {
	private List<XmlNode<?>> elements = new ArrayList<>();
	public XmlSequenceImpl(XmlNode<?> parent) {
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
	public XmlChoice addChoice() {
		XmlChoice el = new XmlChoiceImpl(this);
		elements.add(el);
		return el;
	}
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder choice = parent.
			addChild("sequence");
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
	public XmlAny addAny() {
		XmlAny any = new XmlAnyImpl(this);
		elements.add(any);
		return any;
	}
	@Override
	public XmlSequence loadFromNode(NodeBuilder node) {
		for (NodeBuilder child : node.getChildren()) {
			if ("element".equals(child.getName())) {
				addElement(child.getAttribute("name")).loadFromNode(child);
			}
			else if ("choice".equals(child.getName())) {
				addChoice().loadFromNode(child);
			}
			else if ("group".equals(child.getName())) {
				addGroup().loadFromNode(child);
			}
			else if ("any".equals(child.getName())) {
				addAny().loadFromNode(child);
			}
			else if (child.isTextNode()) {}
			else if ("annotation".equals(child.getName())) {}
			else throw new RuntimeException("not support node name: " + node);
		}
		return this;
	}
	@Override
	public void duplicateInSchema(XmlSequence targetElement, Set<String> changeToTargetNamespace) {
		for (XmlNode<?> node : getChildren()) {
			if (node instanceof XmlElement) {
				XmlElement e = (XmlElement)node;
				e.duplicateInSchema(targetElement.addElement(e.getName()), changeToTargetNamespace);
			}
			else if (node instanceof XmlChoice) {
				XmlChoice e = (XmlChoice)node;
				e.duplicateInSchema(targetElement.addChoice(), changeToTargetNamespace);
			}
			else {
				throw new UnsupportedOperationException("Not supported XmlNode type: " + node);
			}
		}		
	}
}