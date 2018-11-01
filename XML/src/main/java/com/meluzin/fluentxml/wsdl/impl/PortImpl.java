package com.meluzin.fluentxml.wsdl.impl;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Port;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class PortImpl implements Port {
	private static final String PORT = "port";

	private String name;
	private ReferenceInfo binding;
	
	PortImpl(NodeBuilder portXml) {
		if (Wsdl.WSDL_NAMESPACE.equals(portXml.getNamespace()) && PORT.equals(portXml.getName())) {
			name = portXml.getAttribute("name");		
			binding = new ReferenceInfoImpl(portXml.getAttribute("binding"), portXml);
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+PORT + ", but it was {" + portXml.getNamespace() + "}" + portXml.getName());
		}
	}
	
	@Override
	public String getName() { 
		return name;
	}

	@Override
	public ReferenceInfo getBinding() {
		return binding;
	}

}
