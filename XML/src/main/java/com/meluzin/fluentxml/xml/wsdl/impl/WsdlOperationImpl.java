package com.meluzin.fluentxml.xml.wsdl.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.meluzin.fluentxml.xml.builder.NodeBuilder.AddChildFunctor;
import com.meluzin.fluentxml.xml.xsd.XmlNode.Wsdl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlMessage;
import com.meluzin.fluentxml.xml.xsd.XmlNode.WsdlOperation;

public class WsdlOperationImpl implements WsdlOperation {
	private Wsdl wsdl;
	private String targetNamespace;
	private String name;
	private String soapAction;
	private String soapStyle;
	private String inputMessage;
	private String inputMessageName;
	private String outputMessage;
	private String outputMessageName;
	/*private String faultMessage;
	private String faultMessageName;*/
	private Map<String, WsdlMessage> faults = new LinkedHashMap<String, WsdlMessage>(); 
	private boolean deprecated = false;
	private AddChildFunctor<WsdlOperation> documentationRenderer = (n, o) -> {};
	private AddChildFunctor<WsdlOperation> bindingRenderer = (o, n) -> n.addChild("soap", "operation").addAttribute("soapAction", o.getSoapAction());
	private Optional<String> documentation = Optional.empty();
	public WsdlOperationImpl(Wsdl wsdl, String name) {
		this.wsdl = wsdl;
		this.name = name;
	}
	@Override
	public WsdlOperation setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
		return this;
	}

	@Override
	public String getTargetNamespace() {
		return targetNamespace;
	}
	@Override
	public String getSoapAction() {
		return soapAction;
	}
	@Override
	public WsdlOperation setSoapAction(String soapAction) {
		this.soapAction = soapAction;
		return this;
	}
	@Override
	public Wsdl getWsdl() {
		return wsdl;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public WsdlOperation setInputMessage(String msg) {
		this.inputMessage = msg;
		return this;
	}
	@Override
	public String getInputMessage() {
		return inputMessage;
	}
	@Override
	public WsdlOperation setInputMessageName(String name) {
		this.inputMessageName = name;
		return this;
	}
	@Override
	public String getInputMessageName() {
		return inputMessageName;
	}
	@Override
	public WsdlOperation setOutputMessage(String msg) {
		this.outputMessage = msg;
		return this;
	}
	@Override
	public String getOutputMessage() {
		return outputMessage;
	}
	@Override
	public WsdlOperation setOutputMessageName(String name) {
		this.outputMessageName = name;
		return this;
	}
	@Override
	public String getOutputMessageName() {
		return outputMessageName;
	}
	/*@Override
	public WsdlOperation setFaultMessage(String msg) {
		this.faultMessage = msg;
		return this;
	}
	@Override
	public String getFaultMessage() {
		return faultMessage;
	}
	@Override
	public WsdlOperation setFaultMessageName(String name) {
		this.faultMessageName = name;
		return this;
	}
	@Override
	public String getFaultMessageName() {
		return faultMessageName;
	}*/
	@Override
	public WsdlOperation addFault(String fault, WsdlMessage msg) {
		faults.put(fault, msg);
		return this;
	}
	@Override
	public WsdlMessage addFault(String fault) {
		WsdlMessage msg = wsdl.addMessage(null);
		faults.put(fault, msg);
		return msg;
	}
	@Override
	public Map<String, WsdlMessage> getFaults() {
		return Collections.unmodifiableMap(faults);
	}
	@Override
	public AddChildFunctor<WsdlOperation> getDocumentationRenderer() {
		return documentationRenderer;
	}
	@Override
	public WsdlOperation setDocumentationRenderer(AddChildFunctor<WsdlOperation> documentation) {
		this.documentationRenderer = documentation;
		return this;
	}
	@Override
	public AddChildFunctor<WsdlOperation> getBindingRenderer() {
		return bindingRenderer;
	}
	@Override
	public WsdlOperation setBindingRenderer(
			AddChildFunctor<WsdlOperation> binding) {
		this.bindingRenderer = binding;
		return this;
	}
	@Override
	public boolean isDeprecated() {
		return deprecated;
	}
	@Override
	public WsdlOperation setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
		return this;
	}
	@Override
	public String getSoapStyle() {
		return soapStyle;
	}
	@Override
	public WsdlOperation setSoapStyle(String soapStyle) {
		this.soapStyle = soapStyle;
		return this;
	}
	@Override
	public WsdlOperation setDocumentation(Optional<String> documentation) {
		this.documentation = documentation;
		return this;
	}
	@Override
	public Optional<String> getDocumentation() {
		return documentation;
	}
}
