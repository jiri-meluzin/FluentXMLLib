package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.Set;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.NodeBuilder.NodePredicate;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlElement;

public class XmlElementImpl extends BaseXmlNode<XmlElement> implements XmlElement {
	private String ref;
	private String refNamespace;
	private String name;
	private String type;
	private String typeNamespace;
	private String minOccurs;
	private String maxOccurs;
	private String nillable;
	private XmlComplexType complexType;
	private XmlSimpleType simpleType;
	public XmlElementImpl(XmlNode<?> parent) {
		super(parent);
	}
	public String getMaxOccurs() {
		return maxOccurs;
	}
	public String getMinOccurs() {
		return minOccurs;
	}
	public String getName() {
		return name;
	}
	public String getNillable() {
		return nillable;
	}
	public String getType() {
		return type;
	}
	public String getTypeNamespace() {
		return typeNamespace;
	}
	public XmlElement setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}
	public XmlElement setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}
	public XmlElement setName(String name) {
		this.name = name;
		return this;
	}
	public XmlElement setNillable(String nillable) {
		this.nillable = nillable;
		return this;
	}
	public XmlElement setType(String type) {
		this.type = type;
		return this;
	}
	@Override
	public XmlElement setTypeNamespace(String typeNamespace) {
		this.typeNamespace = typeNamespace;
		return this;
	}
	public NodeBuilder render(NodeBuilder parent) {
		
		NodeBuilder el = parent.
			addChild("element").
				addAttribute("name", getName()).
				addAttribute("ref", getQualifiedName(getRefNamespace(), getRef(), parent)).
				addAttribute("type", getQualifiedName(getTypeNamespace(), getType(), parent)).
				addAttribute("minOccurs", getMinOccurs()).
				addAttribute("maxOccurs", getMaxOccurs()).
				addAttribute("nillable", getNillable());
		if (complexType != null) complexType.render(el);
		if (simpleType != null) simpleType.render(el);
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
	public XmlElement setRef(String ref) {
		this.ref = ref;
		return this;
	}
	@Override
	public XmlElement setRefNamespace(String refNamespace) {
		this.refNamespace = refNamespace;
		return this;
	}
	@Override
	public XmlComplexType addComplexType() {
		complexType = new XmlComplexTypeImpl(this);
		return complexType;
	}
	@Override
	public XmlElement loadFromNode(NodeBuilder node) {
		setMaxOccurs(node.getAttribute("maxOccurs"));
		setMinOccurs(node.getAttribute("minOccurs"));
		setName(node.getAttribute("name"));
		setNillable(node.getAttribute("nillable"));
		ReferenceInfo r = parseQualifiedName(node.getAttribute("ref"), node);
		setRef(r.getLocalName()).setRefNamespace(r.getNamespace());		
		ReferenceInfo t = parseQualifiedName(node.getAttribute("type"), node);
		setType(t.getLocalName()).setTypeNamespace(t.getNamespace());
		NodeBuilder complexType = node.searchFirst(new NodePredicate() {			
			@Override
			public boolean test(NodeBuilder t) {
				return "complexType".equals(t.getName());
			}
		});
		if (complexType != null) {
			addComplexType().loadFromNode(complexType);
		}
		NodeBuilder simpleType = node.searchFirst(new NodePredicate() {			
			@Override
			public boolean test(NodeBuilder t) {
				return "simpleType".equals(t.getName());
			}
		});
		if (simpleType != null) {
			addSimpleType().loadFromNode(simpleType);
		}

		setDocumentation(node.search("annotation").map(n -> n.search("documentation")).flatMap(n -> n).map(n -> n.getTextContent()).filter(n -> n != null).findAny());

		return this;
	}
	@Override
	public XmlComplexType getComplexType() {
		return complexType;
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
	
	@Override
	public void duplicateInSchema(XmlElement targetElement, Set<String> changeToTargetNamespace) {
		String targetNamespace = targetElement.getRoot().asSchema().getTargetNamespace();
		String localTargetNamespace = getRoot().asSchema().getTargetNamespace();
		targetElement.
			setName(getName()).
			setMaxOccurs(getMaxOccurs()).
			setMinOccurs(getMinOccurs()).
			setNillable(getNillable()).
			setRef(getRef()).setRefNamespace(getRefNamespace() == null ? null : getRefNamespace().equals(localTargetNamespace) || changeToTargetNamespace.contains(getRefNamespace()) ? targetNamespace : getRefNamespace()).
			setType(getType()).setTypeNamespace(getTypeNamespace() == null ? null : getTypeNamespace().equals(localTargetNamespace) || changeToTargetNamespace.contains(getTypeNamespace()) ? targetNamespace : getTypeNamespace());
		if (getComplexType() != null) getComplexType().duplicateInSchema(targetElement.addComplexType(), changeToTargetNamespace);
		if (getSimpleType() != null) getSimpleType().duplicateInSchema(targetElement.addSimpleType(), changeToTargetNamespace);
	}
	@Override
	public String toString() {
		return getName() != null ? getName() : getRef();
	}
}