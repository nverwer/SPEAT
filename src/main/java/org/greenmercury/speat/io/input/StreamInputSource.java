package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@code InputSource} based on {@code InputStream}.
 */
public class StreamInputSource extends InputSourceBase {

  public StreamInputSource(InputStream inputStream) throws IOException {
    this(inputStream, null);
  }

  public StreamInputSource(InputStream inputStream, String encoding) throws IOException {
    setEncoding(encoding);
    setLastModified(System.currentTimeMillis());
    setInputStream(inputStream);
  }

}
