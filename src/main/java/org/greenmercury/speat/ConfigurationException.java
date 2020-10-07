package org.greenmercury.speat;

public class ConfigurationException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Throw a {@code ConfigurationException} with the given message.
   * @param message
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * Throw a {@code ConfigurationException} with the given message and cause.
   * @param message
   * @param cause
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Throw a {@code ConfigurationException} when a property with the given name is {@code null}.
   * @param name
   * @param property
   * @throws ConfigurationException
   */
  public static void ifNull(String name, Object property) throws ConfigurationException {
    if (property == null) {
      throw new ConfigurationException("Property '"+name+"' must have a value.");
    }
  }

}
