package com.meluzin.fluentxml.xml.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.input.BOMInputStream;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.meluzin.fluentxml.xml.builder.impl.NodeBuilderImpl;
import com.meluzin.functional.Log;


public class XmlBuilderFactory {
	private boolean preserveWhitespace = false;
	

	
	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}
	public XmlBuilderFactory setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
		return this;
	}
	public NodeBuilder createRootElement(String name) {
		return createRootElement(null, name);
	}
	public NodeBuilder createRootElement(String prefix, String name) {
		return new NodeBuilderImpl(this, prefix, name);
	}
	public Document createDocument(NodeBuilder builder)  {
		DocumentBuilderFactory docFactory ;
		DocumentBuilder docBuilder ;
		Document document;
		docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		docFactory.setIgnoringElementContentWhitespace(true);
		try {			
			docBuilder = docFactory.newDocumentBuilder();
			document = docBuilder.newDocument();
			
			Element root = transformToDocument(builder, null, document);
			builder.getProcessingInstructions().forEach((key,value) -> {
				ProcessingInstruction pi = document.createProcessingInstruction(key, value);
				document.appendChild(pi);
			});;
			document.appendChild(root);
			//document.setXmlStandalone(true);
			return document;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could create document", e);
		}
	}
	public void renderDocument(Document document, StreamResult result, boolean prettyPrint) {
		renderDocument(document, result, prettyPrint, false);
	}
	public void renderDocument(Document document, StreamResult result, boolean prettyPrint, boolean omitXmlDeclaration) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			if (prettyPrint) {
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			if (omitXmlDeclaration) {
				transformer.setOutputProperty("omit-xml-declaration", "yes");
			}
			document.setXmlStandalone(true);
			DOMSource xmlSource = new DOMSource(document);
			transformer.transform(xmlSource, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Could not render document", e);
		} catch (TransformerException e) {
			throw new RuntimeException("Could not render document", e);
		}
	}
	/*public String renderDocument(Document document, boolean prettyPrint) {
		StringWriter writer = new StringWriter();
		renderDocument(document, new StreamResult(writer), prettyPrint);
		return writer.getBuffer().toString();
	}*/
	public void renderNode(NodeBuilder node, boolean prettyPrint, OutputStream output, boolean omitXmlDeclaration) {
		renderDocument(createDocument(node), new StreamResult(output), prettyPrint, omitXmlDeclaration);
	}
	public void renderNode(NodeBuilder node, Path file) {
		File f = file.toAbsolutePath().toFile();
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs(); 
				f.createNewFile();
			}
			FileOutputStream output = new FileOutputStream(f, false);
			try {
				renderNode(node, output);
			} finally {
				output.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot render node to file ("+file+")", e);
		}
	}
	public void renderNode(NodeBuilder node, OutputStream output) {
		if (node.isTextNode())
			try {
				output.write(node.getTextContent().getBytes());
			} catch (IOException e) {
				throw new RuntimeException("Cannot print text node", e);
			}
		else renderNode(node, true, output, false);
	}
	public String renderNode(NodeBuilder node, boolean prettyPrint) {
		return renderNode(node, prettyPrint, false);
	}
	public String renderNode(NodeBuilder node, boolean prettyPrint, boolean omitXmlDeclaration) {
		if (node.isTextNode()) return node.getTextContent();
		else {
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			renderNode(node, prettyPrint, writer, omitXmlDeclaration);			
			return writer.toString();
		}
	}
	public void renderDocument(Document document, OutputStream output) {
		renderDocument(document, new StreamResult(output), true);
	}
	private Element transformToDocument(NodeBuilder nodeBuilder, Element docElement, Document document) {
		if (nodeBuilder.isTextNode()) {
			Text el = document.createTextNode(nodeBuilder.getTextContent());
			docElement.appendChild(el);
			return docElement;
		}
		String prefix = nodeBuilder.getPrefix() != null ? nodeBuilder.getPrefix() + ":" : "";
		Element element;
		try {
			element = document.createElementNS(nodeBuilder.getNamespace(),  prefix + nodeBuilder.getName());	
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot create element with namespace ("+prefix + nodeBuilder.getName()+")", e);
		}
		Map<String, String> namespaces = nodeBuilder.getNamespaces();
		for (String ns: namespaces.keySet()) {
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns" + (ns == null ? "" : ":" + ns), namespaces.get(ns));
		}
		Map<String, Map<String, String>> prefixedAttributes = nodeBuilder.getPrefixedAttributes();
		for (String attributesPrefix : prefixedAttributes.keySet()) {
			Map<String, String> attributes = prefixedAttributes.get(attributesPrefix);
			String attributesNamespace = attributesPrefix == null ? null : nodeBuilder.getNamespace(attributesPrefix);
			for (Map.Entry<String,String> attribute : attributes.entrySet()) {
				String attrQN = nodeBuilder.createQualifiedName(attributesPrefix, attribute.getKey());
				String attrValue = attributes.get(attribute.getKey());
				if (attrValue != null) {
					if ("xml".equals(attributesPrefix)) {
						element.setAttribute(attrQN, attrValue);
					}
					else {
						element.setAttributeNS(attributesNamespace, attrQN, attrValue);
					}
				}
			}
		}
		String textContent = nodeBuilder.getTextContent();
		if (textContent != null) {
			element.setTextContent(textContent);
		}
		String cdata = nodeBuilder.getCDataContent();
		if (cdata != null) {
			element.appendChild(document.createCDATASection(cdata.replace("\r\n", "\n")));
		}
		if (docElement != null) docElement.appendChild(element);
		nodeBuilder.getChildren().forEach(child -> {
			transformToDocument(child, element, document);
		});
		return element;
	}
	private NodeBuilder load(Element e, NodeBuilder node) {
		node.setPrefix(e.getPrefix());
		
		NamedNodeMap map = e.getAttributes();
		for (int i = 0; i < map.getLength(); i++) {
			Node n = map.item(i);
			String prefix = n.getPrefix();
			String localName = n.getLocalName();
			if ("xmlns".equals(localName)) {
				node.addNamespace(n.getTextContent());
			}
			else if ("xmlns".equals(prefix)) {
				node.addNamespace(localName, n.getTextContent());
			}
			else if (localName != null){
				node.addAttribute(prefix, localName, n.getTextContent());
			}
			else {
				node.addAttribute(localName, n.getTextContent());
			}
		}
		NodeList nodes = e.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			switch (n.getNodeType()) {
			case Node.ELEMENT_NODE:
				load((Element)n, node.addChild(n.getPrefix(), n.getLocalName()));					
				break;
			case Node.TEXT_NODE:
				if (nodes.getLength() > 1) {
					if (preserveWhitespace || n.getTextContent().trim().length() > 0)
						node.addTextChild(n.getTextContent());
				}
				else {
					if (preserveWhitespace || n.getTextContent().trim().length() > 0)
						node.setTextContent(n.getTextContent());	
				}
				break;
			case Node.COMMENT_NODE: // clear comments
				break;
			case Node.CDATA_SECTION_NODE:
				node.setCDataContent(((CDATASection)n).getData());
				break;
			default:
				Log.get().warning("unknown type: " + n.getNodeType());
				break;
			}		
		}
		return node;
	}
	public NodeBuilder parseDocument(String xmlString) {
		InputStream stream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
		return parseDocument(stream);
	}
	public NodeBuilder parseDocument(InputStream input) {
		try (BOMInputStream bomInputStream = new BOMInputStream(input)) { // when the UTF file contains BOM reading inputStream failed) 
			InputSource ins = new InputSource(bomInputStream);
			return parseDocument(ins);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException("Could not parse XML document from stream: " + e.getMessage(), e);
		}
	}
	
	public NodeBuilder parseDocument(byte[] input) {
		try (InputStream is = new ByteArrayInputStream(input)) {
			return parseDocument(is);
		} catch (IOException e) {
			throw new RuntimeException("Could not parse XML document from array: " + e.getMessage(), e);
		}
	}
	
	private NodeBuilder parseDocument(InputSource ins) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		dbFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(ins);
		Element el = doc.getDocumentElement();
		return load(el, createRootElement(el.getPrefix(), el.getLocalName()));
	}
	public NodeBuilder loadFromFile(File path) {
		try (InputStream is = new FileInputStream(path)) {
			NodeBuilder n = this.parseDocument(is);			
			return n;		
		} catch (Exception e) {
			throw new RuntimeException("Cannot read xml ("+path+"): " + e.getMessage(), e);
		}
	}
	public NodeBuilder loadFromFile(Path path) {
		return loadFromFile(path.toFile());
	}
	public static void main(String[] args) throws FileNotFoundException {
		/*File wsdl = new File("T:\\source\\dev\\SBLCRM_WS_IO\\WSDL\\Siebel\\AccountManagement_UpsertBillingProfile_v2.1.wsdl");
		InputStream is = new FileInputStream(wsdl);
		XmlBuilderFactory f = new XmlBuilderFactory();
		NodeBuilder n = f.parseDocument(is);
		System.out.println(n);*/
		
		XmlBuilderFactory f = new XmlBuilderFactory();
		NodeBuilder x = f.createRootElement("test");
		x.
			addChild("a").
				addChild("aa").getParent().
				addChild("aa").getParent().
			getParent().
			addChild("b").	
				addChild("bb").
					addChild("bbb");
		x.search(true, n -> {
			Log.get().info(n.getName());
			return true;
		}).toArray();
		Log.get().info(x.toString());
	}
	public void renderNode(NodeBuilder node, boolean prettyPrint, OutputStream output) {
		renderNode(node, prettyPrint, output, false);
	}
}
