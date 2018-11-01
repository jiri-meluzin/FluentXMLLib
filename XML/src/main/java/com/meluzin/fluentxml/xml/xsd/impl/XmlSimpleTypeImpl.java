package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSimpleType;

public class XmlSimpleTypeImpl extends XmlTypeImpl<XmlSimpleType> implements
		XmlSimpleType {
	private List<String> enumaration = new ArrayList<String>();
	private Integer minLength;
	private Integer maxLength;
	private String minInclusive;
	private String maxInclusive;
	private String minExclusive;
	private String maxExclusive;
	private Integer length;
	private String pattern;
	public XmlSimpleTypeImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder simpleType = parent.addChild("simpleType").
				addAttribute("name", getName());
		if (getBaseType() != null) {
			NodeBuilder restriction = simpleType.addChild("restriction").addAttribute("base", getQualifiedName(getBaseTypeNamespace(), getBaseType(), parent));
			for (String en : getEnumeration()) {
				restriction.addChild("enumeration").addAttribute("value", en);
			}
			if (getMinLength() != null) {
				restriction.addChild("minLength").addAttribute("value", getMinLength());
			}
			if (getMaxLength() != null) {
				restriction.addChild("maxLength").addAttribute("value", getMaxLength());
			}
			if (getLength() != null) {
				restriction.addChild("length").addAttribute("value", getLength());
			}
			if (getMinInclusive() != null) {
				restriction.addChild("minInclusive").addAttribute("value", getMinInclusive());
			}
			if (getMaxInclusive() != null) {
				restriction.addChild("maxInclusive").addAttribute("value", getMaxInclusive());
			}
			if (getMinExclusive() != null) {
				restriction.addChild("minExclusive").addAttribute("value", getMinExclusive());
			}
			if (getMaxExclusive() != null) {
				restriction.addChild("maxExclusive").addAttribute("value", getMaxExclusive());
			}
			if (getPattern() != null) {
				restriction.addChild("pattern").addAttribute("value", getPattern());
			}
		}
		return simpleType;
	}

	@Override
	public XmlSimpleType loadFromNode(NodeBuilder node) {
		setName(node.getAttribute("name"));
		NodeBuilder rest = node.searchFirst(t -> "restriction".equals(t.getName()));
		if (rest != null) {
			ReferenceInfo r = parseQualifiedName(rest.getAttribute("base"), node);
			setBaseType(r.getLocalName()).setBaseTypeNamespace(r.getNamespace());
			rest.
				search(t -> "enumeration".equals(t.getName())).
				forEach(n -> addEnumeration(n.getAttribute("value")));
			rest.
				search(t -> "minLength".equals(t.getName())).
				forEach(n -> setMinLength(Integer.parseInt(n.getAttribute("value"))));
			rest.
				search(t -> "maxLength".equals(t.getName())).
				forEach(n -> setMaxLength(Integer.parseInt(n.getAttribute("value"))));
			rest.
				search(t -> "length".equals(t.getName())).
				forEach(n -> setLength(Integer.parseInt(n.getAttribute("value"))));
			rest.
				search(t -> "minInclusive".equals(t.getName())).
				forEach(n -> setMinInclusive(n.getAttribute("value")));
			rest.
				search(t -> "maxInclusive".equals(t.getName())).
				forEach(n -> setMaxInclusive(n.getAttribute("value")));
			rest.
				search(t -> "minExclusive".equals(t.getName())).
				forEach(n -> setMinExclusive(n.getAttribute("value")));
			rest.
				search(t -> "maxExclusive".equals(t.getName())).
				forEach(n -> setMaxExclusive(n.getAttribute("value")));
			rest.
				search(t -> "pattern".equals(t.getName())).
				forEach(n -> setPattern(n.getAttribute("value")));
		}
		return this;
	}

	@Override
	public List<String> getEnumeration() {
		return this.enumaration;
	}

	@Override
	public XmlSimpleType addEnumeration(String element) {
		this.enumaration.add(element);
		return this;
	}

	@Override
	public XmlSimpleType addEnumeration(List<String> elements) {
		this.enumaration.addAll(elements);
		return this;
	}
	
	@Override
	public Integer getMaxLength() {
		return maxLength;
	}
	
	@Override
	public XmlSimpleType setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}
	@Override
	public Integer getMinLength() {
		return minLength;
	}
	@Override
	public XmlSimpleType setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}
	
	@Override
	public Integer getLength() {
		return length;
	}
	
	@Override
	public XmlSimpleType setLength(Integer length) {
		this.length = length;
		return this;
	}
	
	@Override
	public String getPattern() {
		return pattern;
	}
	@Override
	public XmlSimpleType setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
	
	@Override
	public String getMinInclusive() {
		return minInclusive;
	}
	@Override
	public XmlSimpleType setMinInclusive(String minInclusive) {
		this.minInclusive = minInclusive;
		return this;
	}
	
	@Override
	public String getMaxInclusive() {
		return maxInclusive;
	}
	@Override
	public XmlSimpleType setMaxInclusive(String maxInclusive) {
		this.maxInclusive = maxInclusive;
		return this;
	}
	
	@Override
	public String getMinExclusive() {
		return minExclusive;
	}
	@Override
	public XmlSimpleType setMinExclusive(String minExclusive) {
		this.minExclusive = minExclusive;
		return this;
	}
	
	@Override
	public String getMaxExclusive() {
		return maxExclusive;
	}
	@Override
	public XmlSimpleType setMaxExclusive(String maxExclusive) {
		this.maxExclusive = maxExclusive;
		return this;
	}

	@Override
	public void duplicateInSchema(XmlSimpleType targetElement, Set<String> changeToTargetNamespace) {
		String targetNamespace = targetElement.getRoot().asSchema().getTargetNamespace();
		String localTargetNamespace = getRoot().asSchema().getTargetNamespace();
		targetElement.
			setBaseType(getBaseType()).
			setBaseTypeNamespace(getBaseTypeNamespace() == null ? null : getBaseTypeNamespace().equals(localTargetNamespace)  || changeToTargetNamespace.contains(getBaseTypeNamespace())? targetNamespace : getBaseTypeNamespace()).
			setMaxLength(getMaxLength()).
			setMinLength(getMinLength()).
			setLength(getLength()).
			setPattern(getPattern()).
			setMinInclusive(getMinInclusive()).
			setMaxInclusive(getMaxInclusive()).
			setMinExclusive(getMinExclusive()).
			setMaxExclusive(getMaxExclusive()).
			addEnumeration(getEnumeration());		
	}
	@Override
	public String toString() {
		return getName();
	}
}
