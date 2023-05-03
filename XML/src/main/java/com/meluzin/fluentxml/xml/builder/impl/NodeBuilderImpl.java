package com.meluzin.fluentxml.xml.builder.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.meluzin.fluentxml.xml.builder.Context;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.ReferenceInfoImpl;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;
import com.meluzin.functional.BaseRecursiveIterator;
import com.meluzin.functional.ChildrenAccessor;
import com.meluzin.functional.Log;
import com.meluzin.functional.T;
import com.meluzin.functional.T.V2;
import com.meluzin.functional.T.V3;

public class NodeBuilderImpl implements NodeBuilder {
	private Map<String, String> processingInstructions = new HashMap<>();
	private static final String TEXT_NODE_NAME = "#text";
	private String name;
	private String prefix;
	private String content;
	private String cdata;
	private NodeBuilderImpl parent;
	private Map<String, String> namespaces = new HashMap<String, String>();
	private Map<String, Map<String, String>> prefixedAttributes = new HashMap<String, Map<String, String>>();
	private List<NodeBuilder> children = new ArrayList<NodeBuilder>();
	protected XmlBuilderFactory factory;
	private boolean textNode = false;
	static int index = 0;
	public NodeBuilderImpl(XmlBuilderFactory factory, String prefix, String name) {
		this.factory = factory;
		this.prefix = prefix;
		this.name = name;
		//addAttribute("id", ++index);
	}	
	public NodeBuilderImpl(NodeBuilderImpl parent, String prefix, String name) {
		this.factory = parent.factory;
		this.parent = parent;
		this.prefix = prefix;
		this.name = name;
		//addAttribute("id", index);
	}
	@Override
	public NodeBuilder addNamespace(String namespace) {
		namespaces.put(null, namespace);
		return this;
	}

	@Override
	public NodeBuilder addNamespace(String prefix, String namespace) {
		namespaces.put(prefix, namespace);
		return this;
	}

	@Override
	public NodeBuilder addAttribute(String name, String value) {
		return addAttribute(null, name, value);
	}

	@Override
	public NodeBuilder addAttribute(String prefix, String name, String value) {
		//if (value != null) 
		{ 
			if (!prefixedAttributes.containsKey(prefix)) prefixedAttributes.put(prefix, new LinkedHashMap<String, String>());			
			prefixedAttributes.get(prefix).put(name, value);
		}
		return this;
	}
	
	@Override
	public NodeBuilder addAttribute(String name, ReferenceInfo value) {
		addAttribute(name, value == null ? null : value.createQualifiedName(this));
		return this;
	}

	@Override
	public NodeBuilder addChild(String name) {			
		return addChild(getPrefix(), name);
	}

	@Override
	public NodeBuilder addChild(String name, boolean parentPrefix) {			
		return addChild(parentPrefix ? getPrefix(): null, name);
	}

	@Override
	public NodeBuilder addChild(String prefix, String name) {
		NodeBuilder child = createElement(prefix, name);
		children.add(child);		
		return child;
	}
	@Override
	public NodeBuilder addChild(ReferenceInfo ref) {		
		return addChild(ref.createPrefix(this), ref.getLocalName());
	}
	@Override
	public NodeBuilder addChild(String name, int index) {
		return appendChild(createElement(getPrefix(), name), index);
	}
	@Override
	public NodeBuilder removeChild(int index) {
		if (index >= 0 && index < children.size()) {
			children.remove(index);
		}
		else if ( children.size() == 0) {
			throw new IllegalArgumentException("Node has no child");
		}
		else {
			throw new IllegalArgumentException(String.format("Child index %d out of bounds [%d,%d]", index, 0, children.size() - 1));
		}
		return this;
	}
	@Override
	public NodeBuilder removeChild(NodeBuilder child) {
		if (children.contains(child)) {
			children.remove(child);
		}
		else if (child == null) {
			throw new IllegalArgumentException("Cannot remove null");			
		}
		else {
			throw new IllegalArgumentException("Node does not contain given child: " + child.getXPath() + " from parent: " + getXPath() );			
		}
		return this;
	}
	@Override
	public NodeBuilder removeChildren(Predicate<NodeBuilder> criterium) {
		List<NodeBuilder> nodesToRemove = children.stream().filter(criterium).collect(Collectors.toList());
		nodesToRemove.forEach(n -> removeChild(n));
		return this;
	}
	private boolean equalsString(String a, String b) {
		return a == b || (a != null && a.equals(b));
	}
	@Override
	public int getChildIndex() {
		if (hasParent()) {
			for (int i = 0; i < parent.children.size(); i++) {
				if (parent.children.get(i) == this) return i;
			}
			throw new RuntimeException("Not found in parent! " + this.getName());
		}
		else return 0;
	}
	
	public int getXPathLocation(NodeBuilder node) {
		NodePredicate sameElements = n -> equalsString(n.getNamespace(), node.getNamespace()) && equalsString(n.getName(), node.getName()) && n.getChildIndex() < node.getChildIndex();
		if (!node.hasParent()) {
			return 0;
		} else {
			Stream<NodeBuilder> search = node.getParent().search(sameElements);
		    
			long count = search.count();
			return (int)count;			
		}		
	}
	
	@Override
	public String getXPath() {
		long countOfSameElements = getXPathLocation(this);
		String part = String.format("/%s[%d]", getQualifiedName(), countOfSameElements + 1);
		if (!hasParent()) return part;
		else {
			return getParent().getXPath() + part;
		}
	}

	@Override
	public NodeBuilder setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public NodeBuilder setName(String name) {
		this.name = name;
		return this;
	}
	@Override
	public String getPrefix() {
		return prefix;
	}
	@Override
	public NodeBuilder getParent() {
		return parent;
	}
	@Override
	public boolean hasParent() {
		return parent != null;
	}
	@Override
	public boolean hasChildren() {
		return children.size() > 0;
	}
	@Override
	public List<NodeBuilder> getChildren() {
		return children;
	}
	@Override
	public Stream<NodeBuilder> getNonTextChildren() {
		return children.stream().filter(n -> !n.isTextNode());
	}
	@Override
	public String getNamespace() {
		Map<String, String> allNamespaces = getAllNamespaces();
		return allNamespaces.get(getPrefix());
	}
	@Override
	public String getNamespace(String prefix) {
		Map<String, String> allNamespaces = getAllNamespaces();
		return allNamespaces.get(prefix);
	}
	@Override
	public Map<String, String> getNamespaces() {
		return namespaces;
	}
	@Override
	public Map<String, String> getAllNamespaces() {
		Map<String, String> allNamespaces = new HashMap<String, String>();
		if (hasParent()) {
			allNamespaces.putAll(getParent().getAllNamespaces());
		}
		allNamespaces.putAll(getNamespaces());
		return allNamespaces;
	}
	@Override
	public String getQualifiedName() {
		return createQualifiedName(getPrefix(), getName());
	}
	@Override
	public Map<String, Map<String, String>> getPrefixedAttributes() {
		return prefixedAttributes;
	}
	@Override
	public String createQualifiedName(String prefix, String name) {
		return (prefix != null ? prefix + ":" : "") + name;
	}
	@Override
	public NodeBuilder setTextContent(String innerText) {
		this.content = innerText;
		return this;
	}
	@Override
	public String getTextContent() {
		return content;
	}	
	@Override
	public String getCDataContent() {
		return cdata;
	}
	@Override
	public NodeBuilder setCDataContent(String cdata) {
		this.cdata = cdata;
		return this;
	}
	@Override
	public String toString() {
		return toString(true);
	}
	@Override
	public XmlBuilderFactory getXmlBuilderFactory() {
		return factory;
	}
	@Override
	public <TChild> NodeBuilder addChildren(Iterable<TChild> elements,
			AddChildFunctor<TChild> functor) {
		for (TChild t : elements) {
			functor.exec(t, this);
		}
		return this;
	}
	@Override
	public <TChild> NodeBuilder addChildren(Stream<TChild> elementsStream, AddChildFunctor<TChild> functor) {
		elementsStream.forEach(item -> functor.exec(item, this));
		return this;
	}
	@Override
	public <TChild, C> NodeBuilder addChildren(Iterable<TChild> elements, C defaultContext, AddChildFunctorWithContext<TChild,C> functor) {
		Context<C> ctx = new Context<C>(defaultContext);
		elements.forEach(e -> {
			C c = functor.exec(e, this, ctx.getValue());
			ctx.setValue(c);
		});
		return this;
	}
	@Override
	public <TChild, C> NodeBuilder addChildren(Stream<TChild> elements, C defaultContext, AddChildFunctorWithContext<TChild,C> functor) {
		Context<C> ctx = new Context<C>(defaultContext);
		elements.forEach(e -> {
			C c = functor.exec(e, this, ctx.getValue());
			ctx.setValue(c);
		});
		return this;
	}
	@Override
	public NodeBuilder addTextChild(String text) {
		NodeBuilder child = createTextElement(text);
		children.add(child);		
		return child;
	}
	@Override
	public NodeBuilder createTextElement(String content) {
		NodeBuilderImpl el = new NodeBuilderImpl(this, null, TEXT_NODE_NAME);
		el.textNode = true;
		return el.setTextContent(content);
	}
	@Override
	public NodeBuilder createElement(String name) {
		return createElement(null, name);
	}
	@Override
	public NodeBuilder createElement(String prefix, String name) {
		return new NodeBuilderImpl(this, prefix, name);
	}
	@Override
	public void setParent(NodeBuilder parent) {
		this.parent = (NodeBuilderImpl)parent;
	}
	@Override
	public NodeBuilder appendChild(NodeBuilder newChild) {
		return appendChild(newChild, children.size());
	}
	@Override
	public NodeBuilder appendChild(NodeBuilder newChild, int index) {
		children.add(index, newChild);
		newChild.setParent(this);
		return newChild;
	}
	@Override
	public NodeBuilder getRoot() {
		return hasParent() ? getParent().getRoot() : this;
	}
	@Override
	public String getAttribute(String name) {			
		return getAttribute(null, name);
	}
	@Override
	public String getAttribute(String prefix, String name) {
		Map<String, Map<String, String>> prefixedAttributes = getPrefixedAttributes();
		Map<String, String> givenPrefixAttributes = prefixedAttributes.get(prefix);
		return givenPrefixAttributes == null ? null : givenPrefixAttributes.get(name);
	}
	@Override
	public boolean hasAttribute(String name) {
		return hasAttribute(null, name);
	}
	@Override
	public boolean hasAttribute(String prefix, String name) {
		return getAttribute(prefix, name) != null;
	}
	@Override
	public Stream<NodeBuilder> search(NodePredicate criterium) {
		return search(false, criterium);
	}
	@Override
	public Stream<NodeBuilder> search(final boolean recursive,
			final NodePredicate criterium) {
		final NodeBuilder currentItem = this;
		Iterable<NodeBuilder> i = (new Iterable<NodeBuilder>() {
			private Iterator<NodeBuilder> iterator = new BaseRecursiveIterator<NodeBuilder>(new ChildrenAccessor<NodeBuilder>() {
				@Override
				public List<NodeBuilder> getChildren(NodeBuilder currentItem) {
					return currentItem.getChildren();
				}
			}, criterium, recursive, currentItem);
			@Override
			public Iterator<NodeBuilder> iterator() {
				return iterator; 
			}
		});
		return StreamSupport.stream(i.spliterator(), false);
	}
	
	@Override
	public Stream<NodeBuilder> search(boolean recursive, String name) {
		return search(recursive, n -> equalsString(name, n.getName()));
	}
	
	@Override
	public Stream<NodeBuilder> search(String name) {
		return search(n -> equalsString(name, n.getName()));
	}
	@Override
	public NodeBuilder searchFirst(boolean recursive,
			NodePredicate criterium) {
		return search(recursive, criterium).findAny().orElse(null);
	}
	@Override
	public NodeBuilder searchFirst(NodePredicate criterium) {
		return searchFirst(false, criterium);
	}
	@Override
	public boolean hasChild(NodePredicate criterium) {
		return searchFirst(false, criterium) != null;
	}
	@Override
	public boolean hasChild(boolean recursive, NodePredicate criterium) {
		return searchFirst(recursive, criterium) != null;
	}
	@Override
	public NodeBuilder addAttribute(String name, Object value) {
		String attrValue;
		if (value == null) {
			attrValue = null;
		} else if (value instanceof Date) {
			attrValue = Log.XSD_DATETIME_FORMATTER.format(value);
		} else {
			attrValue = value.toString();
		}
		
		return addAttribute(name, attrValue);
	}
	@Override
	public String toString(boolean prettyPrint) {
		return factory.renderNode(this, prettyPrint);
	}
	@Override
	public NodeBuilder copyAttributesFrom(NodeBuilder source, String... attributes) {
		if (source != null && attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				String name = attributes[i];
				addAttribute(name, source.getAttribute(name));
			}		
		}
		return this;
	}
	@Override
	public NodeBuilder copy() {
		final NodeBuilderImpl n = new NodeBuilderImpl(factory, getPrefix(), getName());
		n.content = content;
		n.cdata = cdata;
		n.name = name;
		n.prefix = prefix;
		n.namespaces.putAll(namespaces);
		n.textNode = textNode;
		for (String p: prefixedAttributes.keySet()) {
			n.prefixedAttributes.put(p, new HashMap<>(prefixedAttributes.get(p)));
		}			
		for (NodeBuilder child: getChildren()) {
			NodeBuilderImpl childImpl = (NodeBuilderImpl)child.copy();
			childImpl.parent = n;
			n.children.add(childImpl);
		}
		return n;
	}
	@Override
	public String getNamespacePrefix(String namespace) {
		Map<String, String> namespaces = getAllNamespaces();
		for (String prefix : namespaces.keySet()) {
			if (namespace != null && namespace.equals(namespaces.get(prefix))) return prefix;
			else if (namespace == namespaces.get(prefix)) return prefix; // namespace == null
		}
		return null;
	}
	@Override
	public <TChild> NodeBuilder addChild(TChild item, AddChildFunctor<TChild> functor) {
		functor.exec(item, this);
		return this;
	}
	@Override
	public NodeBuilder addChild(AddChildAction action) {
		action.exec(this);
		return this;
	}
	@Override
	public NodeBuilder searchFirstByName(String name) {
		return searchFirstByName(false, name);
	}
	@Override
	public NodeBuilder searchFirstByName(String name, String namespace) {
		return searchFirstByName(false, name, namespace);
	}
	@Override
	public NodeBuilder searchFirstByName(boolean recursive, String name) {
		return searchFirst(recursive, n -> (name == null && n.getName() == null) || (name != null && name.equals(n.getName())));
	}
	@Override
	public NodeBuilder searchFirstByName(boolean recursive, String name, String namespace) {
		return searchFirst(recursive, n -> 
			((name == null && n.getName() == null) || (name != null && name.equals(n.getName()))) &&
			((namespace == null && n.getNamespace() == null) || (namespace != null && namespace.equals(n.getNamespace())))
		);
	}
	@Override
	public NodeBuilder sortChildren(Comparator<NodeBuilder> comparator) {
		children.sort(comparator);
		return this;
	}
	@Override
	public boolean isTextNode() {
		return textNode;
	}
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdata == null) ? 0 : cdata.hashCode());
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((namespaces == null) ? 0 : namespaces.hashCode());
		//result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((prefixedAttributes == null) ? 0 : prefixedAttributes.hashCode());
		result = prime * result + (textNode ? 1231 : 1237);
		return result;
	}*/
	@Override
	public Optional<String> getFirstDiff(NodeBuilder obj) {
		if (!equalsTo(obj)) {
			if (obj == null)
				return Optional.of("Other object is null");
			if (getClass() != obj.getClass())
				return Optional.of("Other object is different class: " + obj.getClass().getName());
			NodeBuilderImpl other = (NodeBuilderImpl) obj;
			if (name == null) {
				if (other.name != null)
					return Optional.of(getXPath() + " - Other has here name: " + other.name);
			} else if (!name.equals(other.name))
				return Optional.of(getXPath() + " - Other has here different name: " + name + " != " + other.name);
			if (namespaces == null) {
				if (other.namespaces != null)
					return Optional.of(getXPath() + " - Other has here namespaces");
			} else if (!namespaces.equals(other.namespaces))
				return Optional.of(getXPath() + " - Other has here different namespaces");
			if (cdata == null) {
				if (other.cdata != null)
					return Optional.of(getXPath() + " - Other has here cdata");
			} else if (!cdata.equals(other.cdata))
				return Optional.of(getXPath() + " - Other has here different cdata");
			if (prefix == null) {
				if (other.prefix != null)
					return Optional.of(getXPath() + " - Other has here prefix");
			} else if (!prefix.equals(other.prefix))
				return Optional.of(getXPath() + " - Other has here different prefix");
			if (prefixedAttributes == null) {
				if (other.prefixedAttributes != null)
					return Optional.of(getXPath() + " - Other has here prefixed attributes");
			} else if (!prefixedAttributes.equals(other.prefixedAttributes))
				return getAttributesDiff(other);
			if (textNode != other.textNode)
				return Optional.of(getXPath() + " - Other has here textnode");
			if (content == null || content.trim().isEmpty()) {
				if (other.content != null && !other.content.trim().isEmpty())
					return Optional.of(getXPath() + " - Other has here textcontent: " + other.content);
			} else if (!content.trim().equals(other.content == null ? "" : other.content.trim()))
				return Optional.of(getXPath() + " - Other has here different textcontent: " + content + " != " + other.content);
			if (children == null) {
				if (other.children != null)
					return Optional.of(getXPath() + " - Other has here children");
			} else if (!isChildrenListEqual(other))
			{
				/*if (children.size() != other.children.size()) {
					return Optional.of(getXPath() + " - Other has here different children count");
				}*/
				for (int i = 0; i < Math.min(children.size(), other.children.size()); i++) {
					NodeBuilder child = children.get(i);
					NodeBuilder otherChild = other.children.get(i);
					if (!child.equalsTo(otherChild)) {
						
						return child.getFirstDiff(otherChild);
					}					
				}
				if (children.size() > other.children.size()) {
					return Optional.of(getXPath() + " - this has "+ (children.size() - other.children.size()) + " more children");
				}
				if (children.size() < other.children.size()) {
					return Optional.of(getXPath() + " - other has "+ (other.children.size() - children.size()) + " more children");
				}
			}
			/*if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;*/
		}
		return Optional.empty();
	}
	private Optional<String> getAttributesDiff(NodeBuilderImpl other) {
		Set<ReferenceInfo> otherPrefixes = getAttributes(other.prefixedAttributes);
		Set<ReferenceInfo> prefixes = getAttributes(prefixedAttributes);
		if (otherPrefixes.equals(prefixes)) {
			
		}
		else {
			Set<ReferenceInfo> diff = new HashSet<>(prefixes);
			diff.removeAll(otherPrefixes);
			Set<ReferenceInfo> diffOther = new HashSet<>(otherPrefixes);
			diffOther.removeAll(prefixes);
			return Optional.of(getXPath() + " - node has here different attributes: [" + diff.stream().map(i -> i.createQualifiedName(this)).collect(Collectors.joining(", ")) + "] != [" + diffOther.stream().map(i -> i.createQualifiedName(this)).collect(Collectors.joining(", ")) + "]");				
		}
		/*if (!otherPrefixes.equals(prefixes)) {
			prefixes.removeAll(otherPrefixes);
			if (prefixes.size() > 0) {
				prefixes.r
			}
		}*/
		return Optional.of(getXPath() + " - Other has here different prefixed attributes" + other.prefixedAttributes);
	}
	private Set<ReferenceInfo> getAttributes( Map<String, Map<String, String>> prefixedAttributes) {
		return prefixedAttributes.keySet().stream().
			map(prefix ->  prefixedAttributes.get(prefix).entrySet().stream().map(v -> T.V(prefix, v))).
			flatMap(m -> m).
			map(m -> new ReferenceInfoImpl(getNamespace(m.getA()),  m.getB().getKey() + ( m.getB().getValue() != null ? "="  + m.getB().getValue() : ""))).
			//map(i -> i.createQualifiedName(this)).
			collect(Collectors.toSet());
	}
	@Override
	public boolean equalsTo(NodeBuilder obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeBuilderImpl other = (NodeBuilderImpl) obj;
		if (cdata == null) {
			if (other.cdata != null)
				return false;
		} else if (!cdata.equals(other.cdata))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!isChildrenListEqual(other))
			return false;
		if (content == null || content.trim().isEmpty()) {
			if (other.content != null && (!other.content.trim().isEmpty()))
				return false;
		} else if (!content.trim().equals(other.content == null ? "" : other.content.trim()))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespaces == null) {
			if (other.namespaces != null)
				return false;
		} else if (!namespaces.equals(other.namespaces))
			return false;
		/*if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;*/
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (prefixedAttributes == null) {
			if (other.prefixedAttributes != null)
				return false;
		} else if (!prefixedAttributes.equals(other.prefixedAttributes)) {
			if (other.prefixedAttributes == null) return false;
			Optional<V2<V3<String, String, String>, String>> found = prefixedAttributes.entrySet().stream().
					map(a -> a.getValue().entrySet().stream().map(e -> T.V(a.getKey(), e.getKey(), e.getValue()))).
					flatMap(s -> s).
					filter(s -> s.getC() != null).
					filter(s -> !s.getC().equals(getOtherAttributeValue(other, s))).
					map(s -> T.V(s, getOtherAttributeValue(other, s))).
					findFirst();
			if (found.isPresent())
				return false;
			Optional<V2<V3<String, String, String>, String>> foundOther = other.prefixedAttributes.entrySet().stream().
					map(a -> a.getValue().entrySet().stream().map(e -> T.V(a.getKey(), e.getKey(), e.getValue()))).
					flatMap(s -> s).
					filter(s -> s.getC() != null).
					filter(s -> !s.getC().equals(getOtherAttributeValue(this, s))).
					map(s -> T.V(s, getOtherAttributeValue(this, s))).
					findFirst();
			if (foundOther.isPresent())
				return false;
		}
		if (textNode != other.textNode)
			return false;
		return true;
	}
	protected String getOtherAttributeValue(NodeBuilderImpl other, V3<String, String, String> thisAttributeWithValue) {
		return other.prefixedAttributes.getOrDefault(thisAttributeWithValue.getA(), new HashMap<>()).get(thisAttributeWithValue.getB());
	}
	private boolean isChildrenListEqual(NodeBuilderImpl other) {
		if (children == other.children) return true;
		if (children == null || other.children == null) return false;
		if (children.size() !=  other.children.size()) return false;
		for (int i = 0; i < children.size(); i++) {
			NodeBuilder child = children.get(i);
			NodeBuilder otherChild = other.children.get(i);
			if (!child.equalsTo(otherChild)) return false;
		}
		return true;
	}
	
	/*@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof NodeBuilderImpl) {
			NodeBuilderImpl other = (NodeBuilderImpl)obj;
			
		}
		return false;
	}*/

	
	public List<NodeBuilder> getPathFromRoot(NodeBuilder node) {
		List<NodeBuilder> list = new ArrayList<>();
		do {
			list.add(node);
			node = node.getParent();
		} while (node != null);		
		Collections.reverse(list);
		List<NodeBuilder> q = new ArrayList<NodeBuilder>(list);
		return q;
	}
	
	@Override
	public Optional<NodeBuilder> findNodeWithSameLocation(NodeBuilder nodeLocation) {
		List<NodeBuilder> pathToLocation = getPathFromRoot(nodeLocation);
		return findNodeWithSameLocation(this, pathToLocation, nodeLocation);
	}
	public Optional<NodeBuilder> findNodeWithSameLocation(NodeBuilder currentNode, List<NodeBuilder> pathToLocation, NodeBuilder targetLocation) {
		
		if (currentNode.getXPath().equals(targetLocation.getXPath())) {
			return Optional.of(currentNode);
		}
		NodeBuilder nextTargetNode = pathToLocation.get(1);

		int position = getXPathLocation(nextTargetNode);
		Optional<NodeBuilder> child = currentNode.
	    		search(n -> equalsString(n.getName(), nextTargetNode.getName()) && equalsString(n.getNamespace(), nextTargetNode.getNamespace())).
	    		filter(n -> 
	    		getXPathLocation(n) == position).
	    		findAny();
	    if (child.isPresent()) {
	    	return findNodeWithSameLocation(child.get(), pathToLocation.subList(1, pathToLocation.size()), targetLocation);
	    }
	    else {
	    	return Optional.empty();
	    }
	}
	@Override
	public NodeBuilder addProcessingInstruction(String name, String value) {
		this.processingInstructions.put(name, value);
		return this;
	}
	@Override
	public NodeBuilder removeProcessingInstruction(String name) {
		this.processingInstructions.remove(name);
		return this;
	}
	@Override
	public Map<String, String> getProcessingInstructions() {
		return processingInstructions;
	}
	
	@Override
	public NodeBuilder clearProcessingInstructions() {
		processingInstructions.clear();
		return this;
	}
}