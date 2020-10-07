package org.greenmercury.speat.io.input;

import java.io.IOException;

import org.greenmercury.speat.AbstractPipeline;
import org.greenmercury.speat.PipelineException;

/**
 * Abstract class that is the start of a pipeline, which reads from different kinds of inputSource sources
 * and produces {@code Api} events.
 *<p>
 * Implementing classes only have to provide a {@code readInputAndSendEvents()} method to parse the inputSource from {@code inputSource},
 * and send {@code Api} events to the {@code handler}.
 *<p>
 * Pipelines are re-useable; It must be possible to {@code read} more than one source document.
 *<p>
 *
 * @author Rakensi
 */
public abstract class InputSourceReader<Api> extends AbstractPipeline<InputSource, Api> {

  protected InputSource inputSource = null;
  protected Api handler = null;
  private long lastRead = 0L;

  /**
   * Read the inputSource source and send events to the handler.
   * This must be implemented by sub-classes.
   * It must never be called directly, but always via the {@code read} method.
   */
  protected abstract void readInputAndSendEvents() throws IOException, PipelineException;

  /**
   * Read the inputSource source and send events to the handler.
   * Also keeps track of when the input source was read by this reader.
   */
  public void read() throws IOException, PipelineException {
    checkReadable();
    readInputAndSendEvents();
    lastRead = System.currentTimeMillis();
  }

  /**
   * The {@code long} time when the input source was last read by this reader.
   * @return time when the input source was last read by this reader, or {@code 0L} if it has not been read yet.
   */
  public long getLastRead() {
    return lastRead;
  }

  /**
   * Check if there is an inputSource and a handler.
   * This throws a runtime exception, because we don't want to handle this unlikely case everywhere.
   * We also don't want NullPointerExceptions, so the more descriptive IllegalStateException is used.
   */
  private void checkReadable() {
    if (handler == null) {
      throw new IllegalStateException(this.getClass().getName()+" must have a handler before it can read.");
    }
    if (inputSource == null) {
      throw new IllegalStateException(this.getClass().getName()+" must have an inputSource before it can read.");
    }
  }

  /**
   * Set the inputSource source to read from.
   * @param inputSource
   */
  public void setInputSource(InputSource inputSource) {
    this.inputSource = inputSource;
    lastRead = 0L; // The new input source has not been read yet.
  }

  /**
   * @see org.greenmercury.speat.EventHandler#getEventApi()
   */
  @Override
  public InputSource getEventApi() {
    return inputSource;
  }

  /**
   * @see org.greenmercury.speat.EventSupplier#setHandler(java.lang.Object)
   */
  @Override
  public void setHandler(Api handler) {
    this.handler = handler;
  }

  /**
   * @see org.greenmercury.speat.EventSupplier#getHandler()
   */
  @Override
  public Api getHandler() {
    return handler;
  }

}
