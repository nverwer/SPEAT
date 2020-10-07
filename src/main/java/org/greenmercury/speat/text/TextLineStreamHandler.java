package org.greenmercury.speat.text;

import java.util.stream.Stream;

public interface TextLineStreamHandler {

  /**
   * Process one line of text.
   * @param line a line of text to process
   */
  public void line(String line);

  /**
   * Process multiple lines of text in their particular order.
   * @param lines a stream of lines of text to process
   */
  public default void process(Stream<String> lines) {
    lines.forEachOrdered(this::line);
  }

  /**
   * Process multiple lines of text in no particular order.
   * This may process the lines in parallel.
   * @param lines A stream of lines of text to process
   */
  public default void processParallel(Stream<String> lines) {
    lines.forEach(this::line);
  }

  /**
   * Event marking the start of a document.
   */
  public default void startDocument() {
    // Do nothing by default.
  }

  /**
   * Event marking the end of a document.
   */
  public default void endDocument() {
    // Do nothing by default.
  }

}
