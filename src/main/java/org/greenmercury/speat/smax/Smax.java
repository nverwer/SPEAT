package org.greenmercury.speat.smax;

import java.io.IOException;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;

/**
 * This interface defines the event API for SMAX documents.
 * It contains one event handler {@code process}, which accepts a SmaxDocument instance.
 *<p>
 * @author Rakensi
 */
public interface Smax {
  public void process(SmaxDocument document) throws PipelineException, ConfigurationException, IOException;
}
