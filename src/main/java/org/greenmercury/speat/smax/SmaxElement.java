package org.greenmercury.speat.smax;

import java.util.ArrayList;
import java.util.List;

import org.greenmercury.speat.Attribute;
import org.greenmercury.speat.NamespacePrefixMapping;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Representation of an XML element in the markup part of SMAX.
 *<p>
 * @author Rakensi
 *<p>
 * "If you want creativity, take a zero off your budget. If you want sustainability, take off two zeros." - Jaime Lerner
 */
public class SmaxElement {
  /**
   * The usual properties of an XML element.
   */
  private String namespaceUri;
  private String namespacePrefix;
  private String localName;
  private String qualifiedName;
  private AttributesImpl attributes;

  /**
   * An array of namespace prefix mappings, declared on this element.
   * See {@code lookupPrefix} for namespace lookup and inheritance.
   */
  private NamespacePrefixMapping[] namespacePrefixMappings;

  /**
   * The start and end character position of the node.
   * These are int, because the capacity of a StringBuffer (used for text content in SmaxDocument) is int.
   * The {@code startpos} is just before the first character in the text,
   * and the {@code endPos} is just after the last character in the text.
   */
  private int startPos;
  private int endPos;

  /**
   * The parent node (a SmaxElement) of this element, if there is one.
   */
  private SmaxElement parentNode;

  /**
   * Children of this node, which are child elements in the XML.
   * They are ordered according to document order.
   * Their startPos - endPos ranges do not overlap.
   */
  private List<SmaxElement> children;

  /**
   * Constructor for a {@code SmaxElement} without namespace, and without attributes.
   * @param localName
   */
  public SmaxElement(String localName) {
    this(null, localName, localName, null);
  }

  /**
   * Constructor for a {@code SmaxElement} that uses the element name to derive local name and namespace prefix.
   * @param namespaceUri
   * @param qualifiedName
   */
  public SmaxElement(String namespaceUri, String qualifiedName) {
    this(namespaceUri, qualifiedName.contains(":") ? qualifiedName.substring(qualifiedName.indexOf(':')+1) : qualifiedName, qualifiedName, null);
  }

  /**
   * Constructor for a {@code SmaxElement} that uses the element name to derive local name and namespace prefix.
   * @param namespaceUri
   * @param qualifiedName
   * @param attributes
   */
  public SmaxElement(String namespaceUri, String qualifiedName, Attributes attributes) {
    this(namespaceUri, qualifiedName.contains(":") ? qualifiedName.substring(qualifiedName.indexOf(':')+1) : qualifiedName, qualifiedName, attributes);
  }

  /**
   * Constructor for a {@code SmaxElement}.
   * @param namespaceUri
   * @param localName
   * @param qualifiedName
   */
  public SmaxElement(String namespaceUri, String localName, String qualifiedName) {
    this(namespaceUri, localName, qualifiedName, null);
  }

  /**
   * Constructor for a {@code SmaxElement}.
   * @param namespaceUri
   * @param localName
   * @param qualifiedName
   * @param attributes
   */
  public SmaxElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) {
    setName(namespaceUri, localName, qualifiedName);
    setAttributes(attributes);
    startPos = 0;
    endPos = 0;
    children = new ArrayList<>();
  }

  /**
   * Make a shallow copy (with no children) of a {@code SmaxElement}.
   * This is useful when a {@code SmaxElement} is used as a template for new {@code SmaxElement}s.
   * @return a copy of the {@code SmaxElement} that can be changed without affecting the original.
   */
  public SmaxElement shallowCopy() {
    return new SmaxElement(namespaceUri, localName, qualifiedName, attributes);
  }

  /**
   * @return the start position
   */
  public int getStartPos() {
    return startPos;
  }

  /**
   * Set the start position of the element.
   * @param startPos the startPos to set
   * Warning: Using this method may corrupt the structure of the markup of a document.
   */
  public SmaxElement setStartPos(int startPos) {
    this.startPos = startPos;
    return this;
  }

  /**
   * @return the endPos
   */
  public int getEndPos() {
    return endPos;
  }

  /**
   * Set the end position of the element.
   * @param endPos the endPos to set
   * Warning: Using this method may corrupt the structure of the markup of a document.
   */
  public SmaxElement setEndPos(int endPos) {
    this.endPos = endPos;
    return this;
  }

  /**
   * @return the namespaceUri
   */
  public String getNamespaceUri() {
    return namespaceUri;
  }

  /**
   * @return the namespacePrefix
   */
  public String getNamespacePrefix() {
    return namespacePrefix;
  }

  /**
   * Find out if the element has a namespace prefix.
   * @return whether the element has a namespace prefix.
   */
  public boolean hasNamespacePrefix() {
    return namespacePrefix != null && namespacePrefix.length() > 0;
  }

  /**
   * @return the localName
   */
  public String getLocalName() {
    return localName;
  }

  /**
   * @return the qualified name
   */
  public String getQualifiedName() {
    return qualifiedName;
  }

  /**
   * Set the name of the SmaxElement.
   * @param namespaceUri
   * @param localName
   * @param qualifiedName
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement setName(String namespaceUri, String localName, String qualifiedName) {
    this.namespaceUri = namespaceUri != null ? namespaceUri : "";
    namespacePrefix = qualifiedName.contains(":") ? qualifiedName.substring(0, qualifiedName.indexOf(':')) : "";
    this.localName = localName;
    this.qualifiedName = qualifiedName;
    return this;
  }

  /**
   * @return the attributes
   */
  public Attributes getAttributes() {
    return attributes;
  }

  /**
   * @param attributes
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement setAttributes(Attributes attributes) {
    this.attributes = attributes != null ? new AttributesImpl(attributes) : new AttributesImpl();
    return this;
  }

  /**
   * Add an attribute to this SMAX node.
   * @param namespaceUri
   * @param localName
   * @param qualifiedName
   * @param type The attribute type is one of the strings "CDATA", "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES", or "NOTATION" (always in upper case).
   * @param value
   * @return  the {@code SmaxElement} itself, now with an extra attribute
   */
  public SmaxElement setAttribute(String namespaceUri, String localName, String qualifiedName, String type, String value) {
    int attrIndex = attributes.getIndex(namespaceUri, localName);
    if (attrIndex < 0) {
      attributes.addAttribute(namespaceUri, localName, qualifiedName, type, value);
    } else {
      attributes.setAttribute(attrIndex, namespaceUri, localName, qualifiedName, type, value);
    }
    return this;
  }

  /**
   * Add a CDATA attribute without namespace to this SMAX node.
   * @param localName
   * @param value
   * @return  the {@code SmaxElement} itself
   */
  public SmaxElement setAttribute(String localName, String value) {
    return this.setAttribute("", localName, localName, "CDATA", value);
  }

  /**
   * Add a CDATA attribute with namespace to this SMAX node.
   * @param namespaceUri
   * @param localName
   * @param qualifiedName
   * @param value
   * @return  the {@code SmaxElement} itself
   */
  public SmaxElement setAttribute(String namespaceUri, String localName, String qualifiedName, String value) {
    return this.setAttribute(namespaceUri, localName, qualifiedName, "CDATA", value);
  }

  /**
   * Get the namespace prefix mappings.
   * @return an array of namespace prefix mappings
   */
  public NamespacePrefixMapping[] getNamespacePrefixMappings() {
    if (namespacePrefixMappings != null) {
      return namespacePrefixMappings;
    } else {
      return new NamespacePrefixMapping[0];
    }
  }

  /**
   * Set the namespace prefix mappings for this element.
   * @param namespacePrefixMappings
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement setNamespacePrefixMappings(NamespacePrefixMapping[] namespacePrefixMappings) {
    this.namespacePrefixMappings = namespacePrefixMappings;
    return this;
  }

  /**
   * Look up the prefix associated to the given namespace URI, starting from this node and moving up in the node tree.
   * @param namespaceURI
   * @return the namespace prefix, or null if it has not been declared.
   */
  public String lookupPrefix(String namespaceURI) {
    if (namespacePrefixMappings != null) {
      for (NamespacePrefixMapping nspMapping : namespacePrefixMappings) {
        if (nspMapping.uri.equals(namespaceURI)) {
          return nspMapping.prefix;
        }
      }
    }
    if (parentNode != null) {
      return parentNode.lookupPrefix(namespaceURI);
    } else {
      return null;
    }
  }

  /**
   * Look up the namespace URI associated to the given prefix, starting from this node.
   * @param prefix
   * @return the namespace uri, or null if it has not been declared.
   */
  public String lookupNamespaceURI(String prefix) {
    if (namespacePrefixMappings != null) {
      for (NamespacePrefixMapping nspMapping : namespacePrefixMappings) {
        if (nspMapping.prefix.equals(prefix)) {
          return nspMapping.uri;
        }
      }
    }
    if (parentNode != null) {
      return parentNode.lookupNamespaceURI(prefix);
    } else {
      return null;
    }
  }

  /**
   * Get the parent element of this element.
   * @return the parent element, or {@code null} if there is none.
   */
  public SmaxElement getParentNode() {
    return parentNode;
  }

  /**
   * Set the parent element of this element.
   * @param parentNode
   * @return the {@code SmaxElement} itself
   */
  protected SmaxElement setParentNode(SmaxElement parentNode) {
    this.parentNode = parentNode;
    return this;
  }

  /**
   * @return the children of this node in document order
   */
  public List<SmaxElement> getChildren() {
    return children;
  }

  /**
   * @param children the children (in document order) to set
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement setChildren(List<SmaxElement> children) {
    for (SmaxElement child : children) {
      child.setParentNode(this);
    }
    this.children = children;
    return this;
  }

  /**
   * Add a child to this node.
   * @param child the new child node, which comes after the existing children in document order
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement appendChild(SmaxElement child) {
    child.setParentNode(this);
    children.add(child);
    return this;
  }

  /**
   * Insert a child at the given index.
   * @param index
   * @param child
   * @return the {@code SmaxElement} itself
   */
  public SmaxElement insertChild(int index, SmaxElement child) {
    child.setParentNode(this);
    children.add(index, child);
    return this;
  }

  /**
   * Remove the children between the specified fromIndex, inclusive, and toIndex, exclusive.
   * @param fromIndex
   * @param toIndex
   * @return the children that have been removed
   */
  public List<SmaxElement> removeChildren(int fromIndex, int toIndex) {
    List<SmaxElement> orphans = new ArrayList<>(children.subList(fromIndex, toIndex));
    children.subList(fromIndex, toIndex).clear();
    for (SmaxElement orphan : orphans) {
      orphan.setParentNode(null);
    }
    return orphans;
  }

  /**
   * Use toString() for debugging, not for serializing.
   */
  @Override
  public String toString() {
    return String.format("<%s %d..%d>", qualifiedName, startPos, endPos);
  }

  /**
   * Find out if this element matches a pattern-element.
   * @param pattern a {@code SmaxElement} without children (they will be ignored)
   * @return whether the name and attributes of this element match with those of the {@code pattern}.
   */
  public boolean matches(SmaxElement pattern) {
    Attributes na = getAttributes();
    return getNamespaceUri().equals(pattern.getNamespaceUri()) &&
           getLocalName().equals(pattern.getLocalName()) &&
           Attribute.stream(pattern.getAttributes())
             .allMatch(pa -> pa.getValue().equals(na.getValue(pa.getURI(), pa.getLocalName())));
  }


}
