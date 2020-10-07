package org.greenmercury.speat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements pipeline methods that are not doing actual pipelining.
 * Currently, this is only logging.
 *<p>
 * @author Rakensi
 */
public abstract class AbstractPipeline<S, T> implements Pipeline<S, T> {

  private Logger logger = null;

  @Override
  public Logger getLogger() {
    if (logger == null) {
      logger = LoggerFactory.getLogger(this.getClass());
    }
    return logger;
  }

  @Override
  public Pipeline<S, T> setLogger(Logger logger) {
    this.logger = logger;
    return this;
  }

}
