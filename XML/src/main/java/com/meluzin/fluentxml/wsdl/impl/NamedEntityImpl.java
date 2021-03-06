package com.meluzin.fluentxml.wsdl.impl;

import java.util.Optional;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.NamedEntity;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class NamedEntityImpl<T extends NamedEntity<T>> implements NamedEntity<T> {
	private String name;
	private Wsdl wsdl;
	private Optional<String> documentation; 
	
	public NamedEntityImpl(String name, Optional<String> documentation, Wsdl wsdl) {
		this.name = name;
		this.documentation = documentation;
		this.wsdl = wsdl;		
	}
	
	protected NamedEntityImpl(NodeBuilder wsdlFragment, Wsdl wsdl) {
		this(
			wsdlFragment.getAttribute("name"), 
			wsdlFragment.
				search("documentation").
				filter(t -> t.getTextContent() != null).
				map(t -> t.getTextContent()).
				findAny(), 
			wsdl);
	}
	@SuppressWarnings("unchecked")
	@Override
	public T setDocumentation(Optional<String> documentation) {
		this.documentation = documentation;
		return (T)this;
	}
	
	@Override
	public Optional<String> getDocumentation() {
		return documentation;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Wsdl getWsdl() {
		return wsdl;
	}

	@Override
	public ReferenceInfo getReferenceInfo() {
		return new ReferenceInfoImpl(getWsdl().getTargetNamespace(), getName());
	}
	
	@Override
	public String toString() {
		return getReferenceInfo().toString();
	}

}
