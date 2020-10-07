package org.greenmercury.speat.io.output;

import org.greenmercury.speat.AbstractPipeline;
import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;

public abstract class OutputSourceWriter<Api> extends AbstractPipeline<Api, OutputSource> {

  protected OutputSource outputSource = null;

  /**
   * Set the {@code Api} handler.
   * Before events are received, the {@code outputSource} must be writable.
   * @see org.greenmercury.speat.EventSupplier#setHandler(java.lang.Object)
   */
  @Override
  public void setHandler(OutputSource outputSource) throws ConfigurationException, PipelineException {
    this.outputSource = outputSource;
    checkWritable();
  }

  @Override
  public OutputSource getHandler() {
    return outputSource;
  }

  /**
   * Check if there is an event supplier and an outputSource.
   * This throws a runtime exception, because we don't want to handle this unlikely case everywhere.
   * We also don't want NullPointerExceptions, so the more descriptive IllegalStateException is used.
   */
  private void checkWritable() {
    if (outputSource == null) {
      throw new IllegalStateException(this.getClass().getName()+" must have an outputSource before it can write.");
    }
    if (!(outputSource.hasWriter() || outputSource.hasOutputStream())) {
      throw new IllegalStateException(this.getClass().getName()+" must have an outputSource with a writer or output stream before it can write.");
    }
  }

}
