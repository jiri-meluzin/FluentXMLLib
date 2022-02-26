package com.meluzin.fluentxml.wsdl.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl.BindingMessage;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingOperation;
import com.meluzin.fluentxml.wsdl.Wsdl.MessageType;
import com.meluzin.fluentxml.wsdl.soapextensionimpl.SoapWsdlExtensionImpl;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;

public class BindingMessageImpl implements BindingMessage {
	
	private Optional<String> name;
	private BindingOperation bindingOperation;
	private MessageType messageType;
	private List<Object> otherFields;
	
	BindingMessageImpl(NodeBuilder partXml, BindingOperation bindingOperation, MessageType messageType) {
		name = Optional.ofNullable(partXml.getAttribute("name"));
		this.bindingOperation = bindingOperation;
		this.messageType = messageType;
		this.otherFields = partXml.getChildren().stream().filter(n -> new SoapWsdlExtensionImpl().isSupported(n)).map(n -> new SoapWsdlExtensionImpl().build(n)).collect(Collectors.toList());
	}
	
	@Override
	public Optional<String> getName() { 
		return name;
	}
	
	@Override
	public BindingOperation getBindingOperation() {
		return bindingOperation;
	}
	@Override
	public MessageType getMessageType() {
		return messageType;
	}
	public List<Object> getOtherFields() {
		return otherFields;
	}

}
