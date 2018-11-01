package com.meluzin.fluentxml.xml.wsdl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.xsd.XmlNode.Wsdl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlOperation;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;
import com.meluzin.fluentxml.xml.xsd.XmlSchemaBuilder;

public class WsdlImpl  implements Wsdl {
	private Map<String, String> namespaces = new HashMap<>();
	private List<WsdlOperation> operations = new ArrayList<WsdlOperation>();
	private List<WsdlMessage> messages = new ArrayList<>();
	private List<XmlSchema> schemas = new ArrayList<>();
	private String namespace;
	private String name;
	private String portType;
	private String binding;
	private String port;
	private String portBinding;
	private String portBindingNamespace;
	public WsdlImpl() {
	}
	public WsdlImpl(NodeBuilder wsdlXml) {
		loadFromXml(wsdlXml);
	}
	@Override
	public WsdlOperation addOperation(String name) {
		WsdlOperation op = new WsdlOperationImpl(this, name);
		operations.add(op);
		return op;
	}

	@Override
	public List<WsdlOperation> getOperations() {
		return operations;
	}

	@Override
	public NodeBuilder render(XmlBuilderFactory factory) {
		// TODO Auto-generated method stub
		NodeBuilder definitions = factory.createRootElement("wsdl", "definitions").
			addNamespace("tns", getTargetNamespace()).
			addNamespace("http://schemas.xmlsoap.org/wsdl/").
			addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/").
			addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/").
			addAttribute("name", getName()).
			addAttribute("targetNamespace", getTargetNamespace());
		for (String prefix : namespaces.keySet()) {
			definitions.addNamespace(prefix, namespaces.get(prefix));
		}
		NodeBuilder types = definitions.addChild("types");
		for (XmlSchema xmlSchema : schemas) {
			xmlSchema.render(types);
		}
		definitions.addChildren(
				messages, 
				(m, p) -> 
					p.addChild("message").
						addAttribute("name", m.getName()).
						addChildren(m.getParts(), 
								(part, parent) -> 
									parent.addChild("part").
										addAttribute(part.isPartElementRef() ? "element" : "type", part.getType() == null ? null : definitions.getNamespacePrefix(part.getTypeNamespace()) + ":" + part.getType()).
										addAttribute("name", part.getName()))
		);
		NodeBuilder portType = definitions.addChild("portType").addAttribute("name", getName());

		for (WsdlOperation op : getOperations()) {
			portType.
				addChild("operation").addAttribute("name", op.getName()).
					addChild("documentation").
						addChild(op, op.getDocumentationRenderer()).
					getParent().
					addChild("input").addAttribute("message", "tns:"+ op.getInputMessage()).addAttribute("name", op.getInputMessageName()).getParent().
					addChild("output").addAttribute("message", "tns:"+ op.getOutputMessage()).addAttribute("name", op.getOutputMessageName()).getParent().
					addChildren(op.getFaults().entrySet(), (o, p) -> {
						p.addChild("fault").
							addAttribute("message", "tns:" + o.getValue().getName()).
							addAttribute("name", o.getKey());
					});
		}
		NodeBuilder binding = definitions.addChild("binding").
				addAttribute("name", getBinding()).
				addAttribute("type", "tns:"+getName());
		binding.addChild("soap","binding").addAttribute("style", "document").addAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
		for (WsdlOperation op : getOperations()) {
			binding.
				addChild("operation").addAttribute("name", op.getName()).
					addChild(op, op.getBindingRenderer()).
					addChild("input").addChild("soap","body").addAttribute("use", "literal").getParent().getParent().
					addChild("output").addChild("soap","body").addAttribute("use", "literal").getParent().getParent().
					addChildren(op.getFaults().entrySet(), (o, p) -> {
						p.addChild("fault").
							addAttribute("name", o.getKey()).
							addChild("soap","fault").
								addAttribute("name", o.getKey()).addAttribute("use", "literal");
					});
				
		}
		NodeBuilder service = definitions.addChild("service").addAttribute("name", getName());
		service.addChild("documentation");
		ReferenceInfoImpl portBinding = new ReferenceInfoImpl(getPortBindingNamespace(), getPortBinding());
		service.addChild("port").addAttribute("binding", portBinding.createQualifiedName(service)).addAttribute("name", getPort()).
			addChild("soap","address").addAttribute("location", "http://dummy");
		return definitions;
	}

	@Override
	public String getTargetNamespace() {		
		return namespace;
	}

	@Override
	public Wsdl setTargetNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Wsdl setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getPort() {
		return port;
	}
	@Override
	public Wsdl setPort(String port) {
		this.port = port;
		return this;
	}
	@Override
	public String getPortBinding() {
		return portBinding;
	}
	@Override
	public Wsdl setPortBinding(String portBinding) {
		this.portBinding = portBinding;
		return this;
	}
	@Override
	public String getPortBindingNamespace() {
		return portBindingNamespace;
	}
	
	@Override
	public Wsdl setPortBindingNamespace(String portBindingNamespace) {
		this.portBindingNamespace = portBindingNamespace;
		return this;
	}
	@Override
	public String getPortType() {
		return portType;
	}
	@Override
	public Wsdl setPortType(String portType) {
		this.portType = portType;
		return this;
	}
	@Override
	public List<XmlSchema> getSchemas() {
		return schemas;
	}

	@Override
	public Wsdl addSchema(XmlSchema schema) {
		schemas.add(schema);
		return this;
	}

	@Override
	public WsdlMessage addMessage(String name) {
		WsdlMessage msg = new WsdlMessageImpl(this).setName(name);
		messages.add(msg);
		return msg;
	}

	@Override
	public List<WsdlMessage> getMessages() {
		return messages;
	}
	
	@Override
	public Wsdl addNamespace(String prefix, String namespace) {
		this.namespaces.put(prefix, namespace);
		return this;
	}
	@Override
	public Map<String, String> getNamespaces() {
		return namespaces;
	}
	
	private void loadFromXml(NodeBuilder node) {
		node.search(true, n -> "schema".equals(n.getName()) && "http://www.w3.org/2001/XMLSchema".equals(n.getNamespace())).
			forEach(n -> addSchema(new XmlSchemaBuilder().loadFromNode(n)));
		node.search(true, n -> "message".equals(n.getName()) && "http://schemas.xmlsoap.org/wsdl/".equals(n.getNamespace())).
			forEach(n -> {
				WsdlMessage message = addMessage(n.getAttribute("name"));
				n.search(false, part -> "part".equals(part.getName())).forEach(part -> {
					ReferenceInfoImpl impl;
					if (part.getAttribute("element") != null) impl = new ReferenceInfoImpl(part.getAttribute("element"), part);
					else impl = new ReferenceInfoImpl(part.getAttribute("type"), part);
					message.addPart(part.getAttribute("name")).setPartElementRef(part.getAttribute("element") != null).setType(impl.getLocalName()).setTypeNamespace(impl.getNamespace());
				});
			});
		NodeBuilder portType = node.searchFirst(true, n -> "portType".equals(n.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(n.getNamespace()));
		NodeBuilder binding = node.searchFirst(true, n -> "binding".equals(n.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(n.getNamespace()));
		NodeBuilder service = node.searchFirst(true, n -> "service".equals(n.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(n.getNamespace()));
		if (binding != null) {
		binding.search(true, n -> "operation".equals(n.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(n.getNamespace())).
			forEach(n -> {
				WsdlOperation op = addOperation(n.getAttribute("name"));
				NodeBuilder defOp = portType.searchFirst(el -> "operation".equals(el.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(el.getNamespace()) && n.getAttribute("name").equals(el.getAttribute("name")));
				NodeBuilder input = defOp.searchFirst(el -> "input".equals(el.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(el.getNamespace()));
				NodeBuilder output = defOp.searchFirst(el -> "output".equals(el.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(el.getNamespace()));
				defOp.search(el -> "fault".equals(el.getName()) &&  "http://schemas.xmlsoap.org/wsdl/".equals(el.getNamespace())).forEach(f -> {
					String messageName = new ReferenceInfoImpl(f.getAttribute("message"), f).getLocalName();
					WsdlMessage msg = getMessages().stream().filter(m -> messageName.equals(m.getName())).findFirst().orElse(null);
					op.addFault(f.getAttribute("name"), msg);
				});
				op.setDeprecated(defOp.hasChild(true, c -> "deprecated".equals(c.getName())));
				if (input != null) {
					op.setInputMessage(new ReferenceInfoImpl(input.getAttribute("message"), input).getLocalName());
				}
				if (output != null) {
					op.setOutputMessage(new ReferenceInfoImpl(output.getAttribute("message"), output).getLocalName());
				}
				/*if (fault != null) {
					op.setFaultMessage(new ReferenceInfoImpl(fault.getAttribute("message"), fault).getLocalName());
				}*/
				NodeBuilder soapOp = n.searchFirst(el -> "operation".equals(el.getName()) && "http://schemas.xmlsoap.org/wsdl/soap/".equals(el.getNamespace()));
				if (soapOp != null) {
					op.setSoapAction(soapOp.getAttribute("soapAction")).setSoapStyle(soapOp.getAttribute("style"));
				}
			});
			setBinding(binding.getAttribute("name"));
		}

		setPortType(portType.getAttribute("name"));
		if (service != null) {
			setName(service.getAttribute("name"));
			NodeBuilder servicePort = service.searchFirstByName("port");
			setPort(servicePort.getAttribute("name"));
			ReferenceInfoImpl servicePortBinding = new ReferenceInfoImpl(servicePort.getAttribute("binding"), servicePort);
			setPortBinding(servicePortBinding.getLocalName());
			setPortBindingNamespace(servicePortBinding.getNamespace());
		}
		setTargetNamespace(node.getAttribute("targetNamespace"));
	}
	@Override
	public WsdlMessage getMessageByName(String name) {
		return getMessages().stream().filter(m -> name.equals(m.getName())).findFirst().orElse(null);
	}
	@Override
	public String getBinding() {
		return binding;
	}
	@Override
	public Wsdl setBinding(String binding) {
		this.binding = binding;
		return this;
	}
}