package com.meluzin.fluentxml.xml.builder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.meluzin.fluentxml.xml.builder.XmlBuilderSAXFactory.EndOfFileType;
import com.meluzin.fluentxml.xml.builder.XmlBuilderSAXFactory.Settings;
import com.meluzin.functional.T;
import com.meluzin.functional.T.V1;

public class SAXRenderHandler {
	private static final String LINE_SEPARATOR = System.lineSeparator();
	// private static final String NEW_LINE = "\n";
	private Settings settings;

	public SAXRenderHandler(Settings settings) {
		this.settings = settings;
	}

	public Settings getSettings() {
		return settings;
	}

	public void render(NodeBuilder root, OutputStream stream, boolean prettyStream)
			throws XMLStreamException, FactoryConfigurationError, IOException {
		try (OutputStreamWriter writer = new OutputStreamWriter(stream,getSettings().getCharset())) {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);

			try {
				if (getSettings().isBOM())
					writer.append(Settings.BOM_CHAR);
				if (getSettings().isSpaceAtAttributes()) {
					writer.write("<?xml version = \"1.0\" encoding = \""+getSettings().getCharset()+"\"?>");
				} else {
					xmlWriter.writeStartDocument(getSettings().getCharset(), "1.0");
				}
				if (prettyStream)
					xmlWriter.writeCharacters(LINE_SEPARATOR);
				render("", root, xmlWriter, prettyStream, writer);
				xmlWriter.writeEndDocument();
			} finally {
				xmlWriter.close();

			}
		}

	}

	private void render(String padding, NodeBuilder root, XMLStreamWriter xmlWriter, boolean prettyStream,
			OutputStreamWriter writer) {
		try {
			root.getComments().forEach(s -> {
				try {
					if (prettyStream) xmlWriter.writeCharacters(padding);
					xmlWriter.writeComment(s);
					if (prettyStream) xmlWriter.writeCharacters(LINE_SEPARATOR);
				} catch (XMLStreamException e) {
					throw new RuntimeException("Cannot render node: " + root.getXPath(), e);
				}
			});
			if (prettyStream)
				xmlWriter.writeCharacters(padding);
			
			if (root.hasChildren() || root.getTextContent() != null) {
				if (root.getPrefix() == null && root.getNamespace() == null) {
					xmlWriter.writeStartElement(root.getName());
				} else if (root.getPrefix() != null) {
					xmlWriter.writeStartElement(root.getPrefix(), root.getName(), root.getNamespace());
				} else {
					xmlWriter.setDefaultNamespace(root.getNamespace());
					xmlWriter.writeStartElement(root.getNamespace(), root.getName());
				}

				renderAttributes(padding, root, xmlWriter, writer);

				if (root.getTextContent() != null) {
					if (prettyStream)
						xmlWriter.writeCharacters(root.getTextContent().trim());
					else
						xmlWriter.writeCharacters(root.getTextContent());
				}
				if (prettyStream) {
					boolean firstIsElementNode = false;
					for (NodeBuilder n : root.getChildren()) {
						if (!n.isTextNode()) {
							firstIsElementNode = true;
							break;
						}
						if (n.getTextContent() != null && n.getTextContent().trim().length() > 0)
							break;
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
								} else
									xmlWriter.writeCharacters(c.getTextContent());
							}
						} catch (XMLStreamException e) {
							throw new RuntimeException("Cannot text content at " + c.getXPath(), e);
						}
					} else {
						render(padding + getSpacing(), c, xmlWriter, prettyStream, writer);
					}
				});
				if (prettyStream && root.getTextContent() == null)
					xmlWriter.writeCharacters(padding);
				xmlWriter.writeEndElement();
				newLineAfterElement(root, xmlWriter, prettyStream, writer);
			} else {
				if (root.getPrefix() == null && root.getNamespace() == null) {
					xmlWriter.writeEmptyElement(root.getName());
				} else if (root.getPrefix() != null) {
					xmlWriter.writeEmptyElement(root.getPrefix(), root.getName(), root.getNamespace());
				} else {
					xmlWriter.setDefaultNamespace(root.getNamespace());
					xmlWriter.writeEmptyElement(root.getNamespace(), root.getName());
				}
				renderAttributes(padding, root, xmlWriter, writer);
				if (getSettings().isSpaceInEmptyTag()) {
					writer.append(' ');
				}
				newLineAfterElement(root, xmlWriter, prettyStream, writer);
			}
		} catch (XMLStreamException | IOException e) {
			throw new RuntimeException("Cannot render node: " + root.getXPath(), e);
		}
	}

	private void newLineAfterElement(NodeBuilder root, XMLStreamWriter xmlWriter, boolean prettyStream, OutputStreamWriter writer)
			throws XMLStreamException {
		if (prettyStream && (getSettings().getNewLineAtTheEnd() != EndOfFileType.None && root.getParent() == null)) {
			xmlWriter.writeCharacters(LINE_SEPARATOR);
		} else if (prettyStream  && root.getParent() != null) {
			xmlWriter.writeCharacters(LINE_SEPARATOR);
		}
	}

	private String getSpacing() {
		if (getSettings().isOldFormatting()) return "   ";
		else return getSettings().getPadding();
	}

	private void renderAttributes(String padding, NodeBuilder root, XMLStreamWriter xmlWriter, OutputStreamWriter writer) {
		V1<Boolean> firstAttribute = T.V(true);
		T.V1<Integer> currentPosition = T.V((padding+"<"+root.getName()+" ").length());
		root.getFinalAttributes().forEach((name, value) -> {
			if (value != null && name != null) {
				try {
					String attributeRendered;
					String attributeSpace = getSettings().isSpaceAtAttributes() ? " " : "";
					if (name.contains(":")) {
						String[] nameParts = name.split(":");
						String pref = nameParts[0];
						attributeRendered = pref + ":" + nameParts[1] + attributeSpace+"=" + attributeSpace+"\""+ escapeAttChars(value) + "\"";
					} else {
						attributeRendered = name + (getSettings().isSpaceAtAttributes() ? " " : "")+ "=" + attributeSpace+"\""+ escapeAttChars(value) + "\"";
					}
					
					int length = (attributeRendered).length();
					boolean writePadding = !firstAttribute.getA() && getSettings().isOldFormatting() && length +currentPosition.getA()> 100;
					String attributePadding = writePadding ? createAttributeSpacing(padding, root) : " ";
					currentPosition.setA(length+currentPosition.getA()+1);
					
					writer.write(attributePadding + attributeRendered);
				} catch (IllegalArgumentException | IOException e) {
					throw new RuntimeException("Cannot render node: " + root.getXPath(), e);
				}
				firstAttribute.setA(false);
			}
		});
	}

	private String createAttributeSpacing(String padding, NodeBuilder root) {
		return LINE_SEPARATOR + padding + new String(new char[2 + (root.getPrefix() == null ? 0 : root.getPrefix().length() + 1) + root.getName().length()]).replace('\0', ' ');
	}

	public String escapeAttChars(String attributeValueToEncode)
			throws IllegalArgumentException {
		StringBuffer result = null;
		String escapedString = null;
		int substringStartIndex = 0;
		int length = attributeValueToEncode.length();
		char largerChars = (char)127;
		for (int index = 0; index < length; index++) {
			char currentChar = attributeValueToEncode.charAt(index);
			if (currentChar < '@' || currentChar > largerChars) {
				if (currentChar < '@') {
					switch (currentChar) {
					case '\000':
					case '\001':
					case '\002':
					case '\003':
					case '\004':
					case '\005':
					case '\006':
					case '\007':
					case '\b':
						illegalCharacter(currentChar);
					case '\t':
						escapedString = "&#x9;";
						break;
					case '\n':
						escapedString = "&#xA;";
						break;
					case '\013':
					case '\f':
						illegalCharacter(currentChar);
					case '\r':
						escapedString = "&#xD;";
						break;
					case '\016':
					case '\017':
					case '\020':
					case '\021':
					case '\022':
					case '\023':
					case '\024':
					case '\025':
					case '\026':
					case '\027':
					case '\030':
					case '\031':
					case '\032':
					case '\033':
					case '\034':
					case '\035':
					case '\036':
					case '\037':
						illegalCharacter(currentChar);
					case '&':
						escapedString = "&amp;";
						break;
					case '"':
						if (getSettings().isOldFormatting()) {
							escapedString = "&#34;";
						} else {
							escapedString = "&quot;";
						}
						break;
					case '<':
						escapedString = "&lt;";
						break;
					case '>':
						if (getSettings().isEscapeGreaterThanChar()) {
							escapedString = "&gt;";
						} else {
							escapedString = ">";
						}
						break;
					}
				}
				if (escapedString != null) {
					if (result == null)
						result = new StringBuffer(attributeValueToEncode.length() + escapedString.length());
					result.append(attributeValueToEncode.substring(substringStartIndex, index));
					result.append(escapedString);
					escapedString = null;
					substringStartIndex = index + 1;
				}
			}
		}
		if (result == null)
			return attributeValueToEncode;
		if (substringStartIndex < attributeValueToEncode.length())
			result.append(attributeValueToEncode.substring(substringStartIndex));
		return result.toString();
	}

	private static void illegalCharacter(char paramChar) {
		throw new IllegalArgumentException("Char 0x" + toHexString(paramChar) + " cannot be used in XML");
	}

	private static final char[] HEX_CHARACTERS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(int paramInt) {
		char[] arrayOfChar = new char[32];
		byte b = 32;
		while (true) {
			arrayOfChar[--b] = HEX_CHARACTERS[paramInt & 0xF];
			paramInt >>>= 4;
			if (paramInt == 0)
				return new String(arrayOfChar, b, 32 - b);
		}
	}
}
