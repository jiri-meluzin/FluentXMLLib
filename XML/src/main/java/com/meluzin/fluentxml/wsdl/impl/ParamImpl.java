package com.meluzin.fluentxml.wsdl.impl;

import java.util.Optional;

import com.meluzin.fluentxml.wsdl.Wsdl.Param;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public class ParamImpl implements Param {
	
	private Optional<String> name;
	private ReferenceInfo message;
	
	ParamImpl(NodeBuilder paramXml) {
		name = Optional.ofNullable(paramXml.getAttribute("name"));		
		message = new ReferenceInfoImpl(paramXml.getAttribute("message"), paramXml);
	}
	
	@Override
	public Optional<String> getName() { 
		return name;
	}
	@Override
	public ReferenceInfo getMessage() {
		return message;
	}

}
