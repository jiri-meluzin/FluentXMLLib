package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlExceptionType;


public class XmlExceptionTypeImpl extends BaseXmlNode<XmlExceptionType> implements XmlExceptionType  {
	private String name;
	private String elementName;
	private String baseType;
	private List<XmlNode<?>> elements = new ArrayList<>();
	public XmlExceptionTypeImpl(XmlNode<?> parent) {
		super(parent);			
	}
	public String getBaseType() {
		return baseType;
	}
	public List<XmlNode<?>> getChildren() {
		return elements;
	}
	public String getName() {
		return name;
	}
	public XmlExceptionTypeImpl setBaseType(String baseType) {
		this.baseType = baseType;
		return this;
	}
	@Override
	public String getElementName() {
		return elementName;
	}
	@Override
	public XmlExceptionType setElementName(String name) {
		this.elementName = name;
		return this;
	}
	public XmlExceptionTypeImpl setName(String name) {
		this.name = name;
		return this;
	}
	public XmlElement addElement(String name) {
		XmlElement el = new XmlElementImpl(this).setName(name);
		elements.add(el);
		return el;			
	}
	public XmlChoice addChoice() {
		XmlChoice el = new XmlChoiceImpl(this);
		elements.add(el);
		return el;			
	}
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder type = parent.addChild("complexType").addAttribute("name", getName());
		NodeBuilder childElements = type;
		if (getBaseType() != null) {				
			childElements =type.
				addChild("complexContent").
					addChild("extension").addAttribute("base", getBaseType());
		}
		childElements = childElements.addChild("sequence");
		for (XmlNode<?> el : getChildren()) {
			el.render(childElements);
		}
		if (elementName != null)
		parent.addChild("element").addAttribute("name", elementName).addAttribute("type", "tns:"+getName());
		return type;
	}
	@Override
	public XmlExceptionType loadFromNode(NodeBuilder node) {
		throw new UnsupportedOperationException();
	}
}