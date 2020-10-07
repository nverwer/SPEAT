package org.greenmercury.speat;

import org.slf4j.Logger;

/**
 * Interface specifying a pipeline transforming one event API {@code S} into another event API {@code T}.
 * A transformer is a (one-step) pipeline, and a pipeline can be used as a transformer step in another pipeline.
 *
 * @param <S> inputSource / source event API type
 * @param <T> output / target event API
 */
public interface Pipeline<S, T> extends EventHandler<S>, EventSupplier<T> {

  /**
   * A pipeline has a logger.
   * If the logger is not set by {@code setLogger}, a default logger will be made by getLogger.
   * @return the logger for this pipeline. This must not be {@code null}.
   */
  public Logger getLogger();

  /**
   * Set the logger, overriding the default logger for this pipeline.
   * This method should only be called if an external logger must be used,
   * preferably before any logging can happen.
   * @param logger
   * @return the pipeline
   */
  public Pipeline<S, T> setLogger(Logger logger);

  /**
   * Method for building pipelines by chaining.
   * It turns a {@code Pipeline<S, T>} and a {@code Pipeline<T, U> next} into a {@code Pipeline<S, U>}.
   *<p>
   * To build a pipeline, you typically use a number of calls to {@code addHandler},
   * followed by one call to {@code setHandler} on the complete pipeline, to capture the output.
   * @param next the handler which handles {@code Api} events and supplies {@code NextApi} events.
   * @return the handler that has been set, for chaining the next handler
   */
  public default <U> Pipeline<S, U> append(Pipeline<T, U> next)
      throws ConfigurationException, PipelineException {
    // Remember this pipeline.
    Pipeline<S, T> previous = this;
    // Set the handler to connect this pipeline to the next.
    setHandler(next.getEventApi());
    /* Make a composite pipeline.
     * Chaining many pipelines in this way is slightly inefficient, but not much,
     * because setHandler and getHandler are not used during pipeline execution.
     */
    return new Pipeline<S, U>() {
      @Override
      public S getEventApi() throws ConfigurationException, PipelineException {
        return previous.getEventApi();
      }
      @Override
      public void setHandler(U handler) throws ConfigurationException, PipelineException {
        next.setHandler(handler);
      }
      @Override
      public U getHandler() {
        return next.getHandler();
      }
      @Override
      public Logger getLogger() {
        // No logging is done in the composite pipeline.
        return null;
      }
      @Override
      public Pipeline<S, U> setLogger(Logger logger) {
        // The same logger is set in the sub-pipelines.
        previous.setLogger(logger);
        next.setLogger(logger);
        return this;
      }
    };
  }

  /**
   * Make a pipeline that passes on events to the next step.
   * This is an identity function, but the term 'identity' is overloaded, so I prefer {@code passOn}.
   * @return a no-operation pipeline step
   */
  public static <T> Pipeline<T, T> passOn() {
    return new Pipeline<T, T>() {
      private T handler;
      @Override
      public T getEventApi() throws ConfigurationException, PipelineException {
        return handler;
      }
      @Override
      public void setHandler(T handler) throws ConfigurationException, PipelineException {
        this.handler = handler;
      }
      @Override
      public T getHandler() {
        return handler;
      }
      @Override
      public Logger getLogger() {
        // No logging is done in the passOn pipeline.
        return null;
      }
      @Override
      public Pipeline<T, T> setLogger(Logger logger) {
        return this;
      }};
  }

}
