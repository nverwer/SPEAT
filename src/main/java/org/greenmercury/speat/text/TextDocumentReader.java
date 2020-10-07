package org.greenmercury.speat.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.io.input.InputSourceReader;

/**
 * Read a text document from an inputSource source and handle the document as a single CharSequence.
 * Normalize line endings to "&#xA;" (which is "\n"), as specified by [https://www.w3.org/TR/REC-xml/#sec-line-ends].
 * <p>
 * Note that the complete text is kept in memory as a String.
 * <p>
 *
 * @author Rakensi
 */
public class TextDocumentReader extends InputSourceReader<TextDocumentApi> implements Pipeline<InputSource, TextDocumentApi> {

  /**
   * Read the text document from an inputSource source.
   */
  @Override
  protected void readInputAndSendEvents() throws IOException, PipelineException {
    if (inputSource.hasReader()) {
      try (
          Reader reader = inputSource.getReader();
          BufferedReader buffer = new BufferedReader(reader)
          ) {
        String inputText = buffer.lines().collect(Collectors.joining("\n"));
        handler.process(inputText);
      }
    } else {
      throw new PipelineException("No reader is available for " + this.getClass().getName());
    }
  }

}
