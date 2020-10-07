package org.greenmercury.speat.sax;

import java.io.IOException;

import javax.xml.transform.sax.TransformerHandler;

import org.greenmercury.speat.EventHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implements the {@code Sax} interface and all of the `org.xml.sax` interfaces
 * {@code ContentHandler}, {@code DTDHandler}, {@code EntityResolver}, {@code ErrorHandler},
 * {@code DeclHandler}, {@code EntityResolver2}, {@code LexicalHandler}
 * by wrapping handler instances, or by providing no-op methods when a handler is not available.
 *<p>
 * This also implements {@code EventHandler<Sax>} by returning itself in {@code getHandler}.
 *<p>
 * @author Rakensi
 */
public class SaxEventHandler extends DefaultHandler2 implements Sax, EventHandler<Sax> {

  private ContentHandler contentHandler = null;
  private DTDHandler dtdHandler = null;
  private EntityResolver entityResolver = null;
  private ErrorHandler errorHandler = null;
  private DeclHandler declHandler = null;
  private EntityResolver2 entityResolver2 = null;
  private LexicalHandler lexicalHandler = null;

  @Override
  public Sax getEventApi() {
    return this;
  }

  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  public void setDTDHandler(DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  public void setEntityResolver(EntityResolver2 entityResolver2) {
    entityResolver = entityResolver2;
    this.entityResolver2 = entityResolver2;
  }

  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public void setDeclHandler(DeclHandler declHandler) {
    this.declHandler = declHandler;
  }

  public void setLexicalHandler(LexicalHandler lexicalHandler) {
    this.lexicalHandler = lexicalHandler;
  }

  // Constructors.

  public SaxEventHandler() {
  }

  public <T extends DefaultHandler> SaxEventHandler(T handler) {
    setContentHandler(handler);
    setDTDHandler(handler);
    this.setEntityResolver(handler);
    setErrorHandler(handler);
  }

  public SaxEventHandler(TransformerHandler handler) {
    setContentHandler(handler);
    setDTDHandler(handler);
    setLexicalHandler(handler);
  }

  // ContentHandler methods

  /* (@see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
   */
  @Override
  public void setDocumentLocator(Locator locator) {
    if (contentHandler != null) {
      contentHandler.setDocumentLocator(locator);
    }
  }

  /* (@see org.xml.sax.ContentHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
    if (contentHandler != null) {
      contentHandler.startDocument();
    }
  }

  /* (@see org.xml.sax.ContentHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    if (contentHandler != null) {
      contentHandler.endDocument();
    }
  }

  /* (@see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
   */
  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    if (contentHandler != null) {
      contentHandler.startPrefixMapping(prefix, uri);
    }
  }

  /* (@see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    if (contentHandler != null) {
      contentHandler.endPrefixMapping(prefix);
    }
  }

  /* (@see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
    if (contentHandler != null) {
      contentHandler.startElement(namespaceUri, localName, qualifiedName, attributes);
    }
  }

  /* (@see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String namespaceUri, String localName, String qualifiedName) throws SAXException {
    if (contentHandler != null) {
      contentHandler.endElement(namespaceUri, localName, qualifiedName);
    }
  }

  /* (@see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] characters, int start, int length) throws SAXException {
    if (contentHandler != null) {
      contentHandler.characters(characters, start, length);
    }
  }

  /* (@see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  @Override
  public void ignorableWhitespace(char[] characters, int start, int length) throws SAXException {
    if (contentHandler != null) {
      contentHandler.ignorableWhitespace(characters, start, length);
    }
  }

  /* (@see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
   */
  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    if (contentHandler != null) {
      contentHandler.processingInstruction(target, data);
    }
  }

  /* (@see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
   */
  @Override
  public void skippedEntity(String name) throws SAXException {
    if (contentHandler != null) {
      contentHandler.skippedEntity(name);
    }
  }

  // DTDHandler methods

  /* (@see org.xml.sax.DTDHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    if (dtdHandler != null) {
      dtdHandler.notationDecl(name, publicId, systemId);
    }
  }

  /* (@see org.xml.sax.DTDHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
    if (dtdHandler != null) {
      dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
    }
  }

  // EntityResolver methods

  /* (@see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
   */
  @Override
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    if (entityResolver != null) {
      return entityResolver.resolveEntity(publicId, systemId);
    } else {
      return null;
    }
  }

  // ErrorHandler methods

  /* (@see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  @Override
  public void warning(SAXParseException exception) throws SAXException {
    if (errorHandler != null) {
      errorHandler.warning(exception);
    }
  }

  /* (@see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
   */
  @Override
  public void error(SAXParseException exception) throws SAXException {
    if (errorHandler != null) {
      errorHandler.error(exception);
    }
  }

  /* (@see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    if (errorHandler != null) {
      errorHandler.fatalError(exception);
    }
  }

  // DeclHandler methods

  /* (@see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
   */
  @Override
  public void elementDecl(String name, String model) throws SAXException {
    if (declHandler != null) {
      declHandler.elementDecl(name, model);
    }
  }

  /* (@see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
    if (declHandler != null) {
      declHandler.attributeDecl(eName, aName, type, mode, value);
    }
  }

  /* (@see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
   */
  @Override
  public void internalEntityDecl(String name, String value) throws SAXException {
    if (declHandler != null) {
      declHandler.internalEntityDecl(name, value);
    }
  }

  /* (@see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
    if (declHandler != null) {
      declHandler.externalEntityDecl(name, publicId, systemId);
    }
  }

  // EntityResolver2 methods

  /* (@see org.xml.sax.ext.EntityResolver2#getExternalSubset(java.lang.String, java.lang.String)
   */
  @Override
  public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
    if (entityResolver2 != null) {
      return entityResolver2.getExternalSubset(name, baseURI);
    } else {
      return null;
    }
  }

  /* (@see org.xml.sax.ext.EntityResolver2#resolveEntity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
    if (entityResolver2 != null) {
      return entityResolver2.resolveEntity(name, publicId, baseURI, systemId);
    } else {
      return null;
    }
  }

  // LexicalHandler methods

  /* (@see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void startDTD(String name, String publicId, String systemId) throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.startDTD(name, publicId, systemId);
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#endDTD()
   */
  @Override
  public void endDTD() throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.endDTD();
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
   */
  @Override
  public void startEntity(String name) throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.startEntity(name);
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
   */
  @Override
  public void endEntity(String name) throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.endEntity(name);
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#startCDATA()
   */
  @Override
  public void startCDATA() throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.startCDATA();
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#endCDATA()
   */
  @Override
  public void endCDATA() throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.endCDATA();
    }
  }

  /* (@see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
   */
  @Override
  public void comment(char[] characters, int start, int length) throws SAXException {
    if (lexicalHandler != null) {
      lexicalHandler.comment(characters, start, length);
    }
  }

}
