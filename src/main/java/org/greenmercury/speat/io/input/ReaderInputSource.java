package org.greenmercury.speat.io.input;

import java.io.Reader;

/**
 * An {@code InputSource} based on {@code Reader}.
 */
public class ReaderInputSource extends InputSourceBase {

  public ReaderInputSource(Reader reader) {
    setLastModified(System.currentTimeMillis());
    setReader(reader);
  }

}
