package com.meluzin.fluentxml.xml.builder;

import java.nio.file.Path;

import com.meluzin.fluentxml.xml.builder.BaseSchema.SchemaType;

public class SchemaReference {
	private String namespace;
	private Path refPath;		
	private Path sourcePath;
	private NodeBuilder xml;
	private SchemaType schemaType;
	/*public SchemaReference() {
	}*/


	public SchemaReference(String namespace, Path refPath, Path sourcePath, NodeBuilder xml, SchemaType schemaType) {
		super();
		this.namespace = namespace;
		this.refPath = refPath;
		this.sourcePath = sourcePath;
		this.xml = xml;
		this.schemaType = schemaType;
	}
	public NodeBuilder getXml() {
		return xml;
	}
	public void setXml(NodeBuilder xml) {
		this.xml = xml;
	}
	
	public String getNamespace() {
		return namespace;
	}
	public Path getRefPath() {
		return refPath;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public void setRefPath(Path refPath) {
		this.refPath = refPath;
	}

	public Path getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(Path sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	public SchemaType getSchemaType() {
		return schemaType;
	}
	public void setSchemaType(SchemaType schemaType) {
		this.schemaType = schemaType;
	}
	
	public Path getAbsolutePath() {
		return getSourcePath().getParent().resolve(getRefPath()).normalize();
	}
	
	@Override
	public String toString() {
		return getAbsolutePath().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result
				+ ((refPath == null) ? 0 : refPath.hashCode());
		result = prime * result
				+ ((sourcePath == null) ? 0 : sourcePath.hashCode());
		result = prime * result
				+ ((schemaType == null) ? 0 : schemaType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SchemaReference other = (SchemaReference) obj;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (refPath == null) {
			if (other.refPath != null)
				return false;
		} else if (!refPath.equals(other.refPath))
			return false;
		if (sourcePath == null) {
			if (other.sourcePath != null)
				return false;
		} else if (!sourcePath.equals(other.sourcePath))
			return false;
		if (schemaType == null) {
			if (other.schemaType != null)
				return false;
		} else if (!schemaType.equals(other.schemaType))
			return false;
		return true;
	}
	
}