package org.greenmercury.speat.io.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * An {@code InputSource} based on {@code File}.
 */
public class FileInputSource extends InputSourceBase {

  private File file;

  /**
   * Create an input source that reads from a file.
   * @param file
   * @param encoding
   * Note that we use a {@code FileInputStream}, not a {@code FileReader}.
   * The javadoc of {@code FileReader} says: The constructors of this class assume that
   * the default character encoding and the default byte-buffer size are appropriate.
   * To specify these values yourself, construct an InputStreamReader on a FileInputStream.
   */
  public FileInputSource(File file, String encoding) throws IOException {
    this.file = file;
    setEncoding(encoding);
    setUrl(file.toURI().toURL());
    setInputStream(new FileInputStream(file));
  }

  /**
   * Create an input source that reads from a file, with UTF-8 encoding.
   * @param file
   * Note that a different encoding can be set later.
   */
  public FileInputSource(File file) throws IOException {
    this(file, "UTF-8");
  }

  @Override
  public long getLastModified() {
    return file.lastModified();
  }

}
