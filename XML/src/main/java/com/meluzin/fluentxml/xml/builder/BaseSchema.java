package com.meluzin.fluentxml.xml.builder;

import java.util.Set;

public interface BaseSchema {
	public String getTargetNamespace();
	public BaseSchema merge(BaseSchema schema, Set<String> changeToTargetNamespace);
	public NodeBuilder render(XmlBuilderFactory xmlBuilderFactory);
	public SchemaType getSchemaType();
	public enum SchemaType {
		XSD,
		WSDL
	}
}
