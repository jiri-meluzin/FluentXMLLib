package com.meluzin.fluentxml.wsdl;

import java.util.Collection;
import java.util.Optional;

import com.meluzin.fluentxml.xml.builder.BaseSchema;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public interface Wsdl extends BaseSchema {
	public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
	public String getTargetNamespace();
	public String getName();
	
	public Collection<PortType> getPortTypes();
	public Collection<Message> getMessages();
	public Collection<Binding> getBindings();
	public Collection<Service> getServices();
	
	
	public interface NamedEntity<T extends NamedEntity<T>> {
		public String getName();
		public Optional<String> getDocumentation();
		public T setDocumentation(Optional<String> documentation); 
		public Wsdl getWsdl();	
		public ReferenceInfo getReferenceInfo();
	}
	public interface Message extends NamedEntity<Message>, XmlNode<Message> {
		public Collection<Part> getParts();
	}
	public interface Part extends XmlNode<Part>  {
		public String getName();
		public Optional<ReferenceInfo> getElement();
		public Optional<ReferenceInfo> getType();
	}
	public interface PortType extends NamedEntity<PortType> {
		public Collection<Operation> getOperations();
		
	}
	public interface Operation extends NamedEntity<Operation> {
		public Optional<Param> getInput();
		public Optional<Param> getOutput();
		public Collection<Param> getFaults();
	}
	public interface Param {
		public Optional<String> getName();
		public ReferenceInfo getMessage();		
	}
	public interface Binding extends NamedEntity<Binding> {
		public ReferenceInfo getType();		
		public Collection<BindingOperation> getOperations();
	}
	public interface BindingOperation extends NamedEntity<BindingOperation> {
		public Binding getBinding();
		public Optional<String> getSoapAction();
		public Optional<BindingMessage> getInput();
		public Optional<BindingMessage> getOutput();
		public Collection<BindingMessage> getFaults();
	}

	public interface BindingMessage {
		public Optional<String> getName();
		public BindingOperation getBindingOperation();
		public MessageType getMessageType();
	}
	public enum MessageType {
		Input,
		Output,
		Fault
	}
	public interface Service extends NamedEntity<Service> {
		public Collection<Port> getPorts();
	}
	public interface Port extends NamedEntity<Port> {
		public ReferenceInfo getBinding();		
	}
}
