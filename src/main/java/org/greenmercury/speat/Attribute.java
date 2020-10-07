package org.greenmercury.speat;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.xml.sax.Attributes;

/**
 * Access individual attributes from a {@code org.xml.sax.Attributes}.
 *<p>
 * @author Rakensi
 */
public class Attribute {

  private Attributes attributes;
  private int index;

  private Attribute(Attributes attributes, int index) {
    this.attributes = attributes;
    this.index = index;
  }

  public static Iterable<Attribute> iterable(Attributes attributes) {
    return () -> new Iterator<Attribute>() {
      int idx = 0;

      @Override
      public boolean hasNext() {
        return idx < attributes.getLength();
      }

      @Override
      public Attribute next() {
        return new Attribute(attributes, idx++);
      }};
  }
  
  public static Stream<Attribute> stream(Attributes attributes) {
    return StreamSupport.stream(Spliterators.spliterator(iterable(attributes).iterator(), attributes.getLength(), 0), false);
    // The above should be more efficient than StreamSupport.stream(iterable(attributes).spliterator(), false)
  }

  public String getLocalName() {
    return attributes.getLocalName(index);
  }

  public String getQName() {
    return attributes.getQName(index);
  }

  public String getType() {
    return attributes.getType(index);
  }

  public String getURI() {
    return attributes.getURI(index);
  }

  public String getValue() {
    return attributes.getValue(index);
  }

}
