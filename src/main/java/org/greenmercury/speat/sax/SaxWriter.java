package org.greenmercury.speat.sax;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.Pipeline;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.output.OutputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.sf.saxon.Configuration;
import net.sf.saxon.jaxp.IdentityTransformer;
import net.sf.saxon.jaxp.IdentityTransformerHandler;

/**
 * SAX serializer for use in a SPEAT pipeline.
 * This consumes SAX events and writes XML to an OutputSource.
 * It extends {@code SaxEventHandler}, and therefore cannot also extend {@code OutputSourceWriter<Sax>}.
 *<p>
 * @author Rakensi
 */
public class SaxWriter extends SaxEventHandler implements Pipeline<Sax, OutputSource> {

  private SerializerIdentityTransformerHandler serializer;
  private OutputSource outputSource = null;

  /**
   * Constructor
   */
  public SaxWriter() throws TransformerConfigurationException {
    serializer = new SerializerIdentityTransformerHandler();
    setContentHandler(serializer);
    setDTDHandler(serializer);
    setLexicalHandler(serializer);
  }

  /**
   * Set the output source and make its writer the result of the serializer.
   * We want a writer, because it has a better chance of handling character encoding correctly.
   * @param outputSource
   * @see org.greenmercury.speat.EventSupplier#setHandler(java.lang.Object)
   * @see <a href="https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/OutputKeys.html">javax.xml.transform.OutputKeys</a>
   */
  @Override
  public void setHandler(OutputSource outputSource) throws ConfigurationException, PipelineException {
    this.outputSource = outputSource;
    if (outputSource.hasWriter()) {
      StreamResult result;
      try {
        result = new StreamResult(outputSource.getWriter());
      } catch (Exception e) {
        throw new ConfigurationException("No Writer for "+outputSource.toString(), e);
      }
      serializer.setResult(result);
    } else {
      throw new ConfigurationException("No Writer for "+outputSource.toString());
    }
    if (outputSource.getEncoding() != null) {
      setOutputProperty(OutputKeys.ENCODING, outputSource.getEncoding());
    }
  }

  @Override
  public OutputSource getHandler() {
    return outputSource;
  }

  /**
   * Set output properties used by the SaxWriter, like {@code setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")}
   * @param name
   * @param value
   * @return the {@code SaxWriter} itself, for use in the builder pattern.
   * @see javax.xml.transform.OutputKeys
   */
  public SaxWriter setOutputProperty(String name, String value) {
    serializer.transformer.setOutputProperty(name, value);
    return this;
  }

  /* The SerializerIdentityTransformerHandler and SerializerIdentityTransformer
   * solve the problem that the standard TransformerHandler does not allow output properties.
   */
  private class SerializerIdentityTransformerHandler extends IdentityTransformerHandler {
    // public transformer for setting output properties
    public IdentityTransformer transformer;
    // public constructor for use by SaxWriter
    public SerializerIdentityTransformerHandler() {
      this(new SerializerIdentityTransformer(Configuration.newConfiguration()));
    }
    protected SerializerIdentityTransformerHandler(IdentityTransformer transformer) {
      super(transformer);
      this.transformer = transformer;
    }
    @Override
    public void startDocument() throws SAXException {
      if (getResult() == null) {
        throw new IllegalStateException("SaxWriter must have an outputSource before it can write.");
      }
      super.startDocument();
    }
  }

  // SerializerIdentityTransformer is needed because IdentityTransformer is not visible here.
  private class SerializerIdentityTransformer extends IdentityTransformer {
    protected SerializerIdentityTransformer(Configuration config) {
      super(config);
    }
  }

  /* Stuff that cannot be inherited from AbstractPipeline. */

  private Logger logger = null;

  @Override
  public Logger getLogger() {
    if (logger == null) {
      logger = LoggerFactory.getLogger(this.getClass());
    }
    return logger;
  }

  @Override
  public Pipeline<Sax, OutputSource> setLogger(Logger logger) {
    this.logger = logger;
    return this;
  }

}
