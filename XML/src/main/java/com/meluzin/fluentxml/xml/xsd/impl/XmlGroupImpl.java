package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlGroup;

public class XmlGroupImpl extends BaseXmlNode<XmlGroup> implements XmlGroup {
	private String name;
	private String ref;
	private String refNamespace;
	private String minOccurs;
	private String maxOccurs;
	private XmlNode<?> child;
	public XmlGroupImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder group = parent.addChild("group").addAttribute("ref", getQualifiedName(getRefNamespace(), getRef(), parent)).addAttribute("name", name).addAttribute("minOccurs", minOccurs).addAttribute("maxOccurs", maxOccurs);
		if (child != null) {
			child.render(group);
		}
		return group;
	}

	@Override
	public XmlGroup loadFromNode(NodeBuilder node) {
		setMaxOccurs(node.getAttribute("maxOccurs"));
		setMinOccurs(node.getAttribute("minOccurs"));
		setName(node.getAttribute("name"));
		ReferenceInfo r = parseQualifiedName(node.getAttribute("ref"), node);
		setRef(r.getLocalName()).setRefNamespace(r.getNamespace());		
		node.getChildren().forEach(n -> {
			if ("sequence".equals(n.getName())) addSequence().loadFromNode(n);
			else if ("all".equals(n.getName())) addAll().loadFromNode(n);
			else if ("element".equals(n.getName())) addElement(n.getAttribute("name")).loadFromNode(n);
			else if ("annotation".equals(n.getName())) {
				setDocumentation(n.search("documentation").map(nn -> nn.getTextContent()).filter(nn -> nn != null).findFirst());
			}
		});
		
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public XmlGroup setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getRef() {
		return ref;
	}

	@Override
	public XmlGroup setRef(String ref) {
		this.ref = ref;
		return this;
	}
	@Override
	public String getRefNamespace() {
		return refNamespace;
	}
	@Override
	public XmlGroup setRefNamespace(String refNamespace) {
		this.refNamespace = refNamespace;
		return this;
	}

	@Override
	public String getMinOccurs() {
		return minOccurs;
	}

	@Override
	public XmlGroup setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}

	@Override
	public String getMaxOccurs() {
		return maxOccurs;
	}

	@Override
	public XmlGroup setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}

	@Override
	public XmlElement addElement(String name) {
		this.child = new XmlElementImpl(this).setName(name);
		return (XmlElement)child;
	}

	@Override
	public XmlSequence addSequence() {
		this.child = new XmlSequenceImpl(this);
		return (XmlSequence)child;
	}

	@Override
	public XmlAll addAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XmlNode<?> getChild() {
		return child;
	}

}
