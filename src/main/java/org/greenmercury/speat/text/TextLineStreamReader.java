package org.greenmercury.speat.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.io.input.InputSourceReader;

/**
 * Read a text document from a source and handle the document as a stream of
 * lines. - Normalize line endings to "&#xA;" (which is "\n"), as specified by
 * [https://www.w3.org/TR/REC-xml/#sec-line-ends].
 * <p>
 *
 * @author Rakensi
 */
public class TextLineStreamReader extends InputSourceReader<TextLineStreamApi> implements Pipeline<InputSource, TextLineStreamApi> {

  private boolean processParallel;

  public TextLineStreamReader() {
    this(false);
  }

  public TextLineStreamReader(boolean processParallel) {
    this.processParallel = processParallel;
  }

  @Override
  protected void readInputAndSendEvents() throws IOException, PipelineException {
    if (inputSource.hasReader()) {
      try (
        Reader reader = inputSource.getReader();
        BufferedReader buffer = new BufferedReader(reader)
      ) {
        if (processParallel) {
          handler.processParallel(buffer.lines());
        } else {
          handler.process(buffer.lines());
        }
      }
    } else {
      throw new PipelineException("No reader is available for " + this.getClass().getName());
    }
  }

}
