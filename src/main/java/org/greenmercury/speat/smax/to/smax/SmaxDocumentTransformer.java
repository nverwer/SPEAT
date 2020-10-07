package org.greenmercury.speat.smax.to.smax;

import java.io.IOException;

import org.greenmercury.speat.AbstractPipeline;
import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.smax.Balancing;
import org.greenmercury.speat.smax.Smax;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;

/**
 * Base class for SMAX-to-SMAX document transformers.
 * Sub-classes should override {@code transform(SmaxDocument document)}.
 * The {@code transform()} method can use the {@code SmaxDocument.insertMarkup()} method to transform the markup of the document.
 *<p>
 * When {@code setTransformWithin()} is used, only sub-documents will be transformed.
 *<p>
 * @author Rakensi
 */
/**
 *
 * @author Rakensi
 */
/**
 *
 * @author Rakensi
 */
public abstract class SmaxDocumentTransformer extends AbstractPipeline<Smax, Smax> implements Smax {

  /**
   * An element that specifies which parts of the incoming document will be transformed.
   * The {@code transform()} method is only applied to sub-documents that match this element.
   */
  protected SmaxElement transformWithinNode;

  /**
   * The handler for the output document of this transformer.
   */
  protected Smax handler;

  /**
   * Sub-classes can use the document that is being transformed.
   * This will always be the complete document, even if {@code setTransformWithin()} is used.
   */
  protected SmaxDocument completeDocument;

  /**
   * The default balancing used for node insertion.
   * This will be Balancing.OUTER by default.
   */
  protected Balancing balancing = Balancing.OUTER;

  /**
   * @see org.greenmercury.speat.EventHandler#getEventApi()
   */
  @Override
  public Smax getEventApi() {
    return this;
  }

  /**
   * @param transformWithinNode the pattern node that specifies sub-documents that will be transformed.
   */
  public SmaxDocumentTransformer setTransformWithin(SmaxElement transformWithinNode) throws ConfigurationException {
    if (transformWithinNode.getChildren().size() > 0 || transformWithinNode.getAttributes().getLength() > 0) {
      throw new ConfigurationException("The value of transformWithinNode must be exactly one root-element without attributes.");
    }
    this.transformWithinNode = transformWithinNode;
    return this;
  }

  @Override
  public void setHandler(Smax handler) {
    this.handler = handler;
  }

  @Override
  public Smax getHandler() {
    return handler;
  }

  /**
   * Set the default balancing.
   * @param balancing
   */
  public SmaxDocumentTransformer setBalancing(Balancing balancing) {
    this.balancing = balancing;
    return this;
  }

  /**
   * Transform a SMAX document, or sub-document if {@code setTransformWithin()} is used.
   * Sub-classes must implement this method to transform the document, using the insertMarkup() method.
   * By default, the document is passed on unchanged.
   * @param document the (sub-)document that will be transformed
   */
  protected void transform(SmaxDocument document) throws ConfigurationException, PipelineException {
    // Do nothing.
  }

  /**
   * Accept, @code{transform} and pass on a document.
   * If {@code transformWithinNode} is defined, only transform the matching parts of the document.
   *<p>
   * A {@code SmaxDocumentTransformer} instance must be able to {@code process} multiple documents in sequence, but not in parallel.
   * Therefore it is synchronized, so {@code this.completeDocument} is consistent.
   * @param completeDocument
   * @see org.greenmercury.speat.smax.Smax#process(org.greenmercury.speat.smax.SmaxDocument)
   */
  @Override
  public synchronized void process(SmaxDocument completeDocument) throws ConfigurationException, PipelineException, IOException {
    this.completeDocument = completeDocument;
    if (transformWithinNode == null) {
      transform(completeDocument);
    } else {
      for (SmaxElement subMarkup : completeDocument.matchingNodes(transformWithinNode)) {
        transform(new SmaxDocument(subMarkup, completeDocument.getContentView()));
      }
    }
    // Pass on to the handler.
    handler.process(completeDocument);
  }

}
