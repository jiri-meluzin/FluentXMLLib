package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Binding;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingOperation;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class BindingImpl extends NamedEntityImpl implements Binding {
	private static final String BINDING = "binding";
	private ReferenceInfo type;
	private Collection<BindingOperation> operations = new ArrayList<>();
	
	
	BindingImpl(NodeBuilder bindingXml, Wsdl wsdl) {
		super(bindingXml.getAttribute("name"), wsdl);
		if (Wsdl.WSDL_NAMESPACE.equals(bindingXml.getNamespace()) && BINDING.equals(bindingXml.getName())) {
			type = new ReferenceInfoImpl(bindingXml.getAttribute("type"), bindingXml);		

			operations = bindingXml.search(false, n -> "operation".equals(n.getName())).map(n -> new BindingOperationImpl(n, this)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+BINDING + ", but it was {" + bindingXml.getNamespace() + "}" + bindingXml.getName());
		}
	}
	@Override
	public ReferenceInfo getType() {
		return type;
	}

	@Override
	public Collection<BindingOperation> getOperations() {
		return operations;
	}

}
