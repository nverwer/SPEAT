package org.greenmercury.speat.io.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class OutputSourceBase implements OutputSource {

  private String encoding = null;
  private URL url = null;

  // You can use the inputStream or the reader, or both, depending on what is available.
  private OutputStream outputStream = null;
  private Writer writer = null;

  /**
   * Set the character encoding of the output source.
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
   * Get the character encoding of the output source.
   * @see org.greenmercury.speat.io.output.OutputSource#getEncoding()
   */
  @Override
  public String getEncoding() {
    return encoding;
  }

  /**
   * Set the URL of the output source.
   * @param url the URL of the input source.
   */
  protected void setUrl(URL url) {
    this.url = url;
  }

  /**
   * Set the URL of the output source.
   * @param url the URL to set
   */
  protected void setUrl(String url) throws MalformedURLException {
    setUrl(new URL(url));
  }

  /**
   * Get the URL of the output source.
   * @see org.greenmercury.speat.io.output.OutputSource#getUrl()
   */
  @Override
  public URL getUrl() {
    return url;
  }

  /**
   * Only the local writer and output stream can be closed.
   * Classes implementing their own writer and/or output stream must override close().
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public synchronized void close() throws IOException {
    if (writer != null) {
      writer.close();
      writer = null;
    }
    if (outputStream != null) {
      outputStream.close();
      outputStream = null;
    }
  }

  protected void setWriter(Writer writer) {
    this.writer = writer;
  }

  @Override
  public boolean hasWriter() {
    if (writer != null) {
      return true;
    } else if (hasOutputStream() && encoding != null) {
      // We know how to make a reader, so: true.
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method must be called only once before the writer is written.
   * Subclasses that override this method must also override {@code hasWriter}.
   * @see org.greenmercury.speat.io.output.OutputSource#getWriter()
   */
  @Override
  public Writer getWriter() throws UnsupportedEncodingException, IOException {
    if (writer != null) {
      return writer;
    } else {
      // Do not store this in `reader`, because it must be re-created when used more than once.
      return new OutputStreamWriter(getOutputStream(), encoding);
    }
  }

  protected void setOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public boolean hasOutputStream() {
    return outputStream != null;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return outputStream;
  }

  /**
   * The {@code String} representation of a {@code OutputSourceBase} is its URL, if available.
   * @return the URL of this {@code OutputSourceBase}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return (url == null ? "<OutputSource>" : url.toString());
  }

}
