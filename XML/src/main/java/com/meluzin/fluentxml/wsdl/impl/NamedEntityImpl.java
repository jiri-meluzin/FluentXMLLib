package com.meluzin.fluentxml.wsdl.impl;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.NamedEntity;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class NamedEntityImpl implements NamedEntity {
	private String name;
	private Wsdl wsdl;
	
	public NamedEntityImpl(String name, Wsdl wsdl) {
		this.name = name;
		this.wsdl = wsdl;		
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
