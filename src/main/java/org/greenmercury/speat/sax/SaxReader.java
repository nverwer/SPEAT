package org.greenmercury.speat.sax;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.io.input.InputSourceReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Wrapper for org.xml.sax.XMLReader for use in a SPEAT pipeline.
 * This reads XML from an InputSource and produces SAX events.
 *<p>
 * @author Rakensi
 */
public class SaxReader extends InputSourceReader<Sax> implements Pipeline<InputSource, Sax> {

  private XMLReader xmlReader;

  /**
   * Constructor for SaxReader.
   */
  public SaxReader() throws SAXException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    xmlReader = factory.newSAXParser().getXMLReader();
  }

  /**
   * Constructor for a SaxReader from a given XMLReader.
   * @param reader
   */
  public SaxReader(XMLReader reader) {
    xmlReader = reader;
  }

  /**
   * Read the text document from an inputSource source.
   */
  @Override
  protected void readInputAndSendEvents() throws IOException, PipelineException {
    xmlReader.setContentHandler(handler);
    xmlReader.setDTDHandler(handler);
    xmlReader.setEntityResolver(handler);
    xmlReader.setErrorHandler(handler);
    try {
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
      xmlReader.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
    } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
      throw new PipelineException("Cannot set SAX handler.", e);
    }
    if (inputSource.hasReader()) {
      try (
        Reader reader = inputSource.getReader()
      ) {
        xmlReader.parse(new org.xml.sax.InputSource(reader));
      } catch (SAXException e) {
        throw new PipelineException(e);
      }
    } else if (inputSource.hasInputStream()) {
      try (InputStream stream = inputSource.getInputStream()) {
        xmlReader.parse(new org.xml.sax.InputSource(stream));
      } catch (SAXException e) {
        throw new PipelineException(e);
      }
    } else {
      throw new PipelineException("Cannot read from inputSource source "+inputSource.toString());
    }
  }

}
