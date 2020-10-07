package org.greenmercury.speat;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;

import org.greenmercury.speat.io.input.StringInputSource;
import org.greenmercury.speat.io.output.StringOutputSource;
import org.greenmercury.speat.sax.SaxReader;
import org.greenmercury.speat.sax.SaxWriter;
import org.greenmercury.speat.sax.to.smax.SaxToSmaxAdapter;
import org.greenmercury.speat.smax.Smax;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.to.sax.SmaxToSaxAdapter;
import org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class for SPEAT tests.
 *<p>
 * @author Rakensi
 */
public abstract class SpeatTests {

  protected static Path resourceDirectory;

  @BeforeAll
  static void setUpProperties() {
    resourceDirectory = Paths.get("src","test","resources").toAbsolutePath();
  }

  /**
   * Apply the given {@code transformer} to the parsed {@code inputXML}.
   * @param transformer
   * @param inputXML
   * @return the result of applying {@code transformer} to {@code inputXML}
   * @throws Exception
   */
  protected String runPipeline(SmaxDocumentTransformer transformer, String inputXML) throws Exception {
    try (
        StringInputSource input = new StringInputSource(inputXML);
        StringOutputSource outputSource = new StringOutputSource();
    ) {
      SaxReader saxReader = new SaxReader();
      saxReader.setInputSource(input);
      SaxWriter serializer = new SaxWriter();
      serializer.setHandler(outputSource);
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      saxReader.
        append(new SaxToSmaxAdapter()).
        append(transformer).
        append(new SmaxToSaxAdapter()).
        setHandler(serializer);
      saxReader.read();
      return outputSource.getOutput();
    }
  }

  /**
   * Parse the string {@code inputXML} into a {@code SmaxDocument}.
   * @param inputXML
   * @return
   * @throws Exception
   */
  protected SmaxDocument parse(String inputXML) throws Exception {
    try (StringInputSource input = new StringInputSource(inputXML)) {
      SaxReader saxReader = new SaxReader();
      saxReader.setInputSource(input);
      SmaxDocumentParser parser = new SmaxDocumentParser();
      saxReader.
        append(new SaxToSmaxAdapter()).
        setHandler(parser);
      saxReader.read();
      return parser.document;
    }
  }

  /**
   * Helper class to access an XML document as a {@code SMAXDocument}.
   */
  private class SmaxDocumentParser implements Smax {
    public SmaxDocument document = null;
    @Override
    public void process(SmaxDocument document) {
      this.document = document;
    }
  }

  /**
   * Serialize a {@code SmaxDocument} into a string.
   * @param document
   * @return
   * @throws TransformerConfigurationException
   * @throws PipelineException
   * @throws ConfigurationException
   */
  protected String serialize(SmaxDocument document) throws TransformerConfigurationException, PipelineException, ConfigurationException {
    StringOutputSource outputSource = new StringOutputSource();
    SmaxToSaxAdapter smaxToSax = new SmaxToSaxAdapter();
    SaxWriter serializer = new SaxWriter();
    serializer.setHandler(outputSource);
    serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    smaxToSax.setHandler(serializer);
    smaxToSax.process(document);
    return outputSource.getOutput();
  }

}
