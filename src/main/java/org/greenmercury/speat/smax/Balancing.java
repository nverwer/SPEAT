package org.greenmercury.speat.smax;

/**
 * Balancing strategies for inserting a {@code SmaxElement} into a {@code SmaxDocument}.
 * When an element insertion based on start- and end-position leads to a non-well-formed XML structure,
 * the balancing strategy is used to modify the start- and end-position in such a way that the
 * structure becomes well-formed (balanced).
 *<p>
 * The following balancing strategies are available.
 * <dl>
 *  <dt>OUTER</dt><dd>new node is placed where it wants; solve intersection by expanding new node</dd>
 *  <dt>INNER</dt><dd>new node is placed where it wants; solve intersection by shrinking new node</dd>
 *  <dt>START</dt><dd>new node is placed at the start of its character span</dd>
 *  <dt>END</dt><dd>new node is placed at the end of its character span</dd>
 *  <dt>BALANCE_TO_START</dt><dd>new node is placed where it wants; solve intersection by START</dd>
 *  <dt>BALANCE_TO_END</dt><dd>new node is placed where it wants; solve intersection by END</dd>
 * </dl>
 * The {@code START} and {@code END} strategies are always applied, even if the insertion would be balanced.
 *<p>
 * @author Rakensi
 */
public enum Balancing {
  OUTER,
  INNER,
  START,
  END,
  BALANCE_TO_START,
  BALANCE_TO_END;

  public static Balancing parseBalancing(String value) {
    switch (value) {
    case "OUTER":
      return Balancing.OUTER;
    case "INNER":
      return Balancing.INNER;
    case "START":
      return Balancing.START;
    case "END":
      return Balancing.END;
    case "BALANCE_TO_START":
      return Balancing.BALANCE_TO_START;
    case "BALANCE_TO_END":
      return Balancing.BALANCE_TO_END;
    default:
      throw new IllegalArgumentException("Illegal balancing \""+value+"\"");
    }
  }

}
