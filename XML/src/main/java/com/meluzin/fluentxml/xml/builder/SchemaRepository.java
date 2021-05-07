package com.meluzin.fluentxml.xml.builder;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.meluzin.fluentxml.wsdl.Wsdl;
import com.meluzin.fluentxml.xml.builder.BaseSchema.SchemaType;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;

public class SchemaRepository {
	private Map<SchemaReference, BaseSchema> repo;
	
	public SchemaRepository(Map<SchemaReference, BaseSchema> repo) {
		this.repo = repo;
	}

	public Optional<SchemaReference> findRefFromSchema(BaseSchema schema) {
		return repo.entrySet().stream().filter(v -> v.getValue() == schema).map(v -> v.getKey()).findAny();
	}
	public Optional<BaseSchema> findSchemaFromRef(Path path, SchemaType type) {
		return repo.entrySet().stream().
					filter(v -> v.getKey().getAbsolutePath().equals(path)).
					filter(v -> v.getValue().getSchemaType() == type).
					map(v -> v.getValue()).findAny();
	}
	
	public XSDSchemaRepository getXSDSchemaRepository() {
		List<XmlSchema> schemas = repo.
			entrySet().
			stream().
			filter(e -> e.getKey().getSchemaType() == SchemaType.XSD).
			map(v -> (XmlSchema)v.getValue()).
			collect(Collectors.toList());
		return new XSDSchemaRepository(this, schemas);
	}
	public WSDLSchemaRepository getWSDLSchemaRepository() {
		List<Wsdl> schemas = repo.
			entrySet().
			stream().
			filter(e -> e.getKey().getSchemaType() == SchemaType.WSDL).
			map(v -> (Wsdl)v.getValue()).
			collect(Collectors.toList());
		return new WSDLSchemaRepository(this, schemas);
	}
	
	public Map<SchemaReference, BaseSchema> getRepo() {
		return repo;
	}
}
