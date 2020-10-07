package org.greenmercury.speat.sax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.SpeatTests;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer;
import org.junit.jupiter.api.Test;

class SaxAdapterTests extends SpeatTests {

  /**
   * Transform a document into the first child element of the root element.
   */
  class subDocumentTransformer extends SmaxDocumentTransformer {
    @Override
    public void process(SmaxDocument document) throws ConfigurationException, PipelineException, IOException {
      handler.process(new SmaxDocument(document.getMarkup().getChildren().get(0), document.getContentView()));
    }
  }

  @Test
  void testSubDocument01() throws Exception {
    String input = "<test><p>!!!</p><q>???</q></test>";
    SmaxDocumentTransformer transformer = new subDocumentTransformer();
    String result = runPipeline(transformer, input);
    assertEquals("<p>!!!</p>", result);
  }

  @Test
  void testSubDocument02() throws Exception {
    String input = "<test>[[[<p>!!!</p><q>???</q>]]]</test>";
    SmaxDocumentTransformer transformer = new subDocumentTransformer();
    String result = runPipeline(transformer, input);
    assertEquals("<p>!!!</p>", result);
  }

  @Test
  void testSubDocument03() throws Exception {
    String input = "<test>[[[<p>!!!<q>???</q></p>]]]</test>";
    SmaxDocumentTransformer transformer = new subDocumentTransformer();
    String result = runPipeline(transformer, input);
    assertEquals("<p>!!!<q>???</q></p>", result);
  }

}
