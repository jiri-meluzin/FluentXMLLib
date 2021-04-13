package com.meluzin.fluentxml.xml.builder;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.meluzin.fluentxml.wsdl.impl.WsdlImpl;
import com.meluzin.fluentxml.xml.builder.BaseSchema.SchemaType;
import com.meluzin.fluentxml.xml.xsd.XmlNode.SchemaImport;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;
import com.meluzin.fluentxml.xml.xsd.XmlSchemaBuilder;

public class SchemaLoader {
	private static String wsdlNamespace = "http://schemas.xmlsoap.org/wsdl/";
	private static String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
	private static PathMatcher wsdlMatcher = FileSystems.getDefault().getPathMatcher("glob:**.wsdl");
	private static PathMatcher xsdMatcher = FileSystems.getDefault().getPathMatcher("glob:**.xsd");
	Map<SchemaReference, BaseSchema> cache = new HashMap<>();
	Set<Path> processedFiles = new HashSet<>();
	public SchemaLoader() {
	}
	
	public SchemaRepository resolve(Path source) {
		if (processedFiles.contains(source)) return getSchemaRepository();
		else processedFiles.add(source);
		NodeBuilder sourceXml = loadXml(source);
		
		Stream<SchemaReference> references = Stream.empty();
		if (wsdlMatcher.matches(source)) {
			references = Stream.concat(references, loadReferences(source, sourceXml));
			
			List<BaseSchema> schemas = loadSchemasFromWSDL(source, sourceXml);
			for (BaseSchema xmlSchema : schemas) {
				String target = xmlSchema.getTargetNamespace();
				SchemaReference ref = new SchemaReference(target, source, source, xmlSchema.render(new XmlBuilderFactory()), xmlSchema.getSchemaType());
				if (cache.containsKey(ref)) {
					xmlSchema = xmlSchema.merge(cache.get(ref), new HashSet<String>());
				}
				cache.put(ref, xmlSchema);
			}		
		} else if (xsdMatcher.matches(source)) {
			SchemaReference ref = new SchemaReference(sourceXml.getAttribute("targetNamespace"), source, source, sourceXml, SchemaType.XSD);
			XmlSchema xmlSchema = new XmlSchemaBuilder(ref);
			if (cache.containsKey(ref)) {
				xmlSchema.merge(cache.get(ref), new HashSet<String>());
			}
			cache.put(ref, xmlSchema);
			references = Stream.concat(references, xmlSchema.getImports().stream().map(extractImportedReferencesFromXSD(source, xmlSchema)));
			references = Stream.concat(references, xmlSchema.getIncludes().stream().map(extractIncludeReferencesFromXSD(source, xmlSchema)));
		} else {
			System.err.println("Unsupported path: " + source);
		}
		references.forEach(schemaReference -> {
			resolve(schemaReference);
		});
		
		return getSchemaRepository();
	}

	public SchemaRepository getSchemaRepository() {
		return new SchemaRepository(cache);
	}
	protected NodeBuilder loadXml(Path source) {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder sourceXml = fac.loadFromFile(source);
		return sourceXml;		
	}
	protected void resolve(SchemaReference schemaReference) {
		if (schemaReference.getRefPath() != null) {
			resolve(schemaReference.getAbsolutePath());					
		}
	}


	public Function<? super SchemaImport, ? extends SchemaReference> extractImportedReferencesFromXSD(Path source, XmlSchema xmlSchema) {
		return schemaImport -> new SchemaReference(schemaImport.getNamespace(), Paths.get(schemaImport.getLocation().get()), source, null, xmlSchema.getSchemaType());
	}
	public Function<? super String, ? extends SchemaReference> extractIncludeReferencesFromXSD(Path source, XmlSchema xmlSchema) {
		return relativePath -> new SchemaReference(xmlSchema.getTargetNamespace(), Paths.get(relativePath), source, null, xmlSchema.getSchemaType());
	}

	public List<BaseSchema> loadSchemasFromWSDL(Path sourcePath, NodeBuilder sourceXml) {		
		List<BaseSchema> xsdSchemas = sourceXml.
			search(true, c -> "schema".equals(c.getName()) && xsdNamespace.equals(c.getNamespace())).
			map(n -> new SchemaReference(n.getAttribute("targetNamespace"), null, sourcePath, n, SchemaType.XSD)).
			map(schemaReference -> new XmlSchemaBuilder(schemaReference)).
			collect(Collectors.toList());
		List<BaseSchema> wsdlSchemas = sourceXml.
		search(true, c -> "definitions".equals(c.getName()) && wsdlNamespace.equals(c.getNamespace())).
		map(n -> new WsdlImpl(n)).
		collect(Collectors.toList());
		xsdSchemas.addAll(wsdlSchemas);
		return xsdSchemas; 
	}

	public Stream<SchemaReference> loadReferences(Path source, NodeBuilder sourceXml) {
		return Stream.concat(loadWSDLReferences(source, sourceXml), loadXSDReferences(source, sourceXml));
	}

	private Stream<SchemaReference> loadXSDReferences(Path source, NodeBuilder sourceXml) {
		return sourceXml.
				search(true, c -> ("import".equals(c.getName()) || "include".equals(c.getName())) && xsdNamespace.equals(c.getNamespace())).
				map(n -> new SchemaReference(n.getAttribute("namespace"), n.getAttribute("schemaLocation") == null ? source : Paths.get(n.getAttribute("schemaLocation").trim()), source, n, SchemaType.XSD));
	}

	private Stream<SchemaReference> loadWSDLReferences(Path source, NodeBuilder sourceXml) {
		return sourceXml.
					search(true, c -> ("import".equals(c.getName()) || "include".equals(c.getName())) && wsdlNamespace.equals(c.getNamespace())).
					filter(n -> n.hasAttribute("location")).
					map(n -> new SchemaReference(n.getAttribute("namespace"), Paths.get(n.getAttribute("location")), source, n, SchemaType.WSDL));
	}
}