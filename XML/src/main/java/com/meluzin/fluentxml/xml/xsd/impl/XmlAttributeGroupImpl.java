package com.meluzin.fluentxml.xml.xsd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAttributeGroup;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlChoice;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlElement;

public class XmlAttributeGroupImpl extends BaseXmlNode<XmlAttributeGroup> implements XmlAttributeGroup {
	private String name;
	private String ref;
	private String refNamespace;
	private List<XmlNode<?>> children = new ArrayList<>();
	public XmlAttributeGroupImpl(XmlNode<?> parent) {
		super(parent);
	}

	@Override
	public NodeBuilder render(NodeBuilder parent) {
		NodeBuilder group = parent.addChild("group").addAttribute("ref", getQualifiedName(getRefNamespace(), getRef(), parent)).addAttribute("name", name);
		children.forEach(c -> c.render(group));
		return group;
	}

	@Override
	public XmlAttributeGroup loadFromNode(NodeBuilder node) {
		setName(node.getAttribute("name"));
		ReferenceInfo r = parseQualifiedName(node.getAttribute("ref"), node);
		setRef(r.getLocalName()).setRefNamespace(r.getNamespace());		
		node.getChildren().forEach(n -> {
			if ("attribute".equals(n.getName())) addAttribute().loadFromNode(n);
			else if ("attributeGroup".equals(n.getName())) addAttributeGroup().loadFromNode(n);
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
	public XmlAttributeGroup setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getRef() {
		return ref;
	}

	@Override
	public XmlAttributeGroup setRef(String ref) {
		this.ref = ref;
		return this;
	}
	@Override
	public String getRefNamespace() {
		return refNamespace;
	}
	@Override
	public XmlAttributeGroup setRefNamespace(String refNamespace) {
		this.refNamespace = refNamespace;
		return this;
	}
	
	@Override
	public XmlAttribute addAttribute() {
		XmlAttribute a = new XmlAttributeImpl(this);
		children.add(a);
		return a;
	}
	@Override
	public XmlAttributeGroup addAttributeGroup() {
		XmlAttributeGroup a = new XmlAttributeGroupImpl(this);
		children.add(a);
		return a;
	}

	@Override
	public List<XmlNode<?>> getChildren() {
		return children;
	}

	@Override
	public void duplicateInSchema(XmlAttributeGroup targetElement, Set<String> changeToTargetNamespace) {

		String targetNamespace = targetElement.getRoot().asSchema().getTargetNamespace();
		String localTargetNamespace = getRoot().asSchema().getTargetNamespace();
		targetElement.
			setName(getName()).
			setRef(getRef()).setRefNamespace(getRefNamespace() == null ? null : getRefNamespace().equals(localTargetNamespace) || changeToTargetNamespace.contains(getRefNamespace()) ? targetNamespace : getRefNamespace());
		for (XmlNode<?> node : getChildren()) {
			if (node instanceof XmlAttribute) {
				XmlAttribute e = (XmlAttribute)node;
				e.duplicateInSchema(targetElement.addAttribute(), changeToTargetNamespace);
			}
			else if (node instanceof XmlAttributeGroup) {
				XmlAttributeGroup e = (XmlAttributeGroup)node;
				e.duplicateInSchema(targetElement.addAttributeGroup(), changeToTargetNamespace);
			}
			else {
				throw new UnsupportedOperationException("Not supported XmlNode type: " + node);
			}
		}	
	}

}
