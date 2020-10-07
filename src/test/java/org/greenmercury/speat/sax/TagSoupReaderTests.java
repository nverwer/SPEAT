package org.greenmercury.speat.sax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.xml.transform.OutputKeys;

import org.greenmercury.speat.io.input.StringInputSource;
import org.greenmercury.speat.io.output.StringOutputSource;
import org.junit.jupiter.api.Test;

class TagSoupReaderTests {

  private String runTestPipeline(String inputXML) throws Exception {
    // Make pipeline components.
    try (
        StringInputSource input = new StringInputSource(inputXML);
        StringOutputSource outputSource = new StringOutputSource();
    ) {
      HtmlSaxReader saxReader = new HtmlSaxReader();
      SaxWriter serializer = new SaxWriter();
      serializer.setHandler(outputSource);
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      // Compose the pipeline.
      saxReader.setInputSource(input);
      saxReader.setHandler(serializer);
      // Run the pipeline.
      saxReader.read();
      return outputSource.getOutput();
    }
  }

  private String soupUp(String html) {
    return "<html xmlns:html=\"http://www.w3.org/1999/xhtml\">\n   "+html+"\n</html>";
  }

  @Test
  void testSoup01() throws Exception {
    String input = "<body>7–9</body>";
    String result = runTestPipeline(input);
    assertEquals(soupUp(input), result);
  }

  @Test
  void testSoup02() throws Exception {
    String input = "<body>7–9<br>...</body>";
    String result = runTestPipeline(input);
    assertEquals(soupUp("<body>7–9<br clear=\"none\" />...</body>"), result);
  }

}
