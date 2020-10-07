package org.greenmercury.speat.sax;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.ccil.cowan.tagsoup.Parser;
import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.io.input.InputSourceReader;
import org.xml.sax.SAXException;

/**
 * This reads HTML from an InputSourceEventSupplier and produces SAX events, using TagSoup [http://vrici.lojban.org/~cowan/XML/tagsoup/].
 *<p>
 * @author Rakensi
 */
public class HtmlSaxReader extends InputSourceReader<Sax> implements Pipeline<InputSource, Sax> {

  private Parser htmlParser;

  /**
   * Constructor for HtmlSaxReader.
   */
  public HtmlSaxReader() throws ParserConfigurationException {
    htmlParser = new Parser();
  }

  /**
   * Read the text document from an inputSource source.
   */
  @Override
  protected void readInputAndSendEvents() throws IOException, PipelineException {
    htmlParser.setContentHandler(handler);
    htmlParser.setDTDHandler(handler);
    htmlParser.setEntityResolver(handler);
    htmlParser.setErrorHandler(handler);
    if (inputSource.hasReader()) {
      try (Reader reader = inputSource.getReader()) {
        try {
          htmlParser.parse(new org.xml.sax.InputSource(reader));
        } catch (SAXException e) {
          throw new PipelineException(e);
        }
      }
    } else if (inputSource.hasInputStream()) {
      try (InputStream stream = inputSource.getInputStream()) {
        htmlParser.parse(new org.xml.sax.InputSource(stream));
      } catch (SAXException e) {
        throw new PipelineException(e);
      }
    } else {
      throw new PipelineException("Cannot read from inputSource source "+inputSource.toString());
    }
  }

}
