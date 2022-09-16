package com.meluzin.fluentxml.xml.builder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.meluzin.fluentxml.xml.xsd.XmlNode.ReferenceInfo;

public interface NodeBuilder {
	public Map<String, String> getProcessingInstructions();
	public NodeBuilder addProcessingInstruction(String name, String value);
	public NodeBuilder clearProcessingInstructions();
	public NodeBuilder removeProcessingInstruction(String name);
	public NodeBuilder copy();
	public NodeBuilder copyAttributesFrom(NodeBuilder source, String... attributes);
	public NodeBuilder appendChild(NodeBuilder newChild);
	public NodeBuilder appendChild(NodeBuilder newChild, int index);
	/**
	 * Adds default namespace
	 * @param namespace
	 * @return
	 */
	public NodeBuilder addNamespace(String namespace);
	/**
	 * Adds namespace with given prefix
	 * @param prefix
	 * @param namespace
	 * @return
	 */
	public NodeBuilder addNamespace(String prefix, String namespace);
	public NodeBuilder addAttribute(String name, String value);
	public NodeBuilder addAttribute(String name, Object value);
	public NodeBuilder addAttribute(String name, ReferenceInfo value);
	/**
	 * Adds given attribute to current node.
	 * @param prefix
	 * @param name
	 * @param value
	 * @return
	 */
	public NodeBuilder addAttribute(String prefix, String name, String value);
	public Map<String, Map<String,String>> getPrefixedAttributes();
	public String getAttribute(String name);
	public String getAttribute(String prefix, String name);
	public boolean hasAttribute(String name);
	public boolean hasAttribute(String prefix, String name);
	/**
	 * 
	 * @param name
	 * @return created child
	 */
	public NodeBuilder addChild(String name);
	public NodeBuilder addChild(String name, int index);
	/**
	 * Adds new child element with same prefix as current element
	 * @param name
	 * @param parentPrefix
	 * @return created child
	 */
	public NodeBuilder addChild(String name, boolean parentPrefix);
	public NodeBuilder addChild(AddChildAction action);
	public default NodeBuilder addChildOnlyIf(BooleanSupplier condition, AddChildAction action) {
		return addChildOnlyIf(condition.getAsBoolean(), action);
	}
	public default NodeBuilder addChildOnlyIf(boolean condition, AddChildAction action) {
		return  condition ? addChild(action) : this;
	}
	public default NodeBuilder addChild(AddChildActionReturn action) {
		return action.exec(this);
	}
	public <T> NodeBuilder addChild(T item, AddChildFunctor<T> functor);
	public NodeBuilder removeChild(int index);
	public NodeBuilder removeChild(NodeBuilder child);
	public NodeBuilder removeChildren(Predicate<NodeBuilder> criterium);
	public <T> NodeBuilder addChildren(Iterable<T> elements, AddChildFunctor<T> functor);
	public <T, C> NodeBuilder addChildren(Iterable<T> elements, C defaultContext, AddChildFunctorWithContext<T,C> functor);
	public <T, C> NodeBuilder addChildren(Stream<T> elements, C defaultContext, AddChildFunctorWithContext<T,C> functor);
	public <T> NodeBuilder addChildren(Stream<T> elementsStream, AddChildFunctor<T> functor);
	/**
	 * 
	 * @param prefix
	 * @param name
	 * @return created child
	 */
	public NodeBuilder addChild(String prefix, String name);
	public NodeBuilder addChild(ReferenceInfo ref);
	public NodeBuilder addTextChild(String text);
	public String getTextContent();
	public boolean isTextNode();
	public NodeBuilder createTextElement(String content);
	public NodeBuilder setTextContent(String innerText);
	public default NodeBuilder setTextContent(Object innerText) {
		return setTextContent(innerText == null ? null : innerText.toString());
	}
	public String getCDataContent();
	public NodeBuilder setCDataContent(String innerText);
	public NodeBuilder setPrefix(String prefix);
	public String getName();
	public NodeBuilder setName(String name);
	/**
	 * 
	 * @return prefix + ":" + name if prefix available
	 */
	public String getQualifiedName();
	public String getPrefix();
	public String getNamespace();
	public String getNamespace(String prefix);
	public String getNamespacePrefix(String namespace);
	
	public String getXPath();
	public int getChildIndex();
	/**
	 * 
	 * @return map of namespaces defined on the node
	 */
	public Map<String, String> getNamespaces();
	/**
	 * 
	 * @return map of all namespaces defined on path from root to this node 
	 */
	public Map<String, String> getAllNamespaces();
	public NodeBuilder getParent();
	public NodeBuilder getRoot();
	void setParent(NodeBuilder parent);
	public boolean hasParent();
	public boolean hasChildren();
	public List<NodeBuilder> getChildren();
	public Stream<NodeBuilder> getNonTextChildren();
	public String createQualifiedName(String prefix, String name);
	/**
	 * Creates new standalone element
	 * @param name
	 * @return
	 */
	public NodeBuilder createElement(String name);
	/**
	 * Creates new standalone element
	 * @param prefix
	 * @param name
	 * @return
	 */	
	public NodeBuilder createElement(String prefix, String name);
	public Stream<NodeBuilder> search(NodePredicate criterium);
	public Stream<NodeBuilder> search(boolean recursive, NodePredicate criterium);
	public Stream<NodeBuilder> search(String name);
	public Stream<NodeBuilder> search(boolean recursive, String name);
	public NodeBuilder searchFirst(NodePredicate criterium);
	public NodeBuilder searchFirstByName(String name);
	public NodeBuilder searchFirstByName(String name, String namespace);
	public NodeBuilder searchFirstByName(boolean recursive, String name);
	public NodeBuilder searchFirstByName(boolean recursive, String name, String namespace);
	public NodeBuilder searchFirst(boolean recursive, NodePredicate criterium);
	default public Optional<String> getTextOfFirst(String name) {
		return getTextOfFirst(false, name);
	};
	default public Optional<String> getTextOfFirst(boolean recursive, String name) {
		return search(recursive, name).findFirst().map(s -> s.getTextContent());
	};
	default public Optional<String> getTextOfFirstAttribute(String name, String attributeName) {
		return getTextOfFirstAttribute(false, name, attributeName);
	};
	default public Optional<String> getTextOfFirstAttribute(boolean recursive, String name, String attributeName) {
		return search(recursive, name).findFirst().map(s -> s.getAttribute(attributeName));
	};
	public boolean hasChild(NodePredicate criterium);
	public boolean hasChild(boolean recursive, NodePredicate criterium);
	public XmlBuilderFactory getXmlBuilderFactory();
	public String toString(boolean prettyPrint);
	public Optional<String> getFirstDiff(NodeBuilder other);
	public Optional<NodeBuilder> findNodeWithSameLocation(NodeBuilder nodeLocation);
	public interface AddChildFunctor<T> {
		public void exec(T item, NodeBuilder parent);
	}
	public interface AddChildFunctorWithContext<T,C> {
		public C exec(T item, NodeBuilder parent, C ctx);
	}
	public interface AddChildAction {
		public void exec(NodeBuilder parent);
	}

	public interface AddChildActionReturn {
		public NodeBuilder exec(NodeBuilder parent);
	}
	public interface NodePredicate extends Predicate<NodeBuilder> {}
	
	public NodeBuilder sortChildren(Comparator<NodeBuilder> comparator);

	public boolean equalsTo(NodeBuilder other);
}
