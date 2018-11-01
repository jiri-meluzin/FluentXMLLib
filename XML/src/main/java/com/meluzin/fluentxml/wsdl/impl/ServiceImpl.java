package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Port;
import com.meluzin.fluentxml.wsdl.Wsdl.Service;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;

public class ServiceImpl extends NamedEntityImpl implements Service {
	private static final String SERVICE = "service";
	
	private Collection<Port> ports = new ArrayList<>();
	
	ServiceImpl(NodeBuilder serviceXml, Wsdl wsdl) {
		super(serviceXml.getAttribute("name"), wsdl);
		if (Wsdl.WSDL_NAMESPACE.equals(serviceXml.getNamespace()) && SERVICE.equals(serviceXml.getName())) {
			ports = serviceXml.search(false, n -> "port".equals(n.getName())).map(n -> new PortImpl(n)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+SERVICE + ", but it was {" + serviceXml.getNamespace() + "}" + serviceXml.getName());
		}
	}
	@Override
	public Collection<Port> getPorts() {
		return ports;
	}

}
