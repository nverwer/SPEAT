package org.greenmercury.speat.io.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

/**
 * An event API for output sources.
 * This defines the methods for configuration that an {@code OutputSource} must support.
 * It produces no events, because it is always the end of a pipeline.
 * The {@code OutputSourceWriter<Api>} and its sub-classes write to an {@code OutputSource}.
 *<p>
 * This interface is AutoCloseable because output sources must usually be closed.
 * Implementations should be used within a try-with-resources.
 *<p>
 * @author Rakensi
 */
public interface OutputSource extends AutoCloseable {

  /**
   * Get the character encoding for this output source.
   * @return the character encoding for this output source, or null if it is not known
   */
  public String getEncoding();

  /**
   * @return the URL of this output source, or null if it is not known
   */
  public URL getUrl();

  /**
   * @return true if a writer is available for this output source
   */
  public boolean hasWriter();

  /**
   * @return the writer of this output source, if available
   */
  public Writer getWriter() throws UnsupportedEncodingException, IOException;

  /**
   * @return true if an OutputStream is available for this output source.
   */
  public boolean hasOutputStream();

  /**
   * @return the OutputStream of this output source. Use this if no writer is available.
   */
  public OutputStream getOutputStream() throws IOException;

}
