package org.greenmercury.speat.io.output;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StringOutputSource extends OutputSourceBase {

  StringWriter writer;

  public StringOutputSource() {
    writer = new StringWriter();
  }

  /**
   * Return the content as a `String`.
   * @return the content that has been written to the output
   */
  public String getOutput() {
    return writer.toString();
  }

  /**
   * @see org.greenmercury.speat.io.output.OutputSourceBase#hasWriter()
   */
  @Override
  public boolean hasWriter() {
    return true;
  }

  /**
   * @see org.greenmercury.speat.io.output.OutputSourceBase#getWriter()
   */
  @Override
  public Writer getWriter() throws UnsupportedEncodingException, IOException {
    return writer;
  }

  /**
   * Get the underlying StringBuffer.
   * @return the StringBuffer used by the StringWriter
   */
  public StringBuffer getBuffer() {
    return writer.getBuffer();
  }

}
