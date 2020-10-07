package org.greenmercury.speat.smax.to.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.greenmercury.speat.SpeatTests;
import org.greenmercury.speat.smax.Balancing;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;
import org.junit.jupiter.api.Test;

class SmaxDocumentInsertTests extends SpeatTests {

  /**
   * Surround character spans that match a given pattern by <M> elements.
   * Unbalanced insertions are solved using a given balancing strategy.
   */
  class MatchMarkingTestTransformer extends SmaxDocumentTransformer {
    private Pattern pattern;
    private Balancing balancing;

    public MatchMarkingTestTransformer(Pattern pattern, Balancing balancing) {
      this.pattern = pattern;
      this.balancing = balancing;
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

  /**
   * Run a pipeline with a {@code MatchMarkingTestTransformer}, matching "!!!+".
   * @param balancing
   * @param inputXML
   * @return
   * @throws Exception
   */
  private String runMatchingTestPipeline(Balancing balancing, String inputXML) throws Exception {
    MatchMarkingTestTransformer transformer = new MatchMarkingTestTransformer(Pattern.compile("!!!+"), balancing);
    return runPipeline(transformer, inputXML);
  }

  String testMatch01 = "<test><p>..!!!..</p></test>";

  @Test
  void testInsertOuter01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch01);
    assertEquals("<test><p>..<M>!!!</M>..</p></test>", result);
  }
  @Test
  void testInsertInner01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch01);
    assertEquals("<test><p>..<M>!!!</M>..</p></test>", result);
  }
  @Test
  void testInsertStart01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch01);
    assertEquals("<test><p>..<M/>!!!..</p></test>", result);
  }
  @Test
  void testInsertEnd01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch01);
    assertEquals("<test><p>..!!!<M/>..</p></test>", result);
  }
  @Test
  void testInsertBalanceToStart01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch01);
    assertEquals("<test><p>..<M>!!!</M>..</p></test>", result);
  }
  @Test
  void testInsertBalanceToEnd01() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch01);
    assertEquals("<test><p>..<M>!!!</M>..</p></test>", result);
  }

  String testMatch02 = "<test><p>!!!</p></test>";
  @Test
  void testInsertOuter02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch02);
    assertEquals("<test><p><M>!!!</M></p></test>", result);
  }
  @Test
  void testInsertInner02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch02);
    assertEquals("<test><p><M>!!!</M></p></test>", result);
  }
  @Test
  void testInsertStart02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch02);
    assertEquals("<test><M/><p>!!!</p></test>", result);
  }
  @Test
  void testInsertEnd02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch02);
    assertEquals("<test><p>!!!</p><M/></test>", result);
  }
  @Test
  void testInsertBalanceToStart02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch02);
    assertEquals("<test><p><M>!!!</M></p></test>", result);
  }
  @Test
  void testInsertBalanceToEnd02() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch02);
    assertEquals("<test><p><M>!!!</M></p></test>", result);
  }

  String testMatch03 = "<test>..!<p>!</p><r/><q>!</q>!..</test>";
  @Test
  void testInsertOuter03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch03);
    assertEquals("<test>..<M>!<p>!</p><r/><q>!</q>!</M>..</test>", result);
  }
  @Test
  void testInsertInner03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch03);
    assertEquals("<test>..<M>!<p>!</p><r/><q>!</q>!</M>..</test>", result);
  }
  @Test
  void testInsertStart03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch03);
    assertEquals("<test>..<M/>!<p>!</p><r/><q>!</q>!..</test>", result);
  }
  @Test
  void testInsertEnd03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch03);
    assertEquals("<test>..!<p>!</p><r/><q>!</q>!<M/>..</test>", result);
  }
  @Test
  void testInsertBalanceToStart03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch03);
    assertEquals("<test>..<M>!<p>!</p><r/><q>!</q>!</M>..</test>", result);
  }
  @Test
  void testInsertBalanceToEnd03() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch03);
    assertEquals("<test>..<M>!<p>!</p><r/><q>!</q>!</M>..</test>", result);
  }

  String testMatch04 = "<test>..<p>!</p>!<r/>!<q>!</q>..</test>";
  @Test
  void testInsertOuter04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch04);
    assertEquals("<test>..<M><p>!</p>!<r/>!<q>!</q></M>..</test>", result);
  }
  @Test
  void testInsertInner04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch04);
    assertEquals("<test>..<M><p>!</p>!<r/>!<q>!</q></M>..</test>", result);
  }
  @Test
  void testInsertStart04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch04);
    assertEquals("<test>..<M/><p>!</p>!<r/>!<q>!</q>..</test>", result);
  }
  @Test
  void testInsertEnd04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch04);
    assertEquals("<test>..<p>!</p>!<r/>!<q>!</q><M/>..</test>", result);
  }
  @Test
  void testInsertBalanceToStart04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch04);
    assertEquals("<test>..<M><p>!</p>!<r/>!<q>!</q></M>..</test>", result);
  }
  @Test
  void testInsertBalanceToEnd04() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch04);
    assertEquals("<test>..<M><p>!</p>!<r/>!<q>!</q></M>..</test>", result);
  }

  String testMatch04a = "<test>..<pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq>..</test>";
  @Test
  void testInsertOuter04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch04a);
    assertEquals("<test>..<M><pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq></M>..</test>", result);
  }
  @Test
  void testInsertInner04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch04a);
    assertEquals("<test>..<M><pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq></M>..</test>", result);
  }
  @Test
  void testInsertStart04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch04a);
    assertEquals("<test>..<M/><pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq>..</test>", result);
  }
  @Test
  void testInsertEnd04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch04a);
    assertEquals("<test>..<pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq><M/>..</test>", result);
  }
  @Test
  void testInsertBalanceToStart04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch04a);
    assertEquals("<test>..<M><pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq></M>..</test>", result);
  }
  @Test
  void testInsertBalanceToEnd04a() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch04a);
    assertEquals("<test>..<M><pp><p>!</p></pp>!<r/>!<qq><q>!</q></qq></M>..</test>", result);
  }

  String testMatch05 = "<test><p>..</p>..!!!..<q>..</q></test>";
  @Test
  void testInsertOuter05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch05);
    assertEquals("<test><p>..</p>..<M>!!!</M>..<q>..</q></test>", result);
  }
  @Test
  void testInsertInner05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch05);
    assertEquals("<test><p>..</p>..<M>!!!</M>..<q>..</q></test>", result);
  }
  @Test
  void testInsertStart05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch05);
    assertEquals("<test><p>..</p>..<M/>!!!..<q>..</q></test>", result);
  }
  @Test
  void testInsertEnd05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch05);
    assertEquals("<test><p>..</p>..!!!<M/>..<q>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToStart05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch05);
    assertEquals("<test><p>..</p>..<M>!!!</M>..<q>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToEnd05() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch05);
    assertEquals("<test><p>..</p>..<M>!!!</M>..<q>..</q></test>", result);
  }

  String testMatch06 = "<test><p>..</p>!!!<q>..</q></test>";
  @Test
  void testInsertOuter06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch06);
    assertEquals("<test><p>..</p><M>!!!</M><q>..</q></test>", result);
  }
  @Test
  void testInsertInner06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch06);
    assertEquals("<test><p>..</p><M>!!!</M><q>..</q></test>", result);
  }
  @Test
  void testInsertStart06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch06);
    assertEquals("<test><p>..</p><M/>!!!<q>..</q></test>", result);
  }
  @Test
  void testInsertEnd06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch06);
    assertEquals("<test><p>..</p>!!!<M/><q>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToStart06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch06);
    assertEquals("<test><p>..</p><M>!!!</M><q>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToEnd06() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch06);
    assertEquals("<test><p>..</p><M>!!!</M><q>..</q></test>", result);
  }

  String testMatch07 = "<test><p>..!</p><r>!</r><q>!..</q></test>";
  @Test
  void testInsertOuter07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch07);
    assertEquals("<test><M><p>..!</p><r>!</r><q>!..</q></M></test>", result);
  }
  @Test
  void testInsertInner07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch07);
    assertEquals("<test><p>..!</p><M><r>!</r></M><q>!..</q></test>", result);
  }
  @Test
  void testInsertStart07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch07);
    assertEquals("<test><p>..<M/>!</p><r>!</r><q>!..</q></test>", result);
  }
  @Test
  void testInsertEnd07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch07);
    assertEquals("<test><p>..!</p><r>!</r><q>!<M/>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToStart07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch07);
    assertEquals("<test><p>..<M/>!</p><r>!</r><q>!..</q></test>", result);
  }
  @Test
  void testInsertBalanceToEnd07() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch07);
    assertEquals("<test><p>..!</p><r>!</r><q>!<M/>..</q></test>", result);
  }

  String testMatch08 = "<test><p>..!</p><r>!</r>!.<q>.</q></test>";
  @Test
  void testInsertOuter08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch08);
    assertEquals("<test><M><p>..!</p><r>!</r>!</M>.<q>.</q></test>", result);
  }
  @Test
  void testInsertInner08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch08);
    assertEquals("<test><p>..!</p><M><r>!</r>!</M>.<q>.</q></test>", result);
  }
  @Test
  void testInsertStart08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch08);
    assertEquals("<test><p>..<M/>!</p><r>!</r>!.<q>.</q></test>", result);
  }
  @Test
  void testInsertEnd08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch08);
    assertEquals("<test><p>..!</p><r>!</r>!<M/>.<q>.</q></test>", result);
  }
  @Test
  void testInsertBalanceToStart08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch08);
    assertEquals("<test><p>..<M/>!</p><r>!</r>!.<q>.</q></test>", result);
  }
  @Test
  void testInsertBalanceToEnd08() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch08);
    assertEquals("<test><p>..!</p><r>!</r>!<M/>.<q>.</q></test>", result);
  }

  String testMatch09 = "<test><p>.</p>.!<r>!</r><q>!..</q></test>";
  @Test
  void testInsertOuter09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.OUTER, testMatch09);
    assertEquals("<test><p>.</p>.<M>!<r>!</r><q>!..</q></M></test>", result);
  }
  @Test
  void testInsertInner09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.INNER, testMatch09);
    assertEquals("<test><p>.</p>.<M>!<r>!</r></M><q>!..</q></test>", result);
  }
  @Test
  void testInsertStart09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.START, testMatch09);
    assertEquals("<test><p>.</p>.<M/>!<r>!</r><q>!..</q></test>", result);
  }
  @Test
  void testInsertEnd09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.END, testMatch09);
    assertEquals("<test><p>.</p>.!<r>!</r><q>!<M/>..</q></test>", result);
  }
  @Test
  void testInsertBalanceToStart09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_START, testMatch09);
    assertEquals("<test><p>.</p>.<M/>!<r>!</r><q>!..</q></test>", result);
  }
  @Test
  void testInsertBalanceToEnd09() throws Exception {
    String result = runMatchingTestPipeline(Balancing.BALANCE_TO_END, testMatch09);
    assertEquals("<test><p>.</p>.!<r>!</r><q>!<M/>..</q></test>", result);
  }

  @Test
  void testInsertNamespace10() throws Exception {
    SmaxDocument document = parse("<test xmlns:m='M'><p>..!!!..</p></test>");
    SmaxElement matchNode = new SmaxElement("M", "m");
    document.insertMarkup(matchNode, Balancing.OUTER, 2, 5);
    String result = serialize(document);
    assertEquals("<test xmlns:m=\"M\"><p>..<m:m>!!!</m:m>..</p></test>", result);
  }
  @Test
  void testInsertNamespace11() throws Exception {
    SmaxDocument document = parse("<test xmlns='T' xmlns:m='M'><p>..!!!..</p></test>");
    SmaxElement matchNode = new SmaxElement("M", "m");
    document.insertMarkup(matchNode, Balancing.OUTER, 2, 5);
    String result = serialize(document);
    assertEquals("<test xmlns=\"T\" xmlns:m=\"M\"><p>..<m:m>!!!</m:m>..</p></test>", result);
  }
  @Test
  void testInsertNamespace12() throws Exception {
    SmaxDocument document = parse("<t:test xmlns:t='T' xmlns:m='M'><p>..!!!..</p></t:test>");
    SmaxElement matchNode = new SmaxElement("M", "m");
    document.insertMarkup(matchNode, Balancing.OUTER, 2, 5);
    String result = serialize(document);
    assertEquals("<t:test xmlns:t=\"T\" xmlns:m=\"M\"><p>..<m:m>!!!</m:m>..</p></t:test>", result);
  }

}
