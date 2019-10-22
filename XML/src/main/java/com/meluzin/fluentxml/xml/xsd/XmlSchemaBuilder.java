package com.meluzin.fluentxml.xml.xsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.meluzin.fluentxml.xml.builder.BaseSchema;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.SchemaReference;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.xsd.XmlNode.XmlSchema;
import com.meluzin.fluentxml.xml.xsd.impl.BaseXmlNode;
import com.meluzin.fluentxml.xml.xsd.impl.XmlAttributeImpl;
import com.meluzin.fluentxml.xml.xsd.impl.XmlComplexTypeImpl;
import com.meluzin.fluentxml.xml.xsd.impl.XmlElementImpl;
import com.meluzin.fluentxml.xml.xsd.impl.XmlExceptionTypeImpl;
import com.meluzin.fluentxml.xml.xsd.impl.XmlGroupImpl;
import com.meluzin.fluentxml.xml.xsd.impl.XmlSimpleTypeImpl;

public class XmlSchemaBuilder extends BaseXmlNode<XmlSchema> implements XmlSchema {
	private List<XmlNode<?>> elements = new ArrayList<XmlNode<?>>();
	private Map<String, String> namespaces = new HashMap<String, String>();
	private Map<String, String> importNamespaces = new HashMap<String, String>();
	private List<String> includes = new ArrayList<>();
	private Boolean elementFormQualified = true;
	private Boolean attributeFormQualified;
	private String targetNamespace;
	private Optional<SchemaReference> schemaReference = Optional.empty();
	public XmlSchemaBuilder(SchemaReference schemaReference) {
		super(null);
		this.targetNamespace = schemaReference.getNamespace();
		this.schemaReference = Optional.of(schemaReference);
		loadFromNode(schemaReference.getXml());
	}

	public XmlSchemaBuilder(String targetNamespace) {
		super(null);
		this.targetNamespace = targetNamespace;
		this.schemaReference = Optional.empty();
	}
	public NodeBuilder renderChildren(NodeBuilder root, boolean sorted) {
		for (String prefix: namespaces.keySet()) {
			root.addNamespace(prefix, namespaces.get(prefix));
		}
		for (String schemaLocation: includes) {
			root.addChild("include").addAttribute("schemaLocation", schemaLocation);
		}
		for (String importNS: importNamespaces.keySet()) {
			root.addChild("import").addAttribute("namespace", importNS).addAttribute("schemaLocation", importNamespaces.get(importNS));
		}
		root.
			addNamespace("tns", targetNamespace).
			addAttribute("targetNamespace", targetNamespace);
		List<XmlNode<?>> elements = getElements(); 
		if (sorted) {
			elements = getElements().stream().sorted((a,b) -> {
				if (a instanceof XmlSimpleType && b instanceof XmlSimpleType) {
					return compare((XmlSimpleType)a, (XmlSimpleType)b);
				}
				else if (a instanceof XmlComplexType && b instanceof XmlComplexType) {
					return compare((XmlComplexType)a, (XmlComplexType)b);
				}
				else if (a instanceof XmlElement && b instanceof XmlElement) {
					return compare(a.asElement(), b.asElement());
				}
				else if (a instanceof XmlElement && b instanceof XmlSimpleType) {
					return -1;
				}
				else if (a instanceof XmlElement && b instanceof XmlComplexType) {
					return -1;
				}
				else if (a instanceof XmlSimpleType && b instanceof XmlElement) {
					return 1;
				}
				else if (a instanceof XmlSimpleType && b instanceof XmlComplexType) {
					return -1;
				}
				else if (a instanceof XmlComplexType && b instanceof XmlElement) {
					return 1;
				}
				else if (a instanceof XmlComplexType && b instanceof XmlSimpleType) {
					return 1;
				}
				return 0;
				
			}).collect(Collectors.toList());
		}
		for (XmlNode<?> complexType : elements) {
			complexType.render(root);
		}
		return root;
	}
	private int compare(XmlElement a, XmlElement b) {
		return a == null && b == null ? 0 : a == null ? -1 : b == null ? 1 : compare(a.getName(), b.getName()); 
	}
	private int compare(XmlType<?> a, XmlType<?> b) {
		return a == null && b == null ? 0 : a == null ? -1 : b == null ? 1 : compare(a.getName(), b.getName()); 
	}
	private int compare(String a, String b) {
		return a == null && b == null ? 0 : a == null ? -1 : b == null ? 1 : a.compareTo(b);
	}
	private void renderSchema(NodeBuilder schemaElement) {
		schemaElement.
			//addNamespace("http://www.w3.org/2001/XMLSchema").
			setPrefix("xsd").
			addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
		if (isAttributeFormQualified() == Boolean.TRUE) schemaElement.addAttribute("attributeFormDefault", "qualified");
		if (isAttributeFormQualified() == Boolean.FALSE) schemaElement.addAttribute("attributeFormDefault", "unqualified");
		if (isElementFormQualified() == Boolean.TRUE) schemaElement.addAttribute("elementFormDefault", "qualified");
		if (isElementFormQualified() == Boolean.FALSE) schemaElement.addAttribute("elementFormDefault", "unqualified");
	}
	public NodeBuilder render(XmlBuilderFactory xmlFactory) {
		return render(xmlFactory, false);
	}
	public NodeBuilder render(XmlBuilderFactory xmlFactory, boolean sorted) {
		NodeBuilder schema = xmlFactory.createRootElement("schema");
		renderSchema(schema);
		return renderChildren(schema, sorted);
	}
	public NodeBuilder render(NodeBuilder parent) {
		return render(parent, false);
	}
	public NodeBuilder render(NodeBuilder parent, boolean sorted) {
		NodeBuilder schema = parent.addChild(null, "schema");
		renderSchema(schema);
		renderChildren(schema, sorted);
		return parent;
	}
	public String getTargetNamespace() {
		return targetNamespace;
	}
	public List<XmlNode<?>> getElements() {
		return elements;
	}
	@Override
	public XmlComplexType addType(String name) {
		XmlComplexType type = new XmlComplexTypeImpl(this).setName(name);
		elements.add(type);
		return type;
	}
	@Override
	public XmlAttribute addAttribute(String name) {
		XmlAttribute type = new XmlAttributeImpl(this).setName(name);
		elements.add(type);
		return type;
	}
	@Override
	public XmlElement addElement(String name) {
		XmlElement type = new XmlElementImpl(this).setName(name);
		elements.add(type);
		return type;
	}
	@Override
	public XmlGroup addGroup() {
		XmlGroup group = new XmlGroupImpl(this);
		elements.add(group);
		return group;
	}
	@Override
	public XmlSimpleType addSimpleType(String name) {
		XmlSimpleType type = new XmlSimpleTypeImpl(this).setName(name);
		elements.add(type);
		return type;
	}
	@Override
	public XmlExceptionType addException(String name) {
		XmlExceptionType ex = new XmlExceptionTypeImpl(this).setName(name);
		elements.add(ex);
		return ex;
	}
	@Override
	public XmlExceptionType addException(String name, String elementName) {
		XmlExceptionType ex = new XmlExceptionTypeImpl(this).setName(name);
		elements.add(ex);
		return ex;
	}
	@Override
	public XmlSchema addNamespace(String prefix, String namespace) {
		this.namespaces.put(prefix, namespace);
		return this;
	}
	@Override
	public XmlSchema importNamespace(String namespace) {
		return importNamespace(namespace, null);
	}
	@Override
	public XmlSchema importNamespace(String namespace, String xsd) {
		this.importNamespaces.put(namespace, xsd);
		return this;
	}
	@Override
	public Map<String, String> getNamespaces() {
		return namespaces;
	}
	@Override
	public Map<String, String> getImports() {
		return importNamespaces;
	}

	@Override
	public BaseSchema merge(BaseSchema schemaToMerge, Set<String> changeToTargetNamespace) {
		if (schemaToMerge == null) {
			throw new IllegalArgumentException("Schema to merge is null");
		}
		/*if (schemaToMerge instanceof XmlSchema) {
			throw new IllegalArgumentException("Schema to merge is not an instance of XmlSchema, but " + schemaToMerge.getClass());
		}*/
		XmlSchema schemaToAdd = (XmlSchema)schemaToMerge;
		if (schemaReference.isPresent() && schemaReference.get().getXml() == null) schemaReference.get().setXml(schemaToAdd.getSchemaReference().get().getXml());
		targetNamespace = targetNamespace == null ? schemaToAdd.getTargetNamespace() : targetNamespace;
		for (XmlNode<?> node : schemaToAdd.getElements()) {

				if (node instanceof XmlElement) {
					XmlElement e = (XmlElement)node;
					e.duplicateInSchema(addElement(e.getName()), changeToTargetNamespace);
				}
				else if (node instanceof XmlSimpleType){
					XmlSimpleType s = (XmlSimpleType)node;
					s.duplicateInSchema(addSimpleType(s.getName()), changeToTargetNamespace);
				}
				else if (node instanceof XmlComplexType){
					XmlComplexType c = (XmlComplexType)node;
					c.duplicateInSchema(addType(c.getName()), changeToTargetNamespace);
				}
				else if (node instanceof XmlAttribute) {
					XmlAttribute e = (XmlAttribute)node;
					e.duplicateInSchema(addAttribute(e.getName()), changeToTargetNamespace);
				}
				else {
					throw new UnsupportedOperationException("Not supported XmlNode type: " + node);
				}
		}
		List<String> validation = validate();
		validation.stream().forEach(msg -> System.err.println(targetNamespace + ": "  + msg));
		return this;
	}
	
	private Stream<NodeBuilder> getSchemaImports(NodeBuilder schema) {
		return getNodesByName(schema, "import");
	}
	private Stream<NodeBuilder> getSchemaIncludes(NodeBuilder schema) {
		return getNodesByName(schema, "include");
	}
	private Stream<NodeBuilder> getNodesByName(NodeBuilder schema, final String nodeName) {
		return schema.search(t -> nodeName.equals(t.getName()));
	}
	@Override
	public Optional<SchemaReference> getSchemaReference() {
		return schemaReference;
	}
	@Override
	public XmlSchema loadFromNode(NodeBuilder node) {
		this.targetNamespace = node.getAttribute("targetNamespace");
		
		if ("unqualified".equals(node.getAttribute("attributeFormDefault"))) setAttributeFormQualified(false);
		else if ("qualified".equals(node.getAttribute("attributeFormDefault"))) setAttributeFormQualified(true);
		else setAttributeFormQualified(null);
		
		if ("unqualified".equals(node.getAttribute("elementFormDefault"))) setElementFormQualified(false);
		else if ("qualified".equals(node.getAttribute("elementFormDefault"))) setElementFormQualified(true);
		else setElementFormQualified(null);
		getSchemaIncludes(node).forEach(inc -> {
			includes.add(inc.getAttribute("schemaLocation"));
		});
		getSchemaImports(node).forEach(imp -> {
			String namespace = imp.getAttribute("namespace");
			importNamespace(namespace, imp.getAttribute("schemaLocation"));			
			addNamespace(node.getNamespacePrefix(namespace), namespace);
		});
		for (NodeBuilder el : node.getChildren()) {
			if ("element".equals(el.getName())) {
				addElement(el.getAttribute("name")).loadFromNode(el);
			}
			else if ("group".equals(el.getName())) {
				addGroup().loadFromNode(el);
			}
			else if ("simpleType".equals(el.getName())) {
				addSimpleType(el.getAttribute("name")).loadFromNode(el);
			}
			else if ("complexType".equals(el.getName())) {
				addType(el.getAttribute("name")).loadFromNode(el);
			}
			else if ("attribute".equals(el.getName())) {
				addAttribute(el.getAttribute("name")).loadFromNode(el);
			}
			else if ("import".equals(el.getName())) {
				String namespace = el.getAttribute("namespace");
				importNamespace(namespace, el.getAttribute("schemaLocation"));			
				addNamespace(node.getNamespacePrefix(namespace), namespace);				
			}
			else if ("annotation".equals(el.getName()) || el.isTextNode()) {
				// ignore annotations
			}
			else {
				System.err.println("Warning, unknown element in schema: " + el.getName());
			}
		}		
		List<String> validation = validate();
		validation.stream().forEach(msg -> System.err.println(targetNamespace + ": "  + msg));
		return this;
	}
	public XmlSchemaBuilder() {
		super(null);
	}
	@Override
	public XmlSchema setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
		return this;
	}
	
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		Map<String, List<XmlComplexType>> types = getElements().stream().filter(t -> t instanceof XmlComplexType).map(t -> (XmlComplexType)t).collect(Collectors.groupingBy(XmlComplexType::getName));
		for (String name : types.keySet()) {
			if (types.get(name).size() > 1) {
				errors.add("Duplicated type: " + name);
			}
		}
		Map<String, List<XmlElement>> elements = getElements().stream().filter(t -> t instanceof XmlElement).map(t -> (XmlElement)t).filter(t -> t.getName() != null).collect(Collectors.groupingBy(XmlElement::getName));
		for (String name : elements.keySet()) {
			if (elements.get(name).size() > 1) {
				errors.add("Duplicated element: " + name);
			}
		}
		Map<String, List<XmlElement>> refs = getElements().stream().filter(t -> t instanceof XmlElement).map(t -> (XmlElement)t).filter(t -> t.getRef() != null).collect(Collectors.groupingBy(t -> t.getRefNamespace() + ":" + t.getRef()));
		for (String ref : refs.keySet()) {
			if (refs.get(ref).size() > 1) {
				errors.add("Duplicated element: " + ref);
			}
		}
		return errors;
	}
	@Override
	public String toString() {
		return render(new XmlBuilderFactory(), false).toString();
	}
	@Override
	public XmlSchema removeElement(XmlNode<?> element) {
		elements.remove(element);
		return this;
	}
	@Override
	public Boolean isAttributeFormQualified() {
		return attributeFormQualified;
	}
	@Override
	public Boolean isElementFormQualified() {
		return elementFormQualified;
	}
	@Override
	public XmlSchema setAttributeFormQualified(Boolean qualified) {
		this.attributeFormQualified = qualified;
		return this;
	}
	@Override
	public XmlSchema setElementFormQualified(Boolean qualified) {
		this.elementFormQualified = qualified;
		return this;
	}
	@Override
	public List<String> getIncludes() {
		return includes;
	}
	@Override
	public SchemaType getSchemaType() {
		return SchemaType.XSD;
	}


}
