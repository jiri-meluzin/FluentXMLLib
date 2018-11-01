package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Operation;
import com.meluzin.fluentxml.wsdl.Wsdl.PortType;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;

public class PortTypeImpl extends NamedEntityImpl implements PortType {
	private static final String PORTTYPE = "portType";
	private Collection<Operation> operations = new ArrayList<>();
	
	PortTypeImpl(NodeBuilder portTypeXml, Wsdl wsdl) {
		super(portTypeXml.getAttribute("name"), wsdl);
		if (Wsdl.WSDL_NAMESPACE.equals(portTypeXml.getNamespace()) && PORTTYPE.equals(portTypeXml.getName())) {
			operations = portTypeXml.search(false, n -> "operation".equals(n.getName())).map(n -> new OperationImpl(n)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+PORTTYPE + ", but it was {" + portTypeXml.getNamespace() + "}" + portTypeXml.getName());
		}
	}
	
	@Override
	public Collection<Operation> getOperations() {
		return operations;
	}

}
