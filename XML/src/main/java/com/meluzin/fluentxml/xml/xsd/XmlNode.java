package com.meluzin.fluentxml.xml.xsd;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.meluzin.fluentxml.xml.builder.BaseSchema;
import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.NodeBuilder.AddChildFunctor;
import com.meluzin.fluentxml.xml.builder.SchemaReference;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.functional.BaseBuilder;

/**
 * Base node for Xml schema definition 
 * @author Jirka
 *
 * @param <T> helper child type, that is returned after modification 
 */
public interface XmlNode<T extends BaseBuilder<T>> extends BaseBuilder<T> {
	public XmlNode<?> getParent();
	public XmlElement asElement();
	public XmlAttribute asAttribute();
	public XmlComplexType asComplexType();
	public XmlSequence asSequence();
	public XmlChoice asChoice();
	public XmlSchema asSchema();
	public XmlExceptionType asException();
	public NodeBuilder render(NodeBuilder parent);
	public XmlNode<?> getRoot();
	public T loadFromNode(NodeBuilder node);
	public String getQualifiedName(String namespace, String localName, NodeBuilder context);
	public ReferenceInfo parseQualifiedName(String qualifiedName, NodeBuilder context);
	public String getSchemaTargetNamespace();
	public interface ReferenceInfo {
		public String getLocalName();
		public String getNamespace();
		public String createPrefix(NodeBuilder context);
		public String createQualifiedName(NodeBuilder context);
		public String createQualifiedName(NodeBuilder context, Boolean qualified);
	}
	public interface XmlGroup extends XmlNode<XmlGroup> {
		public String getName();
		public XmlGroup setName(String name);
		public String getRef();
		public XmlGroup setRef(String ref);
		public String getRefNamespace();
		public XmlGroup setRefNamespace(String refNamespace);
		public String getMinOccurs();
		public XmlGroup setMinOccurs(String minOccurs);
		public String getMaxOccurs();
		public XmlGroup setMaxOccurs(String maxOccurs);
		public XmlElement addElement(String name);
		public XmlSequence addSequence();
		public XmlAll addAll();
		public XmlNode<?> getChild();
	}
	public interface XmlElement extends XmlNode<XmlElement> {
		public String getName();
		public XmlElement setName(String name);
		public String getRef();
		public XmlElement setRef(String name);
		public String getRefNamespace();
		public XmlElement setRefNamespace(String refNamespace);
		public String getMinOccurs();
		public XmlElement setMinOccurs(String name);
		public String getMaxOccurs();
		public XmlElement setMaxOccurs(String name);
		public String getNillable();
		public XmlElement setNillable(String nillable);
		public String getType();
		public XmlElement setType(String type);
		public String getTypeNamespace();
		public XmlElement setTypeNamespace(String typeNamespace);
		public XmlComplexType addComplexType();
		public XmlComplexType getComplexType();
		public XmlSimpleType addSimpleType();
		public XmlSimpleType getSimpleType();
		public void duplicateInSchema(XmlElement targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlAny extends XmlNode<XmlAny> {
		public String getMinOccurs();
		public XmlAny setMinOccurs(String name);
		public String getMaxOccurs();
		public XmlAny setMaxOccurs(String name);
		public String getNamespace();
		public XmlAny setNamespace(String name);
		public String getProcessContents();
		public XmlAny setProcessContents(String processContents);		
	}
	public interface XmlAttribute extends XmlNode<XmlAttribute> {
		public String getName();
		public XmlAttribute setName(String name);
		public String getRef();
		public XmlAttribute setRef(String name);
		public String getRefNamespace();
		public XmlAttribute setRefNamespace(String refNamespace);
		public String getType();
		public XmlAttribute setType(String type);
		public String getUse();
		public XmlAttribute setUse(String use);
		public String getFixed();
		public XmlAttribute setFixed(String fixed);
		public String getTypeNamespace();
		public XmlAttribute setTypeNamespace(String typeNamespace);
		public XmlSimpleType addSimpleType();
		public XmlSimpleType getSimpleType();
		public void duplicateInSchema(XmlAttribute targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlType<T extends XmlNode<T>> extends XmlNode<T> {
		public String getName();
		public T setName(String name);
		public String getBaseType();
		public T setBaseType(String name);
		public String getBaseTypeNamespace();
		public T setBaseTypeNamespace(String name);
	}
	public interface XmlComplexType extends XmlType<XmlComplexType> {
		public XmlElement addElement(String name);
		public XmlAttribute addAttribute(String name);
		public XmlChoice addChoice();
		public XmlSequence addSequence();
		public XmlGroup addGroup();
		public XmlAny addAny();
		public XmlAll addAll();
		public boolean isBaseTypeSimple();
		public XmlComplexType setBaseTypeSimple(boolean isSimple);
		public List<XmlNode<?>> getChildren();
		public String getContentType();
		//public List<XmlAttribute> getAttributes();
		public void duplicateInSchema(XmlComplexType targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlSimpleType extends XmlType<XmlSimpleType> {
		public List<String> getEnumeration();
		public XmlSimpleType addEnumeration(String element);
		public XmlSimpleType addEnumeration(List<String> elements);
		public Integer getMinLength();
		public XmlSimpleType setMinLength(Integer minLength);
		public Integer getMaxLength();
		public XmlSimpleType setMaxLength(Integer maxLength);
		public Integer getLength();
		public XmlSimpleType setLength(Integer length);
		public String getPattern();
		public XmlSimpleType setPattern(String pattern);
		public String getMinInclusive();
		public String getMaxInclusive();
		public String getMinExclusive();
		public String getMaxExclusive();
		public XmlSimpleType setMinInclusive(String minInclusive);
		public XmlSimpleType setMaxInclusive(String maxInclusive);
		public XmlSimpleType setMinExclusive(String minExclusive);
		public XmlSimpleType setMaxExclusive(String maxExclusive);
		public void duplicateInSchema(XmlSimpleType targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlExceptionType extends XmlNode<XmlExceptionType> {
		public String getName();
		public XmlExceptionType setName(String name);
		public String getElementName();
		public XmlExceptionType setElementName(String name);
		public String getBaseType();
		public XmlExceptionType setBaseType(String name);
		public XmlElement addElement(String name);
		public XmlChoice addChoice();
		public List<XmlNode<?>> getChildren();
	}
	public interface XmlChoice extends XmlNode<XmlChoice> {
		public XmlElement addElement(String name);
		public XmlSequence addSequence();
		public XmlGroup addGroup();
		public List<XmlNode<?>> getChildren();		
		public void duplicateInSchema(XmlChoice targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlSequence extends XmlNode<XmlSequence> {
		public XmlElement addElement(String name);
		public XmlChoice addChoice();
		public XmlGroup addGroup();
		public XmlAny addAny();
		public List<XmlNode<?>> getChildren();		
		public void duplicateInSchema(XmlSequence targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlAll extends XmlNode<XmlAll> {
		public XmlElement addElement(String name);
		public List<XmlNode<?>> getChildren();		
		public String getMinOccurs();
		public XmlAll setMinOccurs(String minOccurs);
		public void duplicateInSchema(XmlAll targetElement, Set<String> changeToTargetNamespace);
	}
	public interface XmlSchema extends BaseSchema, XmlNode<XmlSchema> {
		public XmlComplexType addType(String name);
		public XmlAttribute addAttribute(String name);
		public XmlElement addElement(String name);
		public XmlGroup addGroup();
		public XmlExceptionType addException(String name);
		public XmlExceptionType addException(String name, String elementName);
		public List<XmlNode<?>> getElements();
		public NodeBuilder render(NodeBuilder parent, boolean sorted);
		public NodeBuilder render(XmlBuilderFactory factory);
		public NodeBuilder render(XmlBuilderFactory factory, boolean sorted);
		public String getTargetNamespace();
		public XmlSchema setTargetNamespace(String targetNamespace);
		public XmlSchema addNamespace(String prefix, String namespace);
		public XmlSchema importNamespace(String namespace);
		public XmlSchema importNamespace(String namespace, String xsd);
		public Map<String, String> getImports();
		public Map<String, String> getNamespaces();
		public XmlSimpleType addSimpleType(String name);
		public List<String> getIncludes();	
		public XmlSchema removeElement(XmlNode<?> element);
		public Boolean isElementFormQualified();
		public Boolean isAttributeFormQualified();
		public XmlSchema setElementFormQualified(Boolean qualified);
		public XmlSchema setAttributeFormQualified(Boolean qualified);
		public Optional<SchemaReference> getSchemaReference();
	}
	public interface WsdlOperation {
		public WsdlOperation setTargetNamespace(String targetNamespace);
		public String getTargetNamespace();
		public Wsdl getWsdl();
		public String getSoapAction();
		public WsdlOperation setSoapAction(String soapAction);
		public String getSoapStyle();
		public WsdlOperation setSoapStyle(String soapStyle);
		public String getName();
		public WsdlOperation setInputMessage(String msg);
		public String getInputMessage();
		public WsdlOperation setInputMessageName(String name);
		public String getInputMessageName();
		public WsdlOperation setOutputMessage(String msg);
		public String getOutputMessage();
		public WsdlOperation setOutputMessageName(String name);
		public String getOutputMessageName();
		public WsdlMessage addFault(String fault);
		public WsdlOperation addFault(String fault, WsdlMessage msg);
		public Map<String, WsdlMessage> getFaults();
		/*public WsdlOperation setFaultMessage(String msg);
		public String getFaultMessage();
		public WsdlOperation setFaultMessageName(String name);
		public String getFaultMessageName();*/
		public AddChildFunctor<WsdlOperation> getDocumentationRenderer();
		public WsdlOperation setDocumentationRenderer(AddChildFunctor<WsdlOperation> documentation);
		public AddChildFunctor<WsdlOperation> getBindingRenderer();
		public WsdlOperation setBindingRenderer(AddChildFunctor<WsdlOperation> binding);
		public boolean isDeprecated();
		public WsdlOperation setDeprecated(boolean deprecated);
	}
	public interface WsdlMessagePart extends XmlNode<WsdlMessagePart> {
		public String getName();
		public String getType();
		public String getTypeNamespace();
		public boolean isPartElementRef();
		public WsdlMessagePart setPartElementRef(boolean isPartElementRef);
		public WsdlMessagePart setName(String name);
		public WsdlMessagePart setType(String type);
		public WsdlMessagePart setTypeNamespace(String typeNamespace);
	}
	public interface WsdlMessage extends XmlNode<WsdlMessage> {
		public Wsdl getWsdl();
		public String getName();
		public WsdlMessage setName(String name);
		public List<WsdlMessagePart> getParts();
		public WsdlMessagePart addPart(String name);
		public WsdlMessagePart getPartByName(String name);
	}
	public interface Wsdl {
		public String getBinding();
		public Wsdl setBinding(String binding);	
		public String getPortType();
		public Wsdl setPortType(String portType);
		public String getPort();
		public Wsdl setPort(String port);
		public String getPortBinding();
		public Wsdl setPortBinding(String portBinding);
		public String getPortBindingNamespace();
		public Wsdl setPortBindingNamespace(String portBindingNamespace);
		public String getName();
		public Wsdl setName(String name);
		public String getTargetNamespace();
		public Wsdl setTargetNamespace(String namespace);
		public List<XmlSchema> getSchemas();
		public Wsdl addSchema(XmlSchema schema);
		public WsdlOperation addOperation(String name);
		public List<WsdlOperation> getOperations();
		public WsdlMessage addMessage(String name);
		public List<WsdlMessage> getMessages();
		public NodeBuilder render(XmlBuilderFactory factory);
		public Map<String, String> getNamespaces();
		public Wsdl addNamespace(String prefix, String namespace);
		public WsdlMessage getMessageByName(String name);
	}
	public interface WsdlDocumentationRenderer<T> {
		public void render(NodeBuilder documentationNode, T data);
	}
}
