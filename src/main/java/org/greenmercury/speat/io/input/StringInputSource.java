package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * An {@code InputSource} based on {@code String}.
 */
public class StringInputSource extends InputSourceBase {

  String input;

  public StringInputSource(String input) {
    setInput(input);
  }

  /**
   * Set the input string of this source.
   * @param input
   */
  public void setInput(String input) {
    this.input = input;
    setLastModified(System.currentTimeMillis());
  }

  @Override
  public boolean hasReader() throws UnsupportedEncodingException {
    return true;
  }

  @Override
  public Reader getReader() throws UnsupportedEncodingException, IOException {
    return new StringReader(input);
  }

}
