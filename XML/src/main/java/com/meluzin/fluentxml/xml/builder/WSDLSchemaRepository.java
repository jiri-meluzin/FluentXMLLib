package com.meluzin.fluentxml.xml.builder;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Binding;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingMessage;
import com.meluzin.fluentxml.wsdl.Wsdl.BindingOperation;
import com.meluzin.fluentxml.wsdl.Wsdl.Message;
import com.meluzin.fluentxml.wsdl.Wsdl.Operation;
import com.meluzin.fluentxml.wsdl.Wsdl.Param;
import com.meluzin.fluentxml.wsdl.Wsdl.Part;
import com.meluzin.fluentxml.wsdl.Wsdl.PortType;
import com.meluzin.fluentxml.wsdl.Wsdl.Service;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class WSDLSchemaRepository extends BaseSchemaRepository<Wsdl> {
	public WSDLSchemaRepository(SchemaRepository schemaRepository, Collection<Wsdl> wsdls) {
		super(schemaRepository, wsdls);
	}

	public Stream<Wsdl.Service> findAllServices() {
		return getSchemas().stream().map(w -> w.getServices().stream()).flatMap(s -> s);
	}
	public Stream<Wsdl.Binding> findAllBindings() {
		return getSchemas().stream().map(w -> w.getBindings().stream()).flatMap(b -> b);
	}
	public Stream<Wsdl.Binding> findAllBindings(Service service) {
		return getSchemas().stream().map(w -> w.getBindings().stream()).flatMap(b -> b).
				filter(b -> service.getPorts().stream().anyMatch(p -> 
					p.getBinding().getLocalName().equals(b.getName()) && 
					p.getBinding().getNamespace().equals(b.getWsdl().getTargetNamespace()) 
				));
	}
	public Optional<Wsdl.PortType> findPortType(Binding binding) {
		return findAllPortTypes().filter(p -> p.getReferenceInfo().equals(binding.getType())).findAny();
	}
	public Stream<Wsdl.PortType> findAllPortTypes() {
		return getSchemas().stream().map(w -> w.getPortTypes().stream()).flatMap(p -> p);
	}
	public Optional<Wsdl.Message> findMessage(ReferenceInfo ref) {
		Optional<Wsdl> wsdl = getSchema(ref.getNamespace());
		if (wsdl.isPresent()) {
			return wsdl.get().getMessages().stream().filter(m -> m.getName().equals(ref.getLocalName())).findAny();
		}
		return Optional.empty();
	}
	public ReferenceInfo getMessageElement(Message message) {
		Optional<Part> part = message.getParts().stream().findAny();
		if (part.isPresent()) {
			if (part.get().getElement().isPresent()) {
				return part.get().getElement().get();	
			}
			else {
				throw new NoSuchElementException("Message " + message.getName() + " has no element in part " + part.get().getName());
			}
		}
		else {
			throw new NoSuchElementException("Message " + message.getName() + " has no part");
		}
	}
	public PortType getPortType(BindingMessage m) {
		return getPortType(m.getBindingOperation());
	}
	public PortType getPortType(BindingOperation m) {
		Optional<PortType> port = findPortType(m.getBinding());
		if (port.isPresent()) {
			return port.get();
		}		
		else {
			throw new NoSuchElementException("Cannot find portType: " + m.getBinding().getName());
		}
	}
	public Operation getOperation(BindingMessage m) {
		return getOperation(m.getBindingOperation());
	}
	public Operation getOperation(BindingOperation bindingOperation) {
		PortType port = getPortType(bindingOperation);
		Optional<Operation> op = port.getOperations().stream().filter(o -> o.getName().equals(bindingOperation.getName())).findAny();
		if (op.isPresent()) {
			return op.get();
		}
		else {
			throw new NoSuchElementException("Cannot find operation " + bindingOperation.getBinding().getReferenceInfo());
		}
	}
	public Part getPart(Message m) {		
		if (m.getParts().size() == 1) {
			return m.getParts().stream().findAny().get();
		}
		else {
			throw new NoSuchElementException("Message " + m.getName() + " does not have exactly one part");
		}
		
	}
	public Param getParam(BindingMessage m) {
		Operation operation = getOperation(m);
		Optional<Param> param = Optional.empty();
		switch (m.getMessageType()) {
		case Input:
			param = operation.getInput();
			break;
		case Output:
			param = operation.getOutput();
			break;
		case Fault:
			param = operation.getFaults().stream().filter(fp -> fp.getName().equals(m.getName())).findAny();
			break;
		}
		if (param.isPresent()) {
			return param.get();
		}
		else {
			throw new NoSuchElementException("Cannot find " + m.getMessageType() + " param in operation " + operation.getName());
		}
	}
	public Message getMessage(BindingMessage m) {
		Param param = getParam(m);
		return getMessage(param);		
	}
	public Message getMessage(Param param) {
		Optional<Message> message = findMessage(param.getMessage());
		if (message.isPresent()) {
			return message.get();
		}
		else {
			throw new NoSuchElementException("Cannot find " + param.getMessage() +" message");
		}		
	}
	public BindingMessage getTechnicalFaultBindingMessage(BindingOperation bindingOperation) {
		Optional<BindingMessage> technicalFaultParam = bindingOperation.getFaults().stream().filter(f -> f.getName().equals(Optional.of("TechnicalFault"))).findAny();
		if (technicalFaultParam.isPresent()) {
			return technicalFaultParam.get();
		}
		else {
			throw new NoSuchElementException("Cannot find TechnicalFault in operation " + bindingOperation.getName());
		}
	}
	public Message getTechnicalFaultMessage(BindingOperation bindingOperation) {
		return getTechnicalFaultMessage(getOperation(bindingOperation));
	}
	public Message getTechnicalFaultMessage(Operation operation) {
		Optional<Param> technicalFaultParam = operation.getFaults().stream().filter(f -> f.getName().equals(Optional.of("TechnicalFault"))).findAny();
		if (technicalFaultParam.isPresent()) {
			return getMessage(technicalFaultParam.get());
		}
		else {
			throw new NoSuchElementException("Cannot find TechnicalFault in operation " + operation.getName());
		}
	}
	public Stream<Wsdl.Message> findAllMessages() {
		return getSchemas().
				stream().
				map(w -> w.getMessages().stream()).
				flatMap(s -> s);
	}
	public Stream<Wsdl.Service> findAllServices(String namespace) {
		return getSchemas().
				stream().
				filter(w -> w.getTargetNamespace().equals(namespace)).
				map(w -> w.getServices().stream()).
				flatMap(s -> s);
	}
	public Optional<Wsdl.Binding> findBinding(String namespace, String bindingName) {
		return getSchemas().
				stream().
				filter(w -> w.getTargetNamespace().equals(namespace)).
				map(w -> w.getBindings().stream()).
				flatMap(b -> b).
				filter(b -> bindingName.equals(b.getName())).
				findAny();
	}
}
