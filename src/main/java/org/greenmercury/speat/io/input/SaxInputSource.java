package org.greenmercury.speat.io.input;

import java.io.IOException;
import java.io.Reader;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;

/**
 * An {@code InputSource} based on {@code org.xml.sax.InputSource inputSource}.
 */
public class SaxInputSource extends InputSourceBase {

  public SaxInputSource(org.xml.sax.InputSource inputSource) throws IOException, ConfigurationException, PipelineException {
    if (inputSource.getEncoding() != null) {
      setEncoding(inputSource.getEncoding());
    }
    setUrl(inputSource.getSystemId());
    setLastModified(System.currentTimeMillis());
    Reader reader = inputSource.getCharacterStream();
    if (reader != null) {
      setReader(reader);
    } else {
      setInputStream(inputSource.getByteStream());
    }
  }

}
