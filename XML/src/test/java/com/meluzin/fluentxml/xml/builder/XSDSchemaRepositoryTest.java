package com.meluzin.fluentxml.xml.builder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAttribute;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlType;

public class XSDSchemaRepositoryTest {

	@Test
	public void testDefaultXMLAttributes() {
		XSDSchemaRepository repo = new XSDSchemaRepository();
		XmlAttribute findReferenceAttribute = repo.findReferenceAttribute("lang", null);
		assertNotNull(findReferenceAttribute);		
	}
	@Test
	public void testSoapEnc() {
		XSDSchemaRepository repo = new XSDSchemaRepository();
		XmlType<?> findReferenceAttribute = repo.findType("Array", "http://schemas.xmlsoap.org/soap/encoding/");
		assertNotNull(findReferenceAttribute);		
	}

}
