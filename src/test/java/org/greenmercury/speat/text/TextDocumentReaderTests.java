package org.greenmercury.speat.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.ReaderInputSource;
import org.greenmercury.speat.io.input.StringInputSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextDocumentReaderTests {

  TextDocumentReader textDocumentReader;
  TextDocumentToStringHandler result;

  @BeforeEach
  void setUp() throws Exception {
    textDocumentReader = new TextDocumentReader();
    result = new TextDocumentToStringHandler();
    // Create pipeline.
    textDocumentReader.setHandler(result);
  }

  class TextDocumentToStringHandler implements TextDocumentApi {
    public String document;
    @Override
    public void process(CharSequence text) {
      document = text.toString();
    }
  }


  @Test
  void testReadFromReader01() throws IOException, ConfigurationException, PipelineException {
    Reader testString = new StringReader("hello world");
    textDocumentReader.setInputSource(new ReaderInputSource(testString));
    textDocumentReader.read();
    assertEquals("hello world", result.document);
    // Feed a second inputSource into the pipeline.
    testString = new StringReader("goodbye world");
    textDocumentReader.setInputSource(new ReaderInputSource(testString));
    textDocumentReader.read();
    assertEquals("goodbye world", result.document);
  }

  @Test
  void testReadFromString01() throws IOException, ConfigurationException, PipelineException {
    String testString = "hello world";
    textDocumentReader.setInputSource(new StringInputSource(testString));
    textDocumentReader.read();
    assertEquals(testString, result.document);
  }

  @Test
  void testReadFromString02() throws IOException, ConfigurationException, PipelineException {
    String testString = "hello world\nhello again";
    textDocumentReader.setInputSource(new StringInputSource(testString));
    textDocumentReader.read();
    assertEquals(testString, result.document);
  }

  @Test
  void testReadFromString03() throws IOException, ConfigurationException, PipelineException {
    String testString = "hello world\r\nhello again\rand again";
    textDocumentReader.setInputSource(new StringInputSource(testString));
    textDocumentReader.read();
    assertEquals(testString.replaceAll("\\r\\n?", "\n"), result.document);
  }

  @Test
  void testReadFromString04() throws IOException, ConfigurationException, PipelineException {
    String testString = "hello world";
    StringInputSource inputSource = new StringInputSource(testString);
    textDocumentReader.setInputSource(inputSource);
    textDocumentReader.read();
    assertEquals(testString, result.document);
    // Feed a second inputSource into the pipeline.
    testString = "hello again\r\nand again";
    inputSource.setInput(testString);
    textDocumentReader.read();
    assertEquals(testString.replaceAll("\\r\\n?", "\n"), result.document);
  }

}
