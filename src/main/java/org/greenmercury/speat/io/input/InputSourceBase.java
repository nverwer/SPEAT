package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Base implementation for {@code InputSource}.
 * This adds some implementations for {@code InputSource} methods and adds helper methods.
 *<p>
 * This interface is AutoCloseable because input sources must usually be closed.
 * Implementations should be used within a try-with-resources.
 *<p>
 * @author Rakensi
 */
public class InputSourceBase implements InputSource {

  private String encoding = null;
  private long lastModified = 0L;
  private URL url = null;

  // You can use the inputStream or the reader, or both, depending on what is available.
  private InputStream inputStream = null;
  private Reader reader = null;

  /**
   * Set the character encoding of the input source.
   * @param encoding the encoding to set
   */
  protected void setEncoding(String encoding) throws UnsupportedEncodingException {
    if (Charset.isSupported(encoding)) {
      this.encoding = encoding;
    } else {
      throw new UnsupportedEncodingException("Unsupported character encoding: "+encoding);
    }
  }

  /**
   * @see org.greenmercury.speat.io.input.InputSource#getEncoding()
   */
  @Override
  public String getEncoding() {
    return encoding;
  }

  /**
   * Set the last modified timestamp.
   * @param lastModified
   */
  protected void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * @see org.greenmercury.speat.io.input.InputSource#getLastModified()
   */
  @Override
  public long getLastModified() {
    return lastModified;
  }

  /**
   * Set the URL of the input source.
   * @param url the URL of the input source.
   */
  protected void setUrl(URL url) {
    this.url = url;
  }

  /**
   * @param url the URL to set
   */
  protected void setUrl(String url) throws MalformedURLException {
    setUrl(new URL(url));
  }

  /**
   * @see org.greenmercury.speat.io.input.InputSource#getUrl()
   */
  @Override
  public URL getUrl() {
    return url;
  }


  /**
   * Only the local reader and input stream can be closed.
   * Classes implementing their own reader and/or input stream must override close().
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public synchronized void close() throws IOException {
    if (reader != null) {
      reader.close();
      reader = null;
    }
    if (inputStream != null) {
      inputStream.close();
      inputStream = null;
    }
  }

  protected void setReader(Reader reader) {
    this.reader = reader;
  }

  @Override
  public boolean hasReader() throws UnsupportedEncodingException {
    if (reader != null) {
      return true;
    } else if (hasInputStream() && encoding != null) {
      // We know how to make a reader, so: true.
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method must be called only once before the reader is read.
   * Subclasses that override this method must also override {@code hasReader}.
   * @see org.greenmercury.speat.io.input.InputSource#getReader()
   */
  @Override
  public Reader getReader() throws UnsupportedEncodingException, IOException {
    if (reader != null) {
      return reader;
    } else {
      // Do not store this in `reader`, because it must be re-created when used more than once.
      return new InputStreamReader(getInputStream(), encoding);
    }
  }

  protected void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public boolean hasInputStream() {
    return inputStream != null;
  }

  /**
   * This method must be called only once before the input stream is read.
   * Subclasses that override this method must also override {@code hasInputStream}.
   * @see org.greenmercury.speat.io.input.InputSource#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException {
    return inputStream;
  }

  /**
   * The {@code String} representation of a {@code InputSourceBase} is its URL, if available.
   * @return the URL of this {@code InputSourceBase}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return (url == null ? "<InputSource>" : url.toString());
  }

}
