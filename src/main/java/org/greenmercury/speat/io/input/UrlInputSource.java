package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * An {@code InputSource} based on {@code URL}.
 */
public class UrlInputSource extends InputSourceBase {

  public UrlInputSource(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    setEncoding(connection.getContentEncoding());
    setLastModified(connection.getLastModified()); //.getHeaderFieldDate("Last-Modified", 0L);
    setUrl(url);
    setInputStream(connection.getInputStream());
  }

}
