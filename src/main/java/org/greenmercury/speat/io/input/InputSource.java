package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * An event API for input sources.
 * This defines the methods for configuration that an {@code InputSource} must support.
 * It has no events, because it is always the beginning of a pipeline.
 * The {@code InputSourceReader<Api>} and its sub-classes like {@code TextDocumentReader} or {@code SaxReader}
 * read from an {@code InputSource}.
 *<p>
 * When reading, the character encoding tends to be a problem.
 * If there is a character stream available, the input source will read that stream directly,
 * disregarding any text encoding declaration found in that stream.
 * If there is no character stream, but there is a byte stream, the parser will use that byte stream,
 * using the encoding specified in the {@code InputSource}.
 * If no character encoding is specified, the input source uses an algorithm such as the one in the XML specification.
 *<p>
 * This interface is AutoCloseable because input sources must usually be closed.
 * Implementations should be used within a try-with-resources.
 *<p>
 * @author Rakensi
 */
public interface InputSource extends AutoCloseable {

  /**
   * Get the character encoding for this input source.
   * @return the character encoding for this input source, or null if it is not known
   */
  public String getEncoding();

  /**
   * Get the last modified timestamp, if available.
   * @return the timestamp, or {@code 0L} if no timestamp is known.
   */
  public long getLastModified();

  /**
   * @return the URL of this input source, or null if it is not known
   */
  public URL getUrl();

  /**
   * @return true if a reader is available for this input source
   */
  public boolean hasReader() throws UnsupportedEncodingException;

  /**
   * @return the reader of this input source, if available
   */
  public Reader getReader() throws UnsupportedEncodingException, IOException;

  /**
   * @return true if an inputStream is available for this input source.
   */
  public boolean hasInputStream();

  /**
   * @return the inputStream of this input source. Use this if no reader is available.
   */
  public InputStream getInputStream() throws IOException;

}
