package org.greenmercury.speat.sax.to.smax;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.NamespacePrefixMapping;
import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.sax.Sax;
import org.greenmercury.speat.sax.SaxEventHandler;
import org.greenmercury.speat.smax.Smax;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Adapt SAX events into SMAX document events.
 *<p>
 * @see <a href="http://sax.sourceforge.net/quickstart.html">SAX</a>
 *<p>
 * @author Rakensi
 */
public class SaxToSmaxAdapter extends SaxEventHandler implements Pipeline<Sax, Smax> {

  /**
   * Shared empty attributes instance.
   */
  private Attributes emptyAttributes = new AttributesImpl();

  /**
   * Handler for {@code SmaxDocument} instances produced by this adapter.
   */
  private Smax handler;

  /**
   * The text content of the parsed document up to the current parse position.
   * For very large documents, something else than a StringBuffer must be used to hold text content.
   * A StringBuffer is used, because it is thread-safe. A StringBuilder is faster, but not thread-safe.
   */
  private StringBuffer currentContent;

  /**
   * The node that is being constructed.
   */
  private SmaxElement currentNode;

  /**
   * Ancestors of the current node.
   */
  private Stack<SmaxElement> ancestorNodes;

  /**
   * The namespaces and their prefixes that are declared for the next XML element.
   * @see <a href="http://sax.sourceforge.net/namespaces.html">SAX namespaces</a>
   */
  private List<NamespacePrefixMapping> namespaces = null;

  // Measure performance
  Instant startTime;


  /**
   * Constructor for {@code SaxToSmaxAdapter}
   */
  public SaxToSmaxAdapter() {
    handler = null;
    currentContent = new StringBuffer();
  }

  @Override
  public void setHandler(Smax handler) {
    this.handler = handler;
  }

  @Override
  public Smax getHandler() {
    return handler;
  }


  /* At the start of a document, several properties are initialized.
   * @see org.xml.sax.helpers.DefaultHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
    startTime = Instant.now();
    currentContent.setLength(0); // More efficient than constructing a new instance.
    currentNode = null;
    ancestorNodes = new Stack<>();
    namespaces = new ArrayList<NamespacePrefixMapping>(5);
  }

  /* At the end of a document, produce a {@code SmaxDocument}.
   * @see org.xml.sax.helpers.DefaultHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    Instant endTime = Instant.now();
    logger.info("Sax to Smax took "+Duration.between(startTime, endTime).toMillis()+" ms, from "+startTime.toString()+" to "+endTime.toString());
    // Pass on the current node to the next pipeline stage.
    if (handler != null) {
      SmaxDocument document = new SmaxDocument(currentNode, currentContent);
      try {
        handler.process(document);
      } catch (PipelineException | ConfigurationException | IOException e) {
        throw new SAXException(e);
      }
    }
    // Reset properties, to let the garbage collector find them.
    ancestorNodes = null;
    namespaces = null;
  }

  /* Collect the namespace declarations that are relevant for the next {@code SmaxElement}.
   * All startPrefixMapping events will occur immediately before the corresponding startElement event,
   * and all endPrefixMapping events will occur immediately after the corresponding endElement event,
   * but their order is not otherwise guaranteed.
   * @see <a href="http://sax.sourceforge.net/apidoc/org/xml/sax/ContentHandler.html#startPrefixMapping(java.lang.String,%20java.lang.String)">org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)</a>
   */
  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    super.startPrefixMapping(prefix, uri);
    if (prefix == null) {
      prefix = "";
    }
    namespaces.add(new NamespacePrefixMapping(prefix, uri));
  }

  /* No need to do anything, as {@code namespaces} is emptied in {@startElement}.
   * @see org.xml.sax.helpers.DefaultHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    super.endPrefixMapping(prefix);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    ancestorNodes.push(currentNode);
    // Most XMLReader implementations also report the original qName, but that parameter might simply be an empty string (except for elements that aren't in a namespace).
    if (qName == null || qName.length() == 0) {
      qName = localName;
    }
    // Attributes are re-used in SAX events, so we need to copy them.
    Attributes attributesCopy = attributes.getLength() > 0 ?  new AttributesImpl(attributes) : emptyAttributes;
    currentNode = new SmaxElement(uri, localName, qName, attributesCopy).
        setStartPos(currentContent.length()).
        setNamespacePrefixMappings(namespaces.toArray(new NamespacePrefixMapping[namespaces.size()]));
    // Namespaces for this element will not be registered again in child elements, so throw them away.
    namespaces.clear();
  }

  /* (non-Javadoc)
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    // Most XMLReader implementations also report the original qName, but that parameter might simply be an empty string (except for elements that aren't in a namespace).
    if (qName == null || qName.length() == 0) {
      qName = localName;
    }
    if (uri != currentNode.getNamespaceUri() || localName != currentNode.getLocalName() || qName != currentNode.getQualifiedName()) {
      throw new SAXException(this.getClass().getCanonicalName()+" does not transform SAX events correctly.");
    }
    currentNode.setEndPos(currentContent.length());
    SmaxElement parent = ancestorNodes.pop();
    if (parent != null) {
      currentNode = parent.appendChild(currentNode);
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] text, int start, int length) throws SAXException {
    currentContent.append(text, start, length);
  }

  /* Stuff that cannot be inherited from AbstractPipeline. */

  private Logger logger = null;

  @Override
  public Logger getLogger() {
    if (logger == null) {
      logger = LoggerFactory.getLogger(this.getClass());
    }
    return logger;
  }

  @Override
  public Pipeline<Sax, Smax> setLogger(Logger logger) {
    this.logger = logger;
    return this;
  }

}
