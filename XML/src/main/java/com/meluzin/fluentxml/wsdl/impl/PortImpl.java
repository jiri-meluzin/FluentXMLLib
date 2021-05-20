package com.meluzin.fluentxml.wsdl.impl;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Port;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class PortImpl extends NamedEntityImpl<Port> implements Port {
	private static final String PORT = "port";

	private ReferenceInfo binding;
	
	PortImpl(NodeBuilder portXml, Wsdl wsdl) {
		super(portXml, wsdl);
		if (Wsdl.WSDL_NAMESPACE.equals(portXml.getNamespace()) && PORT.equals(portXml.getName())) {
			binding = new ReferenceInfoImpl(portXml.getAttribute("binding"), portXml);
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+PORT + ", but it was {" + portXml.getNamespace() + "}" + portXml.getName());
		}
	}
	

	@Override
	public ReferenceInfo getBinding() {
		return binding;
	}

}
