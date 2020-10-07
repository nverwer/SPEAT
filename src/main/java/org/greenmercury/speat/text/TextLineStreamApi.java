package org.greenmercury.speat.text;

import java.util.stream.Stream;

/**
 * Event API for a stream of text lines.
 *
 * @author Rakensi
 */
public interface TextLineStreamApi {

  /**
   * Process one line of text.
   */
  public void line(String line);

  public default void process(Stream<String> lines) {
    lines.forEachOrdered(this::line);
  }

  public default void processParallel(Stream<String> lines) {
    lines.forEach(this::line);
  }

  public default void startDocument() {
    // Do nothing by default.
  }

  public default void endDocument() {
    // Do nothing by default.
  }

}
