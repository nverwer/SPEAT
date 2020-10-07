package org.greenmercury.speat;

/**
 * A general exception for anything that could go wrong in a SPEAT pipeline.
 * Usually, this is a wrapper around another more specific exception.
 *<p>
 * @author Rakensi
 */
public class PipelineException extends Exception {

  private static final long serialVersionUID = 1L;

  public PipelineException(String message) {
    super(message);
  }

  public PipelineException(Throwable throwable) {
    super(throwable);
  }

  public PipelineException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public static void ifNull(String name, Object property) throws PipelineException {
    if (property == null) {
      throw new PipelineException("Property '"+name+"' must have a value.");
    }
  }

}
