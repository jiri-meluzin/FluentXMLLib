package com.meluzin.fluentxml.wsdl;

import java.util.Collection;
import java.util.Optional;

import com.meluzin.fluentxml.xml.builder.BaseSchema;
import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

/**
 * This interface represents a WSDL (Web Services Description Language) document.
 * It extends the BaseSchema interface and provides methods to access various components of the WSDL.
 */
public interface Wsdl extends BaseSchema {
	public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
	
	/**
	 * Gets the target namespace of the WSDL document.
	 * 
	 * @return The target namespace.
	 */
	public String getTargetNamespace();
	
	/**
	 * Gets the name of the WSDL document.
	 * 
	 * @return The name of the WSDL document.
	 */
	public String getName();
	
	/**
	 * Gets the collection of port types defined in the WSDL document.
	 * 
	 * @return The collection of port types.
	 */
	public Collection<PortType> getPortTypes();
	
	/**
	 * Gets the collection of messages defined in the WSDL document.
	 * 
	 * @return The collection of messages.
	 */
	public Collection<Message> getMessages();
	
	/**
	 * Gets the collection of bindings defined in the WSDL document.
	 * 
	 * @return The collection of bindings.
	 */
	public Collection<Binding> getBindings();
	
	/**
	 * Gets the collection of services defined in the WSDL document.
	 * 
	 * @return The collection of services.
	 */
	public Collection<Service> getServices();
	
	/**
	 * This interface represents a named entity in the WSDL document.
	 * It provides methods to get the name, documentation, and reference information of the entity.
	 *
	 * @param <T> The type of the named entity.
	 */
	public interface NamedEntity<T extends NamedEntity<T>> {
		/**
		 * Gets the name of the entity.
		 * 
		 * @return The name of the entity.
		 */
		public String getName();
		
		/**
		 * Gets the optional documentation of the entity.
		 * 
		 * @return The optional documentation.
		 */
		public Optional<String> getDocumentation();
		
		/**
		 * Sets the documentation of the entity.
		 * 
		 * @param documentation The documentation to set.
		 * @return The updated entity.
		 */
		public T setDocumentation(Optional<String> documentation); 
		
		/**
		 * Gets the WSDL document that contains the entity.
		 * 
		 * @return The WSDL document.
		 */
		public Wsdl getWsdl();	
		
		/**
		 * Gets the reference information of the entity.
		 * 
		 * @return The reference information.
		 */
		public ReferenceInfo getReferenceInfo();
	}
	
	/**
	 * This interface represents a message in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the parts of the message.
	 */
	public interface Message extends NamedEntity<Message>, XmlNode<Message> {
		/**
		 * Gets the collection of parts in the message.
		 * 
		 * @return The collection of parts.
		 */
		public Collection<Part> getParts();
	}
	
	/**
	 * This interface represents a part in the WSDL document.
	 * It extends the XmlNode interface and provides methods to get the name, element reference, and type reference of the part.
	 */
	public interface Part extends XmlNode<Part>  {
		/**
		 * Gets the name of the part.
		 * 
		 * @return The name of the part.
		 */
		public String getName();
		
		/**
		 * Gets the optional element reference of the part.
		 * 
		 * @return The optional element reference.
		 */
		public Optional<ReferenceInfo> getElement();
		
		/**
		 * Gets the optional type reference of the part.
		 * 
		 * @return The optional type reference.
		 */
		public Optional<ReferenceInfo> getType();
	}
	
	/**
	 * This interface represents a port type in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the operations of the port type.
	 */
	public interface PortType extends NamedEntity<PortType> {
		/**
		 * Gets the collection of operations in the port type.
		 * 
		 * @return The collection of operations.
		 */
		public Collection<Operation> getOperations();
	}
	
	/**
	 * This interface represents an operation in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the input, output, and faults of the operation.
	 */
	public interface Operation extends NamedEntity<Operation> {
		/**
		 * Gets the optional input parameter of the operation.
		 * 
		 * @return The optional input parameter.
		 */
		public Optional<Param> getInput();
		
		/**
		 * Gets the optional output parameter of the operation.
		 * 
		 * @return The optional output parameter.
		 */
		public Optional<Param> getOutput();
		
		/**
		 * Gets the collection of fault parameters of the operation.
		 * 
		 * @return The collection of fault parameters.
		 */
		public Collection<Param> getFaults();
	}
	
	/**
	 * This interface represents a parameter in the WSDL document.
	 * It provides methods to get the optional name and message reference of the parameter.
	 */
	public interface Param {
		/**
		 * Gets the optional name of the parameter.
		 * 
		 * @return The optional name.
		 */
		public Optional<String> getName();
		
		/**
		 * Gets the message reference of the parameter.
		 * 
		 * @return The message reference.
		 */
		public ReferenceInfo getMessage();		
	}
	
	/**
	 * This interface represents a binding in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the type, operations, and SOAP transport of the binding.
	 */
	public interface Binding extends NamedEntity<Binding> {
		/**
		 * Gets the type reference of the binding.
		 * 
		 * @return The type reference.
		 */
		public ReferenceInfo getType();		
		
		/**
		 * Gets the collection of operations in the binding.
		 * 
		 * @return The collection of operations.
		 */
		public Collection<BindingOperation> getOperations();
		
		/**
		 * Gets the optional SOAP transport of the binding.
		 * 
		 * @return The optional SOAP transport.
		 */
		public Optional<String> getSoapTransport();
	}
	
	/**
	 * This interface represents a binding operation in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the binding, SOAP action, input, output, and faults of the binding operation.
	 */
	public interface BindingOperation extends NamedEntity<BindingOperation> {
		/**
		 * Gets the binding of the binding operation.
		 * 
		 * @return The binding.
		 */
		public Binding getBinding();
		
		/**
		 * Gets the optional SOAP action of the binding operation.
		 * 
		 * @return The optional SOAP action.
		 */
		public Optional<String> getSoapAction();
		
		/**
		 * Gets the optional input binding message of the binding operation.
		 * 
		 * @return The optional input binding message.
		 */
		public Optional<BindingMessage> getInput();
		
		/**
		 * Gets the optional output binding message of the binding operation.
		 * 
		 * @return The optional output binding message.
		 */
		public Optional<BindingMessage> getOutput();
		
		/**
		 * Gets the collection of fault binding messages of the binding operation.
		 * 
		 * @return The collection of fault binding messages.
		 */
		public Collection<BindingMessage> getFaults();
	}

	/**
	 * This interface represents a binding message in the WSDL document.
	 * It provides methods to get the optional name, binding operation, and message type of the binding message.
	 */
	public interface BindingMessage {
		/**
		 * Gets the optional name of the binding message.
		 * 
		 * @return The optional name.
		 */
		public Optional<String> getName();
		
		/**
		 * Gets the binding operation of the binding message.
		 * 
		 * @return The binding operation.
		 */
		public BindingOperation getBindingOperation();
		
		/**
		 * Gets the message type of the binding message.
		 * 
		 * @return The message type.
		 */
		public MessageType getMessageType();
	}
	
	/**
	 * This enum represents the message type of a binding message.
	 */
	public enum MessageType {
		Input,
		Output,
		Fault
	}
	
	/**
	 * This interface represents a service in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to access the ports of the service.
	 */
	public interface Service extends NamedEntity<Service> {
		/**
		 * Gets the collection of ports in the service.
		 * 
		 * @return The collection of ports.
		 */
		public Collection<Port> getPorts();
	}
	
	/**
	 * This interface represents a port in the WSDL document.
	 * It extends the NamedEntity interface and provides methods to get the binding reference of the port.
	 */
	public interface Port extends NamedEntity<Port> {
		/**
		 * Gets the binding reference of the port.
		 * 
		 * @return The binding reference.
		 */
		public ReferenceInfo getBinding();		
	}
}
