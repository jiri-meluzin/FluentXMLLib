package com.meluzin.fluentxml.xml.builder;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

final class SAXParserHandler extends DefaultHandler {
	private NodeBuilder current;
	private SAXParseException error;
	private SAXParseException fatalError;
	private XmlBuilderFactory factory;
	public NodeBuilder load(XmlBuilderFactory factory, InputStream input, SAXParser parser) throws SAXException, IOException {
		this.factory = factory;
		parser.parse(input, this);
		if (error != null) throw error;
		if (fatalError != null) throw fatalError;
		return current;
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (current != null && current.getTextContent() != null) {
			current.addTextChild(current.getTextContent());
			current.setTextContent(null);
		}
		if (current == null) {
			current = factory.createRootElement(localName);			
		}
		else {
			current = current.addChild(null, localName);
		}
		if (uri != null && uri.length() > 0) {
			String prefix = qName.contains(":") ? qName.split(":")[0] : null;
			if (!current.getAllNamespaces().containsValue(uri)) {
				if (prefix != null) current.addNamespace(prefix, uri);
				else current.addNamespace(uri);
			}
			current.setPrefix(prefix);
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			String val = attributes.getValue(i);
			String qn = attributes.getQName(i);
			String pref = qn.contains(":") ? qn.split(":")[0] : null;
			String name = qn.contains(":") ? qn.split(":")[1] : qn;
			if ("xmlns".equals(pref)) {
				current.addNamespace(name, val);
			}
			else current.addAttribute(pref, name, val);
		}
		super.startElement(uri, localName, qName, attributes);
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (current.hasParent()) {
			current = current.getParent();
		}
	}
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String str = new String(ch, start, length);
		if (current.hasChildren()) {
			current.addTextChild(str);
		}
		else {
			current.setTextContent(str);
		}
		super.characters(ch, start, length);
	}
	@Override
	public void error(SAXParseException e) throws SAXException {
		this.error = e;
		super.error(e);
	}
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		this.fatalError = e;
		super.fatalError(e);
	}
}