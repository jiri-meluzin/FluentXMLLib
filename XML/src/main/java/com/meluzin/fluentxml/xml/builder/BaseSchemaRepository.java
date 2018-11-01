package com.meluzin.fluentxml.xml.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BaseSchemaRepository<T extends BaseSchema> {
	private Optional<SchemaRepository> schemaRepository = Optional.empty();
	private Set<T> schemas = new HashSet<>();
	private Map<String, T> cache = new HashMap<String, T>();
	public BaseSchemaRepository() {
	}
	public BaseSchemaRepository(SchemaRepository schemaRepository, Collection<T> schemas) {
		this.schemaRepository = Optional.of(schemaRepository);
		addSchema(schemas);
	}
	public BaseSchemaRepository(Collection<T> schemas) {
		addSchema(schemas);
	}
	public Set<T> getSchemas() {
		return schemas;
	}
	public void addSchema(Collection<T> schemas) {
		this.schemas.addAll(schemas);
		schemas.forEach(s -> cache.put(s.getTargetNamespace(), s));
	}
	public Optional<T> getSchema(String namespace) {
		T schema = cache.get(namespace);
		if (schema != null) return Optional.of(schema);
		else return Optional.empty();
	}
	public Optional<SchemaRepository> getSchemaRepository() {
		return schemaRepository;
	}

	public Optional<SchemaReference> findRefFromSchema(BaseSchema schema) {
		if (schemaRepository.isPresent()) {
			return schemaRepository.get().findRefFromSchema(schema);
		}
		else {
			return Optional.empty();
		}
	}
}
