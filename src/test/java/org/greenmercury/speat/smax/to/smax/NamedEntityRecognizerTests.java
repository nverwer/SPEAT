package org.greenmercury.speat.smax.to.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.xml.transform.OutputKeys;

import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.io.input.StringInputSource;
import org.greenmercury.speat.io.output.StringOutputSource;
import org.greenmercury.speat.sax.SaxReader;
import org.greenmercury.speat.sax.SaxWriter;
import org.greenmercury.speat.sax.to.smax.SaxToSmaxAdapter;
import org.greenmercury.speat.smax.Balancing;
import org.greenmercury.speat.smax.SmaxElement;
import org.greenmercury.speat.smax.to.sax.SmaxToSaxAdapter;
import org.junit.jupiter.api.Test;

class NamedEntityRecognizerTests {

  String testNER(String grammar, String input, SmaxElement matchNodeTemplate) throws Exception {
    if (matchNodeTemplate == null) {
      matchNodeTemplate = new SmaxElement(null, "ntt", "ntt").setAttribute("name", "");
    }
    try ( InputSource grammarSource = new StringInputSource(grammar);
          InputSource inputSource = new StringInputSource(input);
          StringOutputSource output = new StringOutputSource();
        ) {
      SmaxDocumentTransformer ner =
        new NamedEntityRecognizer(grammarSource, "-/()[].,;:'\"", null, null).
        setCaseInsensitiveMinLength(3).
        setMatchNodeTemplate(matchNodeTemplate).
        setBalancing(Balancing.OUTER);
      SaxReader saxReader = new SaxReader(); // Pipeline<InputSource, Sax>
      saxReader.setInputSource(inputSource);
      SaxWriter saxWriter = new SaxWriter(); // Pipeline<Sax, OutputSource>
      saxWriter.setHandler(output);
      saxWriter.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      // Create pipeline.
      saxReader
        .append(new SaxToSmaxAdapter())
        .append(ner)
        .append(new SmaxToSaxAdapter())
        .append(saxWriter);
      saxReader.read();
      return output.getOutput();
    }
  }

  @Test
  void testNER01() throws Exception {
    String grammar = "#ff0000 <- red";
    String input = "<test>The color is red.</test>";
    String result = testNER(grammar, input, null);
    assertEquals("<test>The color is <ntt name=\"#ff0000\">red</ntt>.</test>", result);
  }

  @Test
  void testNER02() throws Exception {
    String grammar = "#ff0000 <- red\n"+
                     "#00ff00 <- green\n"+
                     "#0000ff <- blue\n";
    String input = "<test>The colors are <em>red</em>, <em>green</em> and <em>blue</em>.</test>";
    String result = testNER(grammar, input, null);
    assertEquals("<test>The colors are <em><ntt name=\"#ff0000\">red</ntt></em>, <em><ntt name=\"#00ff00\">green</ntt></em> and <em><ntt name=\"#0000ff\">blue</ntt></em>.</test>", result);
  }

  @Test
  void testNER03() throws Exception {
    String grammar = "#ff0000 <- red";
    String input = "<test>The color is <a>white, red or green</a>.</test>";
    String result = testNER(grammar, input, null);
    assertEquals("<test>The color is <a>white, <ntt name=\"#ff0000\">red</ntt> or green</a>.</test>", result);
  }

  @Test
  void testNER04() throws Exception {
    String grammar = "#ff0000 <- red";
    String input = "<test>The color is \u2013 red.</test>"; // high unicode
    String result = testNER(grammar, input, null);
    assertEquals("<test>The color is \u2013 <ntt name=\"#ff0000\">red</ntt>.</test>", result);
  }

  @Test
  void testNER05() throws Exception {
    String grammar = "http://dbpedia.org/page/Prague <- praha\tprague\n"+
        "http://dbpedia.org/page/Rome <- roma\trome\n"+
        "http://dbpedia.org/page/XML <- XML\n"+
        "http://www.xmlprague.cz <- XML Prague\n"+
        "http://dbpedia.org/page/Category:Markup_languages <- markup\tmarkup language\n";
    String input = "<p style=\"font-weight: normal;\"><strong><a href=\"https://www.youtube.com/playlist?list=PLQpqh98e9RgUPCljUxZ7C82xEyywqAA24\" target=\"_blank\">Watch videos from XML Prague 2018 on Youtube channel</a>.</strong></p>";
    SmaxElement matchNodeTemplate = new SmaxElement(null, "a", "a");
    matchNodeTemplate.setAttribute("title", "Added by SMAXNER").setAttribute("name", "");
    String expect = "<p style=\"font-weight: normal;\"><strong><a href=\"https://www.youtube.com/playlist?list=PLQpqh98e9RgUPCljUxZ7C82xEyywqAA24\" target=\"_blank\">Watch videos from <a title=\"Added by SMAXNER\" name=\"http://www.xmlprague.cz\">XML Prague</a> 2018 on Youtube channel</a>.</strong></p>";
    String result = testNER(grammar, input, matchNodeTemplate);
    assertEquals(expect, result);
  }

  @Test
  void testCharacterNormalization() throws Exception {
    String grammar = "there! <- voila\n"+
        "coffee <- cafe\n"+
        "hotel <- hotel\n"+
        "received <- recu\n"+
        "ancestor <- aieul\n";
    String input = "<p>Voilà! Le café de l'hôtel est reçu par l'aïeul.</p>";
    String expect = "<p><ntt name=\"there!\">Voilà</ntt>! Le <ntt name=\"coffee\">café</ntt> de l'<ntt name=\"hotel\">hôtel</ntt> est <ntt name=\"received\">reçu</ntt> par l'<ntt name=\"ancestor\">aïeul</ntt>.</p>";
    String result = testNER(grammar, input, null);
    assertEquals(expect, result);
  }

  @Test
  void testSpaceNormalization() throws Exception {
    String grammar = "#ff0000 <- color red\n"+
        "#00ff00 <- color green\n"+
        "#0000ff <- color    blue\n";
    // In the input, "colorred" is an intentional misspelling, which must not be matched as "color red".
    String input = "<p>I like it colorred with the color  red, the color blue\nand the color \n\tgreen.</p>";
    String expect = "<p>I like it colorred with the <ntt name=\"#ff0000\">color  red</ntt>, "+
                    "the <ntt name=\"#0000ff\">color blue</ntt>\n"+
                    "and the <ntt name=\"#00ff00\">color \n\tgreen</ntt>.</p>";
    String result = testNER(grammar, input, null);
    assertEquals(expect, result);
  }

  @Test
  void testNonWordCharacters() throws Exception {
    String grammar = "QD <- Q & D"; // Because of the space, Q and D must be separated.
    assertEquals("<p><ntt name=\"QD\">Q &amp; D</ntt></p>", testNER(grammar, "<p>Q &amp; D</p>", null));
    assertEquals("<p><ntt name=\"QD\">Q&amp; D</ntt></p>", testNER(grammar, "<p>Q&amp; D</p>", null));
    assertEquals("<p><ntt name=\"QD\">Q &amp;D</ntt></p>", testNER(grammar, "<p>Q &amp;D</p>", null));
    assertEquals("<p><ntt name=\"QD\">Q&amp;D</ntt></p>", testNER(grammar, "<p>Q&amp;D</p>", null));
  }

  @Test
  void testRefreshGrammar() throws Exception {
    String grammar1 =
        "#ff0000 <- red\n"+
        "#00ff00 <- green\n"+
        "#0000ff <- blue\n";
    String grammar2 =
        "#f00 <- red\n"+
        "#0f0 <- green\n"+
        "#00f <- blue\n";
    String input = "<test>The colors are <em>red</em>, <em>green</em> and <em>blue</em>.</test>";
    SmaxElement matchNodeTemplate = new SmaxElement(null, "ntt", "ntt").setAttribute("name", "");
    try (
        StringInputSource grammarSource = new StringInputSource(grammar1);
        StringInputSource inputSource = new StringInputSource(input);
        StringOutputSource outputSource = new StringOutputSource();
      ) {
      SmaxDocumentTransformer ner =
        new NamedEntityRecognizer(grammarSource, null, null, null).
        setMatchNodeTemplate(matchNodeTemplate).
        setBalancing(Balancing.OUTER);
      SaxReader saxReader = new SaxReader();
      saxReader.setInputSource(inputSource);
      SaxWriter saxWriter = new SaxWriter();
      saxWriter.setHandler(outputSource);
      saxWriter.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      // Create pipeline.
      saxReader
        .append(new SaxToSmaxAdapter())
        .append(ner)
        .append(new SmaxToSaxAdapter())
        .setHandler(saxWriter);
      saxReader.read();
      assertEquals("<test>The colors are <em><ntt name=\"#ff0000\">red</ntt></em>, <em><ntt name=\"#00ff00\">green</ntt></em> and <em><ntt name=\"#0000ff\">blue</ntt></em>.</test>",
          outputSource.getOutput());
      // Clear the output, and run again with another grammar.
      outputSource.getBuffer().setLength(0);
      grammarSource.setInput(grammar2);
      saxReader.read();
      assertEquals("<test>The colors are <em><ntt name=\"#f00\">red</ntt></em>, <em><ntt name=\"#0f0\">green</ntt></em> and <em><ntt name=\"#00f\">blue</ntt></em>.</test>",
          outputSource.getOutput());
    }
  }

}
