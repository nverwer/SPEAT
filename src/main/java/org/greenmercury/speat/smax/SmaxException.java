package org.greenmercury.speat.smax;

/**
 * An exception that is used when inconsistencies in Smax documents occur.
 *<p>
 * @author Rakensi
 */
public class SmaxException extends Exception {

  private static final long serialVersionUID = 1L;

  public SmaxException(String message) {
    super(message);
  }

  public SmaxException(Throwable throwable) {
    super(throwable);
  }

  public SmaxException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
