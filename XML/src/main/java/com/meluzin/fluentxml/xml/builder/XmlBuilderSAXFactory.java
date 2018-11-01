package com.meluzin.fluentxml.xml.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

public class XmlBuilderSAXFactory extends XmlBuilderFactory {
	@Override
	public void renderNode(NodeBuilder node, boolean prettyPrint, OutputStream output) {
		try {
			new SAXRenderHandler().render(node, output, prettyPrint);
		} catch (XMLStreamException | FactoryConfigurationError | IOException e) {
			throw new RuntimeException("Could not render XML document", e);
		}
	}
	
	@Override
	public NodeBuilder parseDocument(InputStream input) {
		try (InputStream inputStream = input) {

		    SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    spf.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		    SAXParser saxParser = spf.newSAXParser();
		    SAXParserHandler handler = new SAXParserHandler();
		    return handler.load(this, input, saxParser);
		} catch (IOException | ParserConfigurationException | SAXException  e) {
			throw new RuntimeException("Could not parse XML document from stream", e);
		}
	}
}
