package org.greenmercury.speat;

/**
 * This interface is a wrapper around an event API like SAX or SMAX.
 * Its main purpose is to make the {@code Pipeline} interface and meta-pipelines
 * (pipelines that modify other pipelines like {@code PipelineConditional}) possible.
 *<p>
 * Without {@code EventHandler<Api>}, you would want something like
 * <pre>{@code interface Pipeline<S, T> extends S, EventSupplier<T>}</pre>
 * where {@code S} and {@code T} are event APIs.
 * But you cannot refer to the type parameter {@code S} as a supertype in Java.
 * Using the {@code EventHandler} interface, you can use
 * <pre>{@code interface Pipeline<S, T> extends EventHandler<S>, EventSupplier<T>}</pre>
 *<p>
 * Simple implementations of this interface can implement some `Api` and then define
 * <pre>{@code
 *   public InputSource getEventApi() {
 *    return this;
 *  }
 * }</pre>
 * If you have an instance `eventApi` of type `Api`, you can use
 * `EventHandler.from(eventApi)` to turn it into an `EventHandler<Api>`.
 *<p>
 * `EventHandler<Api>` is a functional interface, with {@code getHandler} as its abstract method.
 *
 * @author Rakensi
 * @param <Api>
 */
@FunctionalInterface
public interface EventHandler<Api> {

  /**
   * Get the event API for this event handler.
   * This is the function that classes must implement for this functional interface.
   * @return the event API that handles the events.
   */
  public Api getEventApi() throws ConfigurationException, PipelineException;

  /**
   * Turn an event API into an event handler for that API.
   * This implements the getHandler method in a trivial way, just returning the API itself.
   * @param api
   * @return an {@code EventHandler<Api>} for the given {@code Api}.
   */
  public static <Api> EventHandler<Api> from(Api api) {
    return () -> api;
  }

}
