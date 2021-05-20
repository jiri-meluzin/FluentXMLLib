package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Binding;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingMessage;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingOperation;
import com.meluzin.fluentxml.wsdl.Wsdl.MessageType;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;

public class BindingOperationImpl extends NamedEntityImpl<BindingOperation> implements BindingOperation {
	private static final String OPERATION = "operation";

	private Optional<String> soapAction = Optional.empty();
	private Binding binding;
	private Optional<BindingMessage> input = Optional.empty();
	private Optional<BindingMessage> output = Optional.empty();
	private List<BindingMessage> faults = new ArrayList<>();
	
	BindingOperationImpl(NodeBuilder bindingOperationXml, Binding binding) {
		super(bindingOperationXml, binding.getWsdl());
		if (Wsdl.WSDL_NAMESPACE.equals(bindingOperationXml.getNamespace()) && OPERATION.equals(bindingOperationXml.getName())) {
			this.binding = binding;		
			NodeBuilder soapActionEl = bindingOperationXml.searchFirstByName("operation", "http://schemas.xmlsoap.org/wsdl/soap/");
			NodeBuilder inputXml = bindingOperationXml.searchFirstByName("input");
			NodeBuilder outputXml = bindingOperationXml.searchFirstByName("output");
			if (soapActionEl != null) {
				soapAction = Optional.ofNullable(soapActionEl.getAttribute("soapAction"));
			}
			if (inputXml != null) {
				input = Optional.of(new BindingMessageImpl(inputXml, this, MessageType.Input));
			}
			if (outputXml != null) {
				output = Optional.of(new BindingMessageImpl(outputXml, this, MessageType.Output));
			}
			faults = bindingOperationXml.search(false, n -> "fault".equals(n.getName())).map(n -> new BindingMessageImpl(n, this, MessageType.Fault)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+OPERATION + ", but it was {" + bindingOperationXml.getNamespace() + "}" + bindingOperationXml.getName());
		}
	}
	@Override
	public Optional<BindingMessage> getInput() {
		return input;
	}

	@Override
	public Optional<BindingMessage> getOutput() {
		return output;
	}

	@Override
	public Collection<BindingMessage> getFaults() {
		return faults;
	}

	@Override
	public Optional<String> getSoapAction() {
		return soapAction;
	}
	@Override
	public Binding getBinding() {
		return binding;
	}
}
