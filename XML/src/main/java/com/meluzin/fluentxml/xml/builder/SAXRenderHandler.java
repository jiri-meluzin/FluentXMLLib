package com.meluzin.fluentxml.xml.builder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SAXRenderHandler {
	private static final String LINE_SEPARATOR = System.lineSeparator();
	//private static final String NEW_LINE = "\n";

	public void render(NodeBuilder root, OutputStream stream, boolean prettyStream)
			throws XMLStreamException, FactoryConfigurationError, IOException {
		try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			try {
				xmlWriter.writeStartDocument("UTF-8", "1.0");
				//xmlWriter.writeCharacters("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"true\"?>");
				if (prettyStream) xmlWriter.writeCharacters(LINE_SEPARATOR);
				render("", root, xmlWriter, prettyStream);
				xmlWriter.writeEndDocument();
				//xmlWriter.writeCharacters(System.lineSeparator());
			} finally {
				xmlWriter.close();
			}
		}

	}

	private void render(String padding, NodeBuilder root, XMLStreamWriter xmlWriter, boolean prettyStream) {
		try {
			if (prettyStream) xmlWriter.writeCharacters(padding);
			if (root.hasChildren() || root.getTextContent() != null) {
				if (root.getPrefix() == null && root.getNamespace() == null) {
					xmlWriter.writeStartElement(root.getName());
				} else if (root.getPrefix() != null ) {
					xmlWriter.writeStartElement(root.getPrefix(), root.getName(), root.getNamespace());
				} else {
					xmlWriter.setDefaultNamespace(root.getNamespace());
					xmlWriter.writeStartElement(root.getNamespace(), root.getName());
				}

				renderAttributes(root, xmlWriter);
				
				if (root.getTextContent() != null) {
					if (prettyStream) xmlWriter.writeCharacters(root.getTextContent().trim());
					else xmlWriter.writeCharacters(root.getTextContent());
				}
				if (prettyStream) {
					boolean firstIsElementNode = false;
					for (NodeBuilder n : root.getChildren()) {
						if (!n.isTextNode()) {
							firstIsElementNode = true;
							break;
						}
						if (n.getTextContent() != null && n.getTextContent().trim().length() > 0) break;
					}
					if (firstIsElementNode) {
						xmlWriter.writeCharacters(LINE_SEPARATOR);
					}
				}
				root.getChildren().forEach(c -> {
					if (c.isTextNode()) {
						try {
							if (c.getTextContent() != null) {
								if (prettyStream) {
									xmlWriter.writeCharacters(c.getTextContent().trim());
								}
								else xmlWriter.writeCharacters(c.getTextContent());
							}
						} catch (XMLStreamException e) {
							throw new RuntimeException("Cannot text content at " + c.getXPath(), e);
						}
					} else {
						render(padding + "  ", c, xmlWriter, prettyStream);
					}
				});
				if (prettyStream && root.getTextContent() == null) xmlWriter.writeCharacters(padding);
				xmlWriter.writeEndElement();
				if (prettyStream) xmlWriter.writeCharacters(LINE_SEPARATOR);
			} else {
				if (root.getPrefix() == null && root.getNamespace() == null) {
					xmlWriter.writeEmptyElement(root.getName());
				} else if (root.getPrefix() != null ) {
					xmlWriter.writeEmptyElement(root.getPrefix(), root.getName(), root.getNamespace());
				} else {
					xmlWriter.setDefaultNamespace(root.getNamespace());
					xmlWriter.writeEmptyElement(root.getNamespace(), root.getName());
				}
				renderAttributes(root, xmlWriter);
				if (prettyStream) xmlWriter.writeCharacters(LINE_SEPARATOR);
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException("Cannot render node: " + root.getXPath(), e);
		}
	}

	private void renderAttributes(NodeBuilder root, XMLStreamWriter xmlWriter) {
		root.getNamespaces().forEach((prefix, namespace) -> {
			try {
				xmlWriter.writeNamespace(prefix, namespace);
			} catch (Exception e) {
				throw new RuntimeException("Cannot render namespace " + prefix + ":" + namespace + " at " + root.getXPath(), e);
			}			
		});
		root.getPrefixedAttributes().forEach((k, v) -> {
			v.forEach((name, value) -> {
				try {
					if (value == null) {
						return;
					}
					if (k != null) {
						xmlWriter.writeAttribute(k, root.getNamespace(k), name, value);
					} else {
						xmlWriter.writeAttribute(name, value);
					}
				} catch (XMLStreamException e) {
					throw new RuntimeException("Cannot render attribute " + name + " at " + root.getXPath(), e);
				}
			});
		});
	}
}
