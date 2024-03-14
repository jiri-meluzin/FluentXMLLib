package com.meluzin.fluentxml.xml.xsd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;

public class XsdBuiltInTypes {
	private static XmlSchema xsd;// = new XmlSchemaBuilder("http://www.w3.org/2001/XMLSchema");
	private static XmlSchema xml;// = new XmlSchemaBuilder("http://www.w3.org/XML/1998/namespace");
	private static XmlSchema soap;// = new XmlSchemaBuilder("http://schemas.xmlsoap.org/soap/encoding/");
	public static Set<String> FLOAT_NUMBER_TYPES = new HashSet<String>(Arrays.asList("float", "double", "decimal"));
	public static Set<String> INT_NUMBER_TYPES = new HashSet<String>(Arrays.asList("integer", /*"nonNegativeInteger", "positiveInteger", "nonPositiveInteger", "negativeInteger", */ "byte", "int", "long", "short", "unsignedByte", "unsignedInt",
				"unsignedLong", "unsignedShort"));
	public static XmlSchema getBuiltInTypes() {
		return xsd;
	}
	public static XmlSchema getSOAPBuiltInTypes() {
		return soap;
	}
	public static XmlSchema getBuiltInXmlTypes() {
		return xml;
	}
	static {
		xml = new XmlSchemaBuilder().loadFromNode(new XmlBuilderFactory().parseDocument(XsdBuiltInTypes.class.getResourceAsStream("/xml.xsd")));
		xsd = new XmlSchemaBuilder().loadFromNode(new XmlBuilderFactory().parseDocument(XsdBuiltInTypes.class.getResourceAsStream("/XMLSchema.xsd")));
		xsd.addType("anySimpleType");
		soap = new XmlSchemaBuilder().loadFromNode(new XmlBuilderFactory().parseDocument(XsdBuiltInTypes.class.getResourceAsStream("/schemas.xmlsoap.org.xsd")));
	}
}
