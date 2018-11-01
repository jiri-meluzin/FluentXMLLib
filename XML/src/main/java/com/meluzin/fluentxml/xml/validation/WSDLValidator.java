package com.meluzin.fluentxml.xml.validation;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.functional.FileSearcher;
import com.meluzin.functional.T;

public class WSDLValidator {


	public Validator getValidator(Path wsdlsPath)  {
		String property = System.getProperty("jdk.xml.maxOccurLimit");
		if (property == null) {
			// See https://stackoverflow.com/questions/16651005/workaround-for-xmlschema-not-supporting-maxoccurs-larger-than-5000
			// gsx-coreAsp-schema.xsd contains 
            // <xs:element name="parts"  type="tns:acknowledgeConsignmentIncreaseOrderPartType" minOccurs="1" maxOccurs = "10000"/>
			System.setProperty("jdk.xml.maxOccurLimit", "100000");
		}
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		//LSResourceResolver resolver = schemaFactory.getResourceResolver();
		List<T.V2<StreamSource, NodeBuilder>> wsdls= new FileSearcher().searchFiles(wsdlsPath, "glob:**/*.wsdl", true).
				stream().flatMap(p -> loadSchemasFromWSDL(p)).
				collect(Collectors.toList());
		schemaFactory.setResourceResolver(new LSResourceResolver() {
			
			@Override
			public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
				if (systemId == null) {
					System.out.println("Warning: for namespace {"+namespaceURI+"} has come systemId=null , baseURI="+baseURI);
					// this should be correct, schema contains import with no schemaLocation
					return null;
				}
				Optional<NodeBuilder> s = wsdls.stream().filter(w -> w.getA().getSystemId().equals(systemId)).map(v -> v.getB()).findFirst();
				T.V1<NodeBuilder> data = T.V(null);
				if (!s.isPresent()) {
					try {
						File relativePath = new File(new URI(baseURI).resolve(systemId).getPath());
						data.setA(new XmlBuilderFactory().loadFromFile(relativePath));
					} catch (RuntimeException | URISyntaxException e) {
						throw new RuntimeException("Could not read file: " + systemId + "; " + e.getMessage(), e);
					}
				}
				else {
					data.setA(s.get());
				}
				final LSInput input = new LSInput() {
					
					@Override
					public void setSystemId(String arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setStringData(String arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setPublicId(String arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setEncoding(String arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setCharacterStream(Reader arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setCertifiedText(boolean arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setByteStream(InputStream arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public void setBaseURI(String arg0) {
						throw new UnsupportedOperationException();
						
					}
					
					@Override
					public String getSystemId() {
						return systemId;
					}
					
					@Override
					public String getStringData() {
						return null;
					}
					
					@Override
					public String getPublicId() {
						return publicId;
					}
					
					@Override
					public String getEncoding() {
						return "UTF8";
					}
					
					@Override
					public Reader getCharacterStream() {
						return new StringReader(data.getA().toString());
					}
					
					@Override
					public boolean getCertifiedText() {
						return false;
					}
					
					@Override
					public InputStream getByteStream() {
						return null;
					}
					
					@Override
					public String getBaseURI() {
						return baseURI;
					}
				};
				return input;
			}
		}); 
		Source[] s = listToArray(wsdls.stream().map(v -> v.getA()).collect(Collectors.toList()));
		Schema schema;
		try {
			schema = schemaFactory.newSchema(s);
		} catch (SAXException e) {
			throw new RuntimeException("Could not load schemas: " + e.toString(), e);
		}
		Validator validator = schema.newValidator();
		return validator;
	}

	private  Source[] listToArray(List<? extends Source> wsdls) {
		Object[] o = wsdls.toArray();
		Source[] s = new Source[o.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = (Source)o[i];
		}
		return s;
	}

	private Stream<T.V2<StreamSource, NodeBuilder>> loadSchemasFromWSDL(Path p) {
		List<NodeBuilder> schemasInWSDL = new XmlBuilderFactory().loadFromFile(p).search(true, n -> "schema".equals(n.getName())).collect(Collectors.toList());
		Map<String, String> mapNamespaceToLocation = new HashMap<>();
		for (int i = 0; i < schemasInWSDL.size(); i++) {
			mapNamespaceToLocation.put(schemasInWSDL.get(i).getAttribute("targetNamespace"), p.toUri().toString()+"#"+i+".xsd");
		}
		List<T.V2<StreamSource, NodeBuilder>> streamSorucesFromSchemas = new ArrayList<>();
		for (int i = 0; i < schemasInWSDL.size(); i++) {
			NodeBuilder schemaXml = mergeNamespaces(schemasInWSDL.get(i), mapNamespaceToLocation);
			streamSorucesFromSchemas.add(T.V(new StreamSource( new StringReader(schemaXml.toString()), p.toUri().toString()+"#"+i+".xsd"), schemaXml));
			
		}
		return streamSorucesFromSchemas.stream();
	}
	
	private NodeBuilder mergeNamespaces(NodeBuilder schemaNode, Map<String, String> mapNamespaceToLocation) {
		schemaNode.search(true, n -> "import".equals(n.getName()) && n.getAttribute("schemaLocation") == null).forEach(n -> {
			n.addAttribute("schemaLocation", mapNamespaceToLocation.get(n.getAttribute("namespace")));
		});
		Map<String,String> all = schemaNode.getAllNamespaces();
		Map<String,String> node = schemaNode.getNamespaces();
		for (String prefix : all.keySet()) {
			if (!node.containsKey(prefix)) {
				schemaNode.addNamespace(prefix, all.get(prefix));
			}
		}
		return schemaNode;
	}
}
