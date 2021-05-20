package com.meluzin.fluentxml.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.wsdl.Wsdl.Operation;
import com.meluzin.fluentxml.wsdl.Wsdl.Param;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;

public class OperationImpl extends NamedEntityImpl<Operation> implements Operation {
	private static final String OPERATION = "operation";

	private Optional<Param> input = Optional.empty();
	private Optional<Param> output = Optional.empty();
	private List<Param> faults = new ArrayList<>();
	
	OperationImpl(NodeBuilder operationXml, Wsdl wsdl) {
		super(operationXml, wsdl);
		if (Wsdl.WSDL_NAMESPACE.equals(operationXml.getNamespace()) && OPERATION.equals(operationXml.getName())) {
			NodeBuilder inputXml = operationXml.searchFirstByName("input");
			NodeBuilder outputXml = operationXml.searchFirstByName("output");
			if (inputXml != null) {
				input = Optional.of(new ParamImpl(inputXml));
			}
			if (outputXml != null) {
				output = Optional.of(new ParamImpl(outputXml));
			}
			faults = operationXml.search(false, n -> "fault".equals(n.getName())).map(n -> new ParamImpl(n)).collect(Collectors.toList());
		}
		else {
			throw new IllegalArgumentException("Xml elements must be {"+Wsdl.WSDL_NAMESPACE+"}"+OPERATION + ", but it was {" + operationXml.getNamespace() + "}" + operationXml.getName());
		}
	}
	
	@Override
	public Optional<Param> getInput() {
		return input;
	}

	@Override
	public Optional<Param> getOutput() {
		return output;
	}

	@Override
	public Collection<Param> getFaults() {
		return faults;
	}
}
