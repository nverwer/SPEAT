package org.greenmercury.speat.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.SpeatTests;
import org.greenmercury.speat.io.input.FileInputSource;
import org.greenmercury.speat.io.input.StringInputSource;
import org.greenmercury.speat.io.output.FileOutputSource;
import org.greenmercury.speat.io.output.StringOutputSource;
import org.greenmercury.speat.text.TextDocumentReader;
import org.greenmercury.speat.text.TextDocumentWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileSourceTests extends SpeatTests {

  File testFile;

  @BeforeEach
  void setUp() throws Exception {
    testFile = File.createTempFile("content", ".txt");
  }

  @AfterEach
  void tearDown() throws Exception {
    if(!(testFile.delete()))
    {
      throw new Exception("Could not delete temporary file: " + testFile.getAbsolutePath());
    }
  }

  @Test
  void testFileSource01() throws IOException, ConfigurationException, PipelineException {
    String testContent = "1 2 3 test";
    try (
        StringInputSource input = new StringInputSource(testContent);
        FileOutputSource output = new FileOutputSource(testFile);
    ) {
      TextDocumentReader reader = new TextDocumentReader();
      reader.setInputSource(input);
      TextDocumentWriter writer = new TextDocumentWriter();
      writer.setHandler(output);
      reader.append(writer);
      reader.read();
    }
    // Now the file has been written.
    try (
        FileInputSource input = new FileInputSource(testFile);
        StringOutputSource output = new StringOutputSource();
    ) {
      TextDocumentReader reader = new TextDocumentReader();
      reader.setInputSource(input);
      TextDocumentWriter writer = new TextDocumentWriter();
      writer.setHandler(output);
      reader.append(writer);
      reader.read();
      String actualContent = output.getOutput();
      assertEquals(testContent, actualContent);
    }
  }

}
