package org.greenmercury.speat.smax.to.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.greenmercury.speat.SpeatTests;
import org.greenmercury.speat.smax.SmaxContent;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;
import org.junit.jupiter.api.Test;

class SmaxDocumentTransformerTests extends SpeatTests {
  
  /* Tests for transformWithin */
  
  class HideTextWithinTransformer extends SmaxDocumentTransformer {
    @Override
    protected void transform(SmaxDocument document) {
      SmaxContent content = document.getContentView();
      SmaxElement hideNode = new SmaxElement("x");
      document.insertMarkup(hideNode, balancing, 0, content.length());
      for (int pos = 0; pos < content.length(); ++pos) {
        content.setCharAt(pos, '#');
      }
    }
  }

  @Test
  void testWithinNodes01() throws Exception {
    String input = "<test><p>!!!</p><q>???</q></test>";
    SmaxDocumentTransformer transformer = new HideTextWithinTransformer().setTransformWithin(new SmaxElement("q"));
    String result = runPipeline(transformer, input);
    assertEquals("<test><p>!!!</p><q><x>###</x></q></test>", result);
  }

  @Test
  void testWithinNodes02() throws Exception {
    String input = "<test>a<p>!<q><q>???</q></q>!</p><q>?<r>?</r>?</q>a</test>";
    SmaxDocumentTransformer transformer = new HideTextWithinTransformer().setTransformWithin(new SmaxElement("q"));
    String result = runPipeline(transformer, input);
    assertEquals("<test>a<p>!<q><q><x>###</x></q></q>!</p><q><x>#<r>#</r>#</x></q>a</test>", result);
  }
  
  class MatchTextWithinTransformer extends SmaxDocumentTransformer {
    private Pattern pattern;
    public MatchTextWithinTransformer(Pattern pattern) {
      this.pattern = pattern;
    }
    @Override
    protected void transform(SmaxDocument document) {
      Matcher matcher = pattern.matcher(document.getContentView());
      while (matcher.find()) {
        SmaxElement matchNode = new SmaxElement("M");
        document.insertMarkup(matchNode, balancing, matcher.start(), matcher.end());
      }
    }
  }

  @Test
  void testWithinNodes11() throws Exception {
    String input = "<test>!<p>!<q><q>???</q></q>!</p><q>?<r>?</r>?</q>?</test>";
    SmaxDocumentTransformer transformer = new MatchTextWithinTransformer(Pattern.compile("[!?]+")).setTransformWithin(new SmaxElement("q"));
    String result = runPipeline(transformer, input);
    assertEquals("<test>!<p>!<q><q><M>???</M></q></q>!</p><q><M>?<r>?</r>?</M></q>?</test>", result);
  }

}
