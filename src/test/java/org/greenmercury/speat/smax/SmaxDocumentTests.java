package org.greenmercury.speat.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.greenmercury.speat.SpeatTests;
import org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer;
import org.junit.jupiter.api.Test;

class SmaxDocumentTests extends SpeatTests {
  
  /* Tests for matchingNodes */
  
  @Test
  void testMatchingNodes01() throws Exception {
    String input = "<test><p>!!!</p>#<p>!!!</p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.matchingNodes(new SmaxElement("p"))
            .forEach(el -> sb.append(el.toString()));
    assertEquals("<p 0..3><p 4..7>", sb.toString());
  }
  
  @Test
  void testMatchingNodes02() throws Exception {
    String input = "<test><p a=''>!!!</p>#<p>!!!</p>#<p a=''>!!!</p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.matchingNodes(new SmaxElement("p").setAttribute("a", ""))
            .forEach(el -> sb.append(el.toString()));
    assertEquals("<p 0..3><p 8..11>", sb.toString());
  }
  
  @Test
  void testMatchingNodes03() throws Exception {
    String input = "<test><p a=''>!!!</p><r>#<p>!!!</p>#<p a=''>!!!</p></r></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.matchingNodes(new SmaxElement("p").setAttribute("a", ""))
            .forEach(el -> sb.append(el.toString()));
    assertEquals("<p 0..3><p 8..11>", sb.toString());
  }

  class HideTextInMatchingNodesTransformer extends SmaxDocumentTransformer {
    private SmaxElement secret;
    public HideTextInMatchingNodesTransformer(SmaxElement secret) {
      this.secret = secret;
    }
    @Override
    protected void transform(SmaxDocument document) {
      SmaxContent content = document.getContentView();
      for (SmaxElement secret : document.matchingNodes(secret)) {
        SmaxElement hideNode = new SmaxElement("x");
        document.insertMarkup(hideNode, balancing, secret.getStartPos(), secret.getEndPos());
        for (int pos = secret.getStartPos(); pos < secret.getEndPos(); ++pos) {
          content.setCharAt(pos, '#');
        }
      }
    }
  }

  @Test
  void testMatchingNodes11() throws Exception {
    String input = "<test><p>!!!</p><q>???</q></test>";
    SmaxDocumentTransformer transformer = new HideTextInMatchingNodesTransformer(new SmaxElement("q"));
    String result = runPipeline(transformer, input);
    assertEquals("<test><p>!!!</p><q><x>###</x></q></test>", result);
  }

  @Test
  void testMatchingNodes12() throws Exception {
    String input = "<test>a<p>!<q>???</q>!</p><q>?<r>?</r>?</q>a</test>";
    SmaxDocumentTransformer transformer = new HideTextInMatchingNodesTransformer(new SmaxElement("q"));
    String result = runPipeline(transformer, input);
    assertEquals("<test>a<p>!<q><x>###</x></q>!</p><q><x>#<r>#</r>#</x></q>a</test>", result);
  }

  /* Tests for ancestorNodes */
  
  class SetAncestorPathTransformer extends SmaxDocumentTransformer {
    private Pattern pattern;
    private Balancing balancing;
    public SetAncestorPathTransformer(Pattern pattern, Balancing balancing) {
      this.pattern = pattern;
      this.balancing = balancing;
    }
    @Override
    protected void transform(SmaxDocument document) {
      Matcher matcher = pattern.matcher(document.getContentView());
      while (matcher.find()) {
        List<String> path = document.ancestorNodes(matcher.start())
                                    .map(node -> node.getQualifiedName())
                                    .collect(Collectors.toList());
        SmaxElement matchNode = new SmaxElement("M");
        matchNode.setAttribute("path", String.join("/", path));
        document.insertMarkup(matchNode, balancing, matcher.start(), matcher.end());
      }
    }
  }

  @Test
  void testAncestorNodes01() throws Exception {
    String input = "<test><p>!</p></test>";
    SmaxDocumentTransformer transformer = new SetAncestorPathTransformer(Pattern.compile("!"), Balancing.OUTER);
    String result = runPipeline(transformer, input);
    assertEquals("<test><p><M path=\"test/p\">!</M></p></test>", result);
  }

  @Test
  void testAncestorNodes02() throws Exception {
    String input = "<test><p><a>!</a><b>!</b></p></test>";
    SmaxDocumentTransformer transformer = new SetAncestorPathTransformer(Pattern.compile("!"), Balancing.OUTER);
    String result = runPipeline(transformer, input);
    assertEquals("<test><p><a><M path=\"test/p/a\">!</M></a><b><M path=\"test/p/b\">!</M></b></p></test>", result);
  }

  @Test
  void testAncestorNodes03() throws Exception {
    String input = "<test><p><a>!</a></p><p><b>!</b></p></test>";
    SmaxDocumentTransformer transformer = new SetAncestorPathTransformer(Pattern.compile("!"), Balancing.OUTER)
        .setTransformWithin(new SmaxElement("p"));
    String result = runPipeline(transformer, input);
    assertEquals("<test><p><a><M path=\"p/a\">!</M></a></p><p><b><M path=\"p/b\">!</M></b></p></test>", result);
  }

}
