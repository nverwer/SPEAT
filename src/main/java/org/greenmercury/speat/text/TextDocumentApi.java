package org.greenmercury.speat.text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Event API for text documents.
 *
 * @author Rakensi
 */
public interface TextDocumentApi {

  public void process(CharSequence text) throws UnsupportedEncodingException, IOException;

}
