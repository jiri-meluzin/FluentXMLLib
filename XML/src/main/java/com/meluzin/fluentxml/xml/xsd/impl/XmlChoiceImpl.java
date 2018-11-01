package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlChoice;

public class XmlChoiceImpl extends BaseXmlNode<XmlChoice> implements XmlChoice {
	private List<XmlNode<?>> elements = new ArrayList<>();
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
	@Override
	public XmlSequence addSequence() {
		XmlSequence sequence = new XmlSequenceImpl(this);
		elements.add(sequence);
		return sequence;
	}
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder choice = parent.
			addChild("choice");
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