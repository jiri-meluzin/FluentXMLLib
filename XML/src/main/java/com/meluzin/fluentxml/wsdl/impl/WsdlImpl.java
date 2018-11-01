package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.xml.builder.BaseSchema;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;

public class WsdlImpl implements Wsdl {
	private static final String DEFINITIONS = "definitions";
	private String targetNamespace;
	private String name;

	private List<PortType> portTypes = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
	private List<Binding> bindings = new ArrayList<>();
	private List<Service> services = new ArrayList<>();
	private NodeBuilder wsdlXml;
	
	public WsdlImpl(NodeBuilder wsdlXml) {
		if (Wsdl.WSDL_NAMESPACE.equals(wsdlXml.getNamespace()) && DEFINITIONS.equals(wsdlXml.getName())) {
			this.wsdlXml = wsdlXml;
			name = wsdlXml.getAttribute("name");
			targetNamespace = wsdlXml.getAttribute("targetNamespace");

			messages = wsdlXml.search(false, n -> "message".equals(n.getName())).map(n -> new MessageImpl(n, this)).collect(Collectors.toList());
			bindings = wsdlXml.search(false, n -> "binding".equals(n.getName())).map(n -> new BindingImpl(n, this)).collect(Collectors.toList());
			portTypes = wsdlXml.search(false, n -> "portType".equals(n.getName())).map(n -> new PortTypeImpl(n, this)).collect(Collectors.toList());
			services = wsdlXml.search(false, n -> "service".equals(n.getName())).map(n -> new ServiceImpl(n, this)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+WSDL_NAMESPACE+"}"+DEFINITIONS + ", but it was {" + wsdlXml.getNamespace() + "}" + wsdlXml.getName());
		}
	}
	@Override
	public String getTargetNamespace() {
		return targetNamespace;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<PortType> getPortTypes() {
		return portTypes;
	}

	@Override
	public Collection<Message> getMessages() {
		return messages;
	}

	@Override
	public Collection<Binding> getBindings() {
		return bindings;
	}

	@Override
	public Collection<Service> getServices() {
		return services;
	}

	@Override
	public BaseSchema merge(BaseSchema schema, Set<String> changeToTargetNamespace) {

		if (schema == null) {
			throw new IllegalArgumentException("Schema to merge is null");
		}
		if (schema instanceof WsdlImpl) {
			throw new IllegalArgumentException("Schema to merge is not an instance of WsdlImpl, but " + schema.getClass());
		}
		WsdlImpl schemaToAdd = (WsdlImpl)schema;
		NodeBuilder copy = wsdlXml.copy();
		schemaToAdd.wsdlXml.getChildren().forEach(child -> copy.appendChild(child.copy()));
		return new WsdlImpl(copy);
	}

	@Override
	public NodeBuilder render(XmlBuilderFactory xmlBuilderFactory) {
		return wsdlXml;
	}
	
	@Override
	public SchemaType getSchemaType() {
		return SchemaType.WSDL;
	}	
	

}
