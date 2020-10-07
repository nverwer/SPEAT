package org.greenmercury.speat.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.greenmercury.speat.SpeatTests;
import org.junit.jupiter.api.Test;

class SmaxElementTests extends SpeatTests {

  @Test
  void testParentNode01() throws Exception {
    String input = "<test><p></p><p></p></test>";
    SmaxDocument document = parse(input);
    assertEquals(null, document.getMarkup().getParentNode());
  }

  @Test
  void testParentNode02() throws Exception {
    String input = "<test><p></p><p></p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.matchingNodes(new SmaxElement("p"))
            .forEach(el -> sb.append(el.getParentNode().getLocalName()));
    assertEquals("testtest", sb.toString());
  }

  @Test
  void testNamespaces01() throws Exception {
    String input = "<test xmlns='T'><p xmlns='P1'></p><p xmlns='P2'></p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.getNamespaceUri()).append("."));
    assertEquals("P1.P2.", sb.toString());
  }

  @Test
  void testNamespaces02() throws Exception {
    String input = "<test xmlns='T'><p xmlns='P1'></p><p xmlns='P2'></p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.getNamespacePrefix()).append("."));
    assertEquals("..", sb.toString());
  }

  @Test
  void testNamespaces03() throws Exception {
    String input = "<test xmlns='T'><p xmlns='P1'></p><p xmlns='P2'></p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupNamespaceURI("")).append("."));
    assertEquals("P1.P2.", sb.toString());
  }

  @Test
  void testNamespaces04() throws Exception {
    String input = "<test xmlns='T'><p xmlns='P1'></p><p xmlns='P2'></p></test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupPrefix("P1")).append("."));
    assertEquals(".null.", sb.toString());
  }

  @Test
  void testNamespaces11() throws Exception {
    String input = "<t:test xmlns:t='T'><p:p xmlns:p='P1'></p:p><p:p xmlns:p='P2'></p:p></t:test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.getNamespacePrefix()).append(":").append(el.getNamespaceUri()).append("."));
    assertEquals("p:P1.p:P2.", sb.toString());
  }

  @Test
  void testNamespaces12() throws Exception {
    String input = "<t:test xmlns:t='T'><p:p xmlns:p='P1'></p:p><p:p xmlns:p='P2'></p:p></t:test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupNamespaceURI("p")).append("."));
    assertEquals("P1.P2.", sb.toString());
  }

  @Test
  void testNamespaces13() throws Exception {
    String input = "<t:test xmlns:t='T'><p:p xmlns:p='P1'></p:p><p:p xmlns:p='P2'></p:p></t:test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupNamespaceURI("t")).append("."));
    assertEquals("T.T.", sb.toString());
  }

  @Test
  void testNamespaces14() throws Exception {
    String input = "<p:test xmlns:p='T'><p1:p xmlns:p1='P'></p1:p><p2:p xmlns:p2='P'></p2:p></p:test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupPrefix("P")).append("."));
    assertEquals("p1.p2.", sb.toString());
  }

  @Test
  void testNamespaces15() throws Exception {
    String input = "<t:test xmlns:t='T'><p:p xmlns:p='P1'></p:p><p:p xmlns:p='P2'></p:p></t:test>";
    SmaxDocument document = parse(input);
    StringBuilder sb = new StringBuilder();
    document.getMarkup().getChildren()
            .forEach(el -> sb.append(el.lookupPrefix("T")).append("."));
    assertEquals("t.t.", sb.toString());
  }

}
