package org.greenmercury.speat.smax.to.sax;

import java.time.Duration;
import java.time.Instant;

import org.greenmercury.speat.AbstractPipeline;
import org.greenmercury.speat.NamespacePrefixMapping;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.sax.Sax;
import org.greenmercury.speat.smax.Smax;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;
import org.xml.sax.SAXException;

/**
 * Convert SMAX document events into SAX events.
 *<p>
 * @author Rakensi
 */
public class SmaxToSaxAdapter extends AbstractPipeline<Smax, Sax> implements Smax {
  /** The handler for the SAX events produced by this adapter. */
  private Sax handler;
  /** The content that will be output through the SAX handler. */
  private CharSequence content;
  /** The position in the content from which text has not yet been processed. */
  private int charPos;

  @Override
  public void setHandler(Sax handler) {
    this.handler = handler;
  }

  @Override
  public Sax getHandler() {
    return handler;
  }

  @Override
  public Smax getEventApi() {
    return this;
  }

  /**
   * Convert a SMAX document into SAX events.
   * @param smaxDocument
   */
  @Override
  public void process(SmaxDocument smaxDocument) throws PipelineException {
    Instant startTime = Instant.now();
    content = smaxDocument.getContentBuffer();
    SmaxElement root = smaxDocument.getMarkup();
    charPos = root.getStartPos();
    try {
      handler.startDocument();
      processElement(root);
      Instant endTime = Instant.now();
      getLogger().info("Smax to Sax took "+Duration.between(startTime, endTime).toMillis()+" ms, from "+startTime.toString()+" to "+endTime.toString());
      handler.endDocument();
    } catch (SAXException e) {
      throw new PipelineException(e);
    }
  }

  /**
   * Convert an XML element from the SMAX document into SAX events.
   * @param markup
   */
  private void processElement(SmaxElement markup) throws SAXException {
    int startPos = markup.getStartPos();
    int endPos = markup.getEndPos();
    // Send text before element.
    if (startPos > charPos) {
      sendCharacters(charPos, startPos - charPos);
      charPos = startPos;
    }
    // Start namespace prefix mappings. See https://sourceforge.net/p/saxon/mailman/message/35548184/
    for (NamespacePrefixMapping nspMapping : markup.getNamespacePrefixMappings()) {
      handler.startPrefixMapping(nspMapping.prefix, nspMapping.uri);
    }
    // Send element and its content.
    handler.startElement(markup.getNamespaceUri(), markup.getLocalName(), markup.getQualifiedName(), markup.getAttributes());
    for (SmaxElement child : markup.getChildren()) {
      processElement(child);
    }
    if (endPos > charPos) {
      sendCharacters(charPos, endPos - charPos);
      charPos = endPos;
    }
    handler.endElement(markup.getNamespaceUri(), markup.getLocalName(), markup.getQualifiedName());
    // End namespace prefix mappings.
    for (NamespacePrefixMapping nspMapping : markup.getNamespacePrefixMappings()) {
      handler.endPrefixMapping(nspMapping.prefix);
    }
  }

  /**
   * Convert content from the SMAX document into a SAX event.
   * @param start
   * @param length
   */
  private void sendCharacters(int start, int length) throws SAXException {
    char[] out = new char[length];
    for (int i = 0; i < length; ++i) {
      out[i] = content.charAt(start+i);
    }
    handler.characters(out, 0, length);
  }

}
