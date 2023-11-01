package com.meluzin.fluentxml.xml.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.meluzin.functional.T;

final class SAXParserHandler extends DefaultHandler implements LexicalHandler {
	private NodeBuilder current;
	private SAXParseException error;
	private SAXParseException fatalError;
	private XmlBuilderFactory factory;
	private List<String> comments = new ArrayList<>();
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
		if (comments.size() > 0) {
			current.getComments().addAll(comments);
			comments.clear();
		}
		Optional<T.V2<String, String>> nodePrefixNamespace = Optional.empty();
		if (uri != null && uri.length() > 0) {
			String prefix = qName.contains(":") ? qName.split(":")[0] : null;
			if (!current.getAllNamespaces().containsValue(uri)) {
				
				if (prefix != null) nodePrefixNamespace = Optional.of(T.V("xmlns:"+prefix, uri)); //current.addNamespace(prefix, uri);
				else nodePrefixNamespace = Optional.of(T.V("xmlns", uri));// current.addNamespace(uri);
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
			} else if (pref == null && "xmlns".equals(name)) {
				current.addNamespace(val);
			}
			else current.addAttribute(pref, name, val);
		}
		super.startElement(uri, localName, qName, attributes);
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (comments.size() > 0) {
			current.getComments().addAll(comments);
			comments.clear();
		}
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
		if (str.trim().length() > 0) {
			if (current.hasChildren()) {
				current.addTextChild(str);
			}
			else {
				current.setTextContent((current.getTextContent() == null ? "" : current.getTextContent()) + str.replace("\n", System.lineSeparator()));
			}
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

	@Override
	public void startDTD(String name, String publicId, String systemId) throws SAXException {}

	@Override
	public void endDTD() throws SAXException {}

	@Override
	public void startEntity(String name) throws SAXException {}

	@Override
	public void endEntity(String name) throws SAXException {}

	@Override
	public void startCDATA() throws SAXException {}

	@Override
	public void endCDATA() throws SAXException {}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		comments.add(new String(ch, start, length).replace("\n", System.lineSeparator()));
	}
}