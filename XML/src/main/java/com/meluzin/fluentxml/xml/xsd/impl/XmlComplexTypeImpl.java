package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlComplexType;
import com.meluzin.functional.Lists;


public class XmlComplexTypeImpl extends XmlTypeImpl<XmlComplexType> implements XmlComplexType {
	private boolean isBaseTypeSimple = false;
	private List<XmlNode<?>> elements = new ArrayList<>();
	private String contentType = "sequence"; // can be group, all, choice, sequence 
	//private List<XmlAttribute> attributes = new ArrayList<>();
	public XmlComplexTypeImpl(XmlNode<?> parent) {
		super(parent);			
	}
	public List<XmlNode<?>> getChildren() {
		return elements;
	}
	//@Override
	public List<XmlAttribute> getAttributes() {
		return elements.stream().filter(e -> e instanceof XmlAttribute).map(e -> e.asAttribute()).collect(Collectors.toList());
	}
	public XmlElement addElement(String name) {
		XmlElement el = new XmlElementImpl(this).setName(name);
		elements.add(el);
		return el;			
	}
	public XmlAttribute addAttribute(String name) {
		XmlAttribute el = new XmlAttributeImpl(this).setName(name);
		elements.add(el);
		return el;			
	}
	public XmlChoice addChoice() {
		XmlChoice el = new XmlChoiceImpl(this);
		elements.add(el);
		return el;			
	}
	@Override
	public XmlSequence addSequence() {
		XmlSequence el = new XmlSequenceImpl(this);
		elements.add(el);
		return el;
	}
	@Override
	public XmlAny addAny() {
		XmlAny any = new XmlAnyImpl(this);
		elements.add(any);
		return any;
	}
	@Override
	public XmlGroup addGroup() {
		XmlGroup group = new XmlGroupImpl(this);
		elements.add(group);
		return group;
	}
	@Override
	public XmlAll addAll() {
		XmlAll all = new XmlAllImpl(this);
		elements.add(all);
		return all;
	}
	@Override
	public String getContentType() {
		return contentType;
	}
	@Override
	public XmlComplexType setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder type = parent.addChild("complexType").addAttribute("name", getName());
		NodeBuilder childElements = type;
		NodeBuilder childElementsAttributes = type;
		if (getBaseType() != null) {				
			childElements =type.
				addChild(isBaseTypeSimple() ? "simpleContent":"complexContent").
					addChild("extension").addAttribute("base", getBaseType());
			childElementsAttributes = childElements;
		}
		if (!isBaseTypeSimple()) {
			childElements = childElements.addChild(contentType);
		}
		for (XmlNode<?> el : getChildren()) {
			if (el instanceof XmlAttribute) {
				// attributes are rendered bellow
			}
			else el.render(childElements);
		}
		childElementsAttributes.addChildren(getAttributes(), (a, n) -> {
			a.render(n);	
		});
		return type;
	}
	@Override
	public XmlComplexType loadFromNode(NodeBuilder node) {
		//setBaseType(node.getAttribute("))
		setName(node.getAttribute("name"));
		NodeBuilder simpleContent = node.searchFirstByName("simpleContent");
		NodeBuilder complexContent = node.searchFirstByName("complexContent");
		NodeBuilder content = complexContent != null ? complexContent.searchFirstByName("restriction") != null ?  complexContent.searchFirstByName("restriction") : complexContent.searchFirstByName("extension") : simpleContent != null ? simpleContent.searchFirstByName("extension") : node;
		NodeBuilder sequence = content.searchFirst(n -> Lists.asList("group","all","choice","sequence").contains(n.getName())); //getChildren().size() == 1 ? content.getChildren().get(0) : null; 
		if (sequence != null) contentType = sequence.getName();
		if (content.getAttribute("base") != null) {
			ReferenceInfoImpl ref = new ReferenceInfoImpl(content.getAttribute("base"), content);
			setBaseType(ref.getLocalName());
			setBaseTypeNamespace(ref.getNamespace());
		}
		setBaseTypeSimple(simpleContent != null);
		content.search(n -> "attribute".equals(n.getName())).forEach(n -> {
			addAttribute(n.getAttribute("name")).loadFromNode(n);
		});
		if (sequence != null) {
			for (NodeBuilder child : sequence.getChildren()) {
				if ("element".equals(child.getName())) {
					addElement(child.getAttribute("name")).loadFromNode(child);
				}
				else if ("choice".equals(child.getName())) {
					addChoice().loadFromNode(child);
				}
				else if ("sequence".equals(child.getName())) {
					addSequence().loadFromNode(child);
				}
				else if ("group".equals(child.getName())) {
					addGroup().loadFromNode(child);
				}
				else if ("any".equals(child.getName())) {
					addAny().loadFromNode(child);
				}		
				else if ("all".equals(child.getName())) {
					addAll().loadFromNode(child);
				}			
				else if (child.isTextNode()) {}
				else if ("annotation".equals(child.getName())) {}
				else throw new RuntimeException("not support node name: " + node);
			}
		}
		return this;
	}
	@Override
	public void duplicateInSchema(XmlComplexType targetElement, Set<String> changeToTargetNamespace) {
		targetElement.
			setName(getName()).
			setBaseType(getBaseType());
		for (XmlAttribute a: getAttributes()) {
			a.duplicateInSchema(targetElement.addAttribute(a.getName()), changeToTargetNamespace);			
		}
		for (XmlNode<?> node : getChildren()) {
			if (node instanceof XmlElement) {
				XmlElement e = (XmlElement)node;
				e.duplicateInSchema(targetElement.addElement(e.getName()), changeToTargetNamespace);
			}
			else if (node instanceof XmlChoice){
				XmlChoice c = (XmlChoice)node;
				c.duplicateInSchema(targetElement.addChoice(), changeToTargetNamespace);
			}
			else if (node instanceof XmlSequence){
				XmlSequence c = (XmlSequence)node;
				c.duplicateInSchema(targetElement.addSequence(), changeToTargetNamespace);
			}
			else if (node instanceof XmlAll){
				XmlAll c = (XmlAll)node;
				c.duplicateInSchema(targetElement.addAll(), changeToTargetNamespace);
			}
			else if (node instanceof XmlAttribute) {
				// nothing, attributes are processed above
			}
			else {
				throw new UnsupportedOperationException("Not supported XmlNode type: " + node);
			}
		}		
	}
	@Override
	public String toString() {
		return getName();
	}
	@Override
	public boolean isBaseTypeSimple() {
		return isBaseTypeSimple;
	}
	@Override
	public XmlComplexType setBaseTypeSimple(boolean isSimple) {
		this.isBaseTypeSimple = isSimple;
		return this;
	}
	@Override
	public String getMinOccurs() {
		throw new UnsupportedOperationException("XmlComplextType does not support minOccurs");
	}
	@Override
	public XmlComplexType setMinOccurs(String minOccurs) {
		throw new UnsupportedOperationException("XmlComplextType does not support minOccurs");
	}
	@Override
	public String getMaxOccurs() {
		throw new UnsupportedOperationException("XmlComplextType does not support maxOccurs");
	}
	@Override
	public XmlComplexType setMaxOccurs(String name) {
		throw new UnsupportedOperationException("XmlComplextType does not support maxOccurs");
	}
}