package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAttribute;

public class XmlAttributeImpl extends BaseXmlNode<XmlAttribute> implements XmlAttribute {
	private String ref;
	private String refNamespace;
	private String name;
	private String use;
	private String type;
	private String typeNamespace;
	private String fixed;
	private String defaultValue;
	private XmlSimpleType simpleType;
	public XmlAttributeImpl(XmlNode<?> parent) {
		super(parent);
	}
	public String getName() {
		return name;
	}
	public String getUse() {
		return use;
	}
	public String getType() {
		return type;
	}
	public String getTypeNamespace() {
		return typeNamespace;
	}
	public XmlAttribute setName(String name) {
		this.name = name;
		return this;
	}
	public XmlAttribute setUse(String use) {
		this.use = use;
		return this;
	}
	public XmlAttribute setType(String type) {
		this.type = type;
		return this;
	}
	
	@Override
	public XmlAttribute setTypeNamespace(String typeNamespace) {
		this.typeNamespace = typeNamespace;
		return this;
	}
	
	@Override
	public String getFixed() {
		return fixed;
	}
	
	@Override
	public XmlAttribute setFixed(String fixed) {
		this.fixed = fixed;
		return this;
	}
	
	@Override
	public XmlSimpleType getSimpleType() {
		return simpleType;
	}
	
	@Override
	public XmlSimpleType addSimpleType() {
		simpleType = new XmlSimpleTypeImpl(this);
		return simpleType;
	}
	
	public NodeBuilder render(NodeBuilder parent) {
		
		NodeBuilder el = parent.
			addChild("attribute").
				addAttribute("name", getName()).
				addAttribute("use", getUse()).
				addAttribute("ref", getQualifiedName(getRefNamespace(), getRef(), parent)).
				addAttribute("type", getQualifiedName(getTypeNamespace(), getType(), parent)).
				addAttribute("fixed", getFixed()).
				addAttribute("default", getDefault());
		if (simpleType != null) {
			simpleType.render(el);
		}
		return el;
	}
	@Override
	public String getRef() {
		return ref;
	}
	@Override
	public String getRefNamespace() {
		return refNamespace;
	}
	@Override
	public XmlAttribute setRef(String ref) {
		this.ref = ref;
		return this;
	}
	@Override
	public XmlAttribute setRefNamespace(String refNamespace) {
		this.refNamespace = refNamespace;
		return this;
	}
	@Override
	public XmlAttribute loadFromNode(NodeBuilder node) {
		setName(node.getAttribute("name"));
		setUse(node.getAttribute("use"));
		ReferenceInfo r = parseQualifiedName(node.getAttribute("ref"), node);
		setRef(r.getLocalName()).setRefNamespace(r.getNamespace());		
		ReferenceInfo t = parseQualifiedName(node.getAttribute("type"), node);
		setType(t.getLocalName()).setTypeNamespace(t.getNamespace());
		setFixed(node.getAttribute("fixed"));
		setDefault(node.getAttribute("default"));
		NodeBuilder simple = node.searchFirstByName("simpleType");
		if (simple != null) {
			addSimpleType().loadFromNode(simple);
		}

		setDocumentation(node.search(true, "documentation").map(n -> n.getTextContent()).filter(n -> n != null).findFirst());
		return this;
	}
	
	@Override
	public void duplicateInSchema(XmlAttribute targetElement, Set<String> changeToTargetNamespace) {
		String targetNamespace = targetElement.getRoot().asSchema().getTargetNamespace();
		String localTargetNamespace = getRoot().asSchema().getTargetNamespace();
		targetElement.
			setName(getName()).
			setUse(getUse()).
			setRef(getRef()).setRefNamespace(getRefNamespace() == null ? null : getRefNamespace().equals(localTargetNamespace) || changeToTargetNamespace.contains(getRefNamespace()) ? targetNamespace : getRefNamespace()).
			setType(getType()).setTypeNamespace(getTypeNamespace() == null ? null : getTypeNamespace().equals(localTargetNamespace) || changeToTargetNamespace.contains(getTypeNamespace()) ? targetNamespace : getTypeNamespace()).
			setFixed(getFixed()).
			setDefault(getDefault());
		if (simpleType != null) {
			simpleType.duplicateInSchema(targetElement.addSimpleType(), changeToTargetNamespace);
		}
	}
	@Override
	public String toString() {
		return getName() != null ? getName() : getRef();
	}
	@Override
	public String getDefault() {
		return defaultValue;
	}
	@Override
	public XmlAttribute setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
}