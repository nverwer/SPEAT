package org.greenmercury.speat.io.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOutputSource extends OutputSourceBase {

  private File file;

  /**
   * Create an output source that writes to a file.
   * @param file
   * @param encoding
   * Note that we use a {@code FileOutputStream}, not a {@code FileReader}.
   * The javadoc of {@code FileWriter} says: The constructors of this class assume that
   * the default character encoding and the default byte-buffer size are appropriate.
   * To specify these values yourself, construct an OutputStreamWriter on a FileOutputStream.
   */
  public FileOutputSource(File file, String encoding) throws IOException {
    this.file = file;
    setEncoding(encoding);
    setUrl(file.toURI().toURL());
    setOutputStream(new FileOutputStream(file));
  }

  /**
   * Create an output source that writes to a file, with UTF-8 encoding.
   * @param file
   * Note that a different encoding can be set later.
   */
  public FileOutputSource(File file) throws IOException {
    this(file, "UTF-8");
  }

}
