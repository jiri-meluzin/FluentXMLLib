package com.meluzin.fluentxml.xml.builder;

import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public final class ReferenceInfoImpl implements ReferenceInfo {
	private String namespace; 
	private String localName;
	public ReferenceInfoImpl(String qualifiedName, NodeBuilder context) {
		if (qualifiedName != null) {
			String[] split = qualifiedName.split(":");
			if (split.length == 2) {
				localName = split[1];
				namespace = context.getNamespace(split[0]);
			}
			else {
				localName = split[0];
				namespace = context.getNamespace(null);
			}
		}
	}
	public ReferenceInfoImpl(String namespace, String localName) {
		this.localName = localName;
		this.namespace = namespace;
	}
	@Override
	public String createQualifiedName(NodeBuilder context) {
		return createQualifiedName(context, true);
	}
	@Override
	public String createQualifiedName(NodeBuilder context, Boolean qualified) {
		if (localName == null) return null;
		if (namespace == null || !(Boolean.TRUE.equals(qualified))) return localName;
		String prefix = context.getNamespacePrefix(namespace);		
		if (prefix == null && namespace.equals(context.getNamespace())) return localName;
		if (prefix == null) {
			NodeBuilder root = context.getRoot();
			int count = context.getAllNamespaces().size();
			prefix = "ns" + count;
			root.addNamespace(prefix, namespace);
		}
		return prefix + ":" + localName;
	}
	
	@Override
	public String createPrefix(NodeBuilder context) {
		createQualifiedName(context);
		return context.getNamespacePrefix(namespace);
	}
	
	@Override
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public String getLocalName() {
		return localName;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localName == null) ? 0 : localName.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
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
		ReferenceInfoImpl other = (ReferenceInfoImpl) obj;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "{" + getNamespace() + "}" + getLocalName();
	}
}