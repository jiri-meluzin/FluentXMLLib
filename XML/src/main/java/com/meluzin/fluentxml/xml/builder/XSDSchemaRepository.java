package com.meluzin.fluentxml.xml.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.meluzin.fluentxml.xml.xsd.XmlNode;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAll;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlAttribute;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlChoice;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlComplexType;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlElement;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlGroup;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlType;
import com.meluzin.fluentxml.xml.xsd.XsdBuiltInTypes;
import com.meluzin.functional.Lists;

public class XSDSchemaRepository extends BaseSchemaRepository<XmlSchema> {
	private boolean enabledCache = false;
	private Map<String, List<XmlSchema>> schemaCache = new WeakHashMap<>();
	private Map<ReferenceInfo, XmlType<?>> typeCache = new WeakHashMap<>();
	private Map<ReferenceInfo, XmlGroup> groupCache = new WeakHashMap<>();
	private XSDSchemaRepository(SchemaRepository schemaRepository) {
		super(schemaRepository, Lists.asList(XsdBuiltInTypes.getBuiltInTypes(), XsdBuiltInTypes.getSOAPBuiltInTypes(), XsdBuiltInTypes.getBuiltInXmlTypes()));
		
	}
	public XSDSchemaRepository(SchemaRepository schemaRepository, XmlSchema... schemas) {
		this(schemaRepository);
		addSchema(Lists.asList(schemas));
	}
	public XSDSchemaRepository(SchemaRepository schemaRepository, Collection<XmlSchema> schemas) {
		this(schemaRepository);
		addSchema(schemas);
	}
	public XSDSchemaRepository(XmlSchema... schemas) {
		addSchema(Lists.asList(XsdBuiltInTypes.getBuiltInTypes(), XsdBuiltInTypes.getSOAPBuiltInTypes(), XsdBuiltInTypes.getBuiltInXmlTypes()));
		addSchema(Lists.asList(schemas));
	}
	public XSDSchemaRepository(Collection<XmlSchema> schemas) {
		addSchema(Lists.asList(XsdBuiltInTypes.getBuiltInTypes(), XsdBuiltInTypes.getSOAPBuiltInTypes(), XsdBuiltInTypes.getBuiltInXmlTypes()));
		addSchema(schemas);
	}
	public XmlNode<?> findReference(ReferenceInfo ref) {
		return findReference(ref.getLocalName(), ref.getNamespace());
	}
	public XmlNode<?> findReference(String elementName, String elementNamespace) {
		XmlNode<?> ref = 
				//getSchemas().stream().filter(s -> s.getTargetNamespace() != null && s.getTargetNamespace().equals(elementNamespace)).
				getSchemas(elementNamespace).
				flatMap(s -> s.getElements().stream()).
				filter(e -> (e instanceof XmlElement && elementName.equals(e.asElement().getName()))|| (e instanceof XmlGroup && elementName.equals(((XmlGroup)e).getName()))).
				findFirst().orElse(null)
			;
		if (ref == null) throw new NoSuchElementException("Unknown element: {" + elementNamespace +  "}:"+ elementName);
		return ref;
	}
	public XmlAttribute findReferenceAttribute(String attributeName, String elementNamespace) {
		XmlAttribute ref =
				getSchemas(elementNamespace).
				flatMap(s -> s.getElements().stream()).
				filter(e -> (e instanceof XmlAttribute && attributeName.equals(e.asAttribute().getName()))).
				map(e -> e.asAttribute()).
				findFirst().orElse(null)
			;
		if (ref == null && elementNamespace == null) {
			ref = XsdBuiltInTypes.getBuiltInXmlTypes().getElements().stream().filter(n -> n instanceof XmlAttribute).map(a -> a.asAttribute()).filter(a -> a.getName().equals(attributeName)).findFirst().orElse(null);
		}
		if (ref == null) {
			throw new NoSuchElementException("Unknown element: {" + elementNamespace +  "}:"+ attributeName);
		}
		return ref;
	}
	public XmlType<?> findType(ReferenceInfo ref) {
		return findType(ref.getLocalName(), ref.getNamespace());
	}
	public XmlType<?> findType(String typeName, String typeNamespace) {
		if (!enabledCache) return doFind(typeName, typeNamespace);
		ReferenceInfo typeRef = new ReferenceInfoImpl(typeNamespace, typeName);
		return typeCache.computeIfAbsent(typeRef, (tr) -> doFind(typeName, typeNamespace));		
	}
	private XmlType<?> doFind(String typeName, String typeNamespace) {
		XmlType<?> type = getSchemas(typeNamespace).
				flatMap(s -> s.getElements().stream()).
				filter(e -> e instanceof XmlType && typeName.equals(((XmlType<?>)e).getName())).
				map(t -> (XmlType<?>)t).
				findFirst().orElse(null)
			;
		if (type == null) {
			throw new NoSuchElementException("Unknown type: {" + typeNamespace +  "}:"+ typeName);
		}
		return type;
	}
	public boolean isEnabledCache() {
		return enabledCache;
	}
	public void setEnabledCache(boolean enabledCache) {
		if (enabledCache != this.enabledCache) clearCache();
		this.enabledCache = enabledCache;		
	}
	public void clearCache() {
		schemaCache.clear();
		typeCache.clear();
		groupCache.clear();
	}
	private Stream<XmlSchema> getSchemas(String typeNamespace) {
		if (!enabledCache) return doGetSchemas(typeNamespace);
		if (!schemaCache.containsKey(typeNamespace)) {
			schemaCache.put(typeNamespace, doGetSchemas(typeNamespace).collect(Collectors.toList()));
		}
		return schemaCache.get(typeNamespace).stream();
	}
	private Stream<XmlSchema> doGetSchemas(String typeNamespace) {
		return getSchemas().stream().filter(s ->  (s.getTargetNamespace() != null && s.getTargetNamespace().equals(typeNamespace)) || typeNamespace==s.getTargetNamespace());
	}
	public XmlType<?> findType(String typeName) {		
		XmlType<?> type = getSchemas().stream().//filter(s ->  s.getTargetNamespace() != null && s.getTargetNamespace().equals(typeNamespace)).
				flatMap(s -> s.getElements().stream()).
				filter(e -> e instanceof XmlType && typeName.equals(((XmlType<?>)e).getName())).
				map(t -> (XmlType<?>)t).
				findFirst().orElse(null)
			;
		if (type == null) throw new NoSuchElementException("Unknown type: "+ typeName);
		return type;
	}
	public XmlElement findElement(Predicate<XmlElement> filter) {
		for (XmlSchema schema : getSchemas()) {
			XmlElement ref =
					schema.getElements().stream().
						filter(e -> e instanceof XmlElement).
						map(e -> (XmlElement)e).
						filter(filter).
						findFirst().orElse(null)
					;
			if (ref != null) return ref;
		}
		throw new NoSuchElementException("not found for given filter");
	}
	public XmlType<?> findType(Predicate<XmlType<?>> filter) {
		for (XmlSchema schema : getSchemas()) {
			XmlType<?> type = 
				schema.getElements().stream().
					filter(e -> e instanceof XmlType).
					map(t -> (XmlType<?>)t).
					filter(filter).
					findFirst().orElse(null)
				;
			if (type != null) return type;
		}
		throw new NoSuchElementException("not found for given filter");
	}
	public List<XmlNode<?>> findAllReferencesTo(XmlNode<?> tree, XmlNode<?> target) {
		return doFindAllReferences(new HashSet<XmlNode<?>>(), tree, target);
	}
	private List<XmlNode<?>> doFindAllReferences(Set<XmlNode<?>> processed, XmlNode<?> tree, XmlNode<?> target) {
		if (processed.contains(tree)) return Arrays.asList();
		HashSet<XmlNode<?>> newHashSet = new HashSet<XmlNode<?>>(processed);
		newHashSet.add(tree);
		List<XmlNode<?>> list = new ArrayList<XmlNode<?>>();
		List<List<XmlNode<?>>> references = new ArrayList<List<XmlNode<?>>>();
		if (tree instanceof XmlSchema) {
			references = 
					tree.asSchema().getElements().stream().
						map(e -> doFindAllReferences(newHashSet, e, target)).
						collect(Collectors.toList());
			
		}
		else if (tree instanceof XmlType<?> && target instanceof XmlType<?>) {
			XmlType<?> t = (XmlType<?>)tree;
			XmlType<?> r = (XmlType<?>)target;
			
			if (t.getBaseType() != null) {
				if (t.getBaseType().equals(r.getName()) && t.getBaseTypeNamespace().equals(r.getSchemaTargetNamespace())) list.add(t);
				else {
					references.add(doFindAllReferences(newHashSet, findType(t.getBaseType(), t.getBaseTypeNamespace()), target));						
				}
			}
			if (t instanceof XmlComplexType) {
				XmlComplexType c = (XmlComplexType)t;
				references.addAll(c.getChildren().stream().map(e -> doFindAllReferences(newHashSet, e, target)).collect(Collectors.toList()));
			}
		}
		else if (tree instanceof XmlGroup) {
			XmlGroup t = (XmlGroup)tree;
			
			if (target instanceof XmlGroup) {
				XmlGroup r = (XmlGroup)target;
				if (t.getRef() != null) {
					if (t.getRef().equals(r.getName()) && t.getRefNamespace().equals(r.getSchemaTargetNamespace())) list.add(t);
					else {
						references = Arrays.asList(doFindAllReferences(newHashSet, findReference(t.getRef(), t.getRefNamespace()), target));						
					}
				}
			}
			if (t.getChild() != null) {
				references = Arrays.asList(doFindAllReferences(newHashSet, t.getChild(), target));
			}
		}
		else if (tree instanceof XmlChoice) {
			XmlChoice t = (XmlChoice)tree;
			references = t.getChildren().stream().map(n -> doFindAllReferences(newHashSet, n, target)).collect(Collectors.toList());
		}
		else if (tree instanceof XmlAll) {
			XmlAll t = (XmlAll)tree;
			references = t.getChildren().stream().map(n -> doFindAllReferences(newHashSet, n, target)).collect(Collectors.toList());
		}
		else if (tree instanceof XmlElement && target instanceof XmlType<?>) {
			XmlElement t = (XmlElement)tree;
			XmlType<?> r = (XmlType<?>)target;
			
			if (t.getType() != null) {
				if (t.getType().equals(r.getName()) && t.getTypeNamespace().equals(r.getSchemaTargetNamespace())) list.add(t);
				else {
					references.add(doFindAllReferences(newHashSet, findType(t.getType(), t.getTypeNamespace()), target));
				}
			}
			else if (t.getRef() != null) {
				references.add(doFindAllReferences(newHashSet, findReference(t.getRef(), t.getRefNamespace()), target));
			}
			if (t.getComplexType() != null){
				references.addAll(t.getComplexType().getChildren().stream().map(e -> doFindAllReferences(newHashSet, e, target)).collect(Collectors.toList()));
			}
		}
		else if (tree instanceof XmlElement && target instanceof XmlElement) {
			XmlElement t = (XmlElement)tree;
			XmlElement r = (XmlElement)target;
			
			if (t.getRef() != null && t.getRef().equals(r.getName()) && t.getRefNamespace().equals(r.getSchemaTargetNamespace())) list.add(t);
			if (t.getComplexType() != null){
				references.addAll(t.getComplexType().getChildren().stream().map(e -> doFindAllReferences(newHashSet, e, target)).collect(Collectors.toList()));
				
			}
		}
		for (List<XmlNode<?>> l : references) {
			list.addAll(l);
		}
		return list;
	}
	public List<XmlNode<?>> findAllReferencesTo(XmlNode<?> node) {
		List<XmlNode<?>> list = new ArrayList<XmlNode<?>>();
		for (XmlSchema xmlSchema : getSchemas()) {
			list.addAll(findAllReferencesTo(xmlSchema, node));
		}
		return list;
	}
	public XmlGroup findGroup(String groupName, String groupNamespace) {
		if (!enabledCache) return doFindGroup(groupName, groupNamespace);		
		ReferenceInfo groupRef = new ReferenceInfoImpl(groupNamespace, groupName);
		return groupCache.computeIfAbsent(groupRef, (ref) -> doFindGroup(groupName, groupNamespace));
	}
	private XmlGroup doFindGroup(String groupName, String groupNamespace) {
		
		XmlGroup group = getSchemas(groupNamespace).map(s -> s.getElements().stream()).flatMap(g -> g).filter(g -> g instanceof XmlGroup).map(g -> (XmlGroup)g).filter(g -> groupName.equals(g.getName())).findAny().orElse(null);

		if (group == null) {
			throw new NoSuchElementException("Unknown group: {" + groupNamespace +  "}:"+ groupName);
		}
		return group;
	}
}
