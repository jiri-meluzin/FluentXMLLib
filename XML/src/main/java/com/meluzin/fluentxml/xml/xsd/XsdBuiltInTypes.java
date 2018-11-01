package com.meluzin.fluentxml.xml.xsd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.BaseSchema.SchemaType;
import com.meluzin.fluentxml.xml.builder.SchemaReference;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;

public class XsdBuiltInTypes {
	private static XmlSchema xsd = new XmlSchemaBuilder("http://www.w3.org/2001/XMLSchema");
	public static Set<String> FLOAT_NUMBER_TYPES = new HashSet<String>(Arrays.asList("float", "double", "decimal"));
	public static Set<String> INT_NUMBER_TYPES = new HashSet<String>(Arrays.asList("integer", /*"nonNegativeInteger", "positiveInteger", "nonPositiveInteger", "negativeInteger", */ "byte", "int", "long", "short", "unsignedByte", "unsignedInt",
				"unsignedLong", "unsignedShort"));
	public static XmlSchema getBuiltInTypes() {
		return xsd;
	}
	static {
		xsd.addSimpleType("string");
		xsd.addSimpleType("boolean");
		xsd.addSimpleType("float");  
		xsd.addSimpleType("double"); 
		xsd.addSimpleType("decimal");
		xsd.addSimpleType("dateTime");
		xsd.addSimpleType("duration");
		xsd.addSimpleType("hexBinary");
		xsd.addSimpleType("base64Binary");
		xsd.addSimpleType("anyURI");
		xsd.addSimpleType("ID");
		xsd.addSimpleType("IDREF");
		xsd.addSimpleType("ENTITY");
		xsd.addSimpleType("NOTATION");
		xsd.addSimpleType("normalizedString");
		xsd.addSimpleType("token");
		xsd.addSimpleType("language");
		xsd.addSimpleType("IDREFS");
		xsd.addSimpleType("ENTITIES");
		xsd.addSimpleType("NMTOKEN");
		xsd.addSimpleType("NMTOKENS");
		xsd.addSimpleType("Name");
		xsd.addSimpleType("QName");
		xsd.addSimpleType("NCName");
		xsd.addSimpleType("integer");
		xsd.addSimpleType("nonNegativeInteger");
		xsd.addSimpleType("positiveInteger");
		xsd.addSimpleType("nonPositiveInteger");
		xsd.addSimpleType("negativeInteger");
		xsd.addSimpleType("byte");
		xsd.addSimpleType("int");
		xsd.addSimpleType("long");
		xsd.addSimpleType("short");
		xsd.addSimpleType("unsignedByte");
		xsd.addSimpleType("unsignedInt");
		xsd.addSimpleType("unsignedLong");
		xsd.addSimpleType("unsignedShort");
		xsd.addSimpleType("date");
		xsd.addSimpleType("time");
		xsd.addSimpleType("gYearMonth");
		xsd.addSimpleType("gYear");
		xsd.addSimpleType("gMonthDay");
		xsd.addSimpleType("gDay");
		xsd.addSimpleType("gMonth");
		xsd.addType("anyType");
		xsd.addType("anySimpleType");
		xsd = new XmlSchemaBuilder(new SchemaReference("http://www.w3.org/2001/XMLSchema", null, null, xsd.render(new XmlBuilderFactory()), SchemaType.XSD));
	}
}
