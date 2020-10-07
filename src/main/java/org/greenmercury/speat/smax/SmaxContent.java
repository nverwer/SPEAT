package org.greenmercury.speat.smax;

/**
 * Representation of document content for SMAX, based on {@code StringBuffer},
 * which is final and cannot be extended, so {@code SmaxContent} wraps {@code StringBuffer}.
 * Because the text content is a {@code StringBuffer}, it cannot support very large (> 2^31-1 characters) documents.
 *<p>
 * We use {@code StringBuffer} rather than {@code StringBuilder}, because it is thread-safe.
 * The {@code StringBuffer} may be used in different threads, so thread-safety may be needed.
 *<p>
 * When {@code SmaxContent} is used in a {@code SmaxDocument}, the start and end indexes point to
 * the wrapped {@code StringBuffer}, not to the zero-based view provided by {@code SmaxContent}.
 * This makes it easier to create sub-documents of a {@code SmaxDocument} without changing start and end positions.
 *<p>
 * This class provides sub-document views with zero-based indexes on a document,
 * without copying parts of the underlying buffer like StringBuffer, CharBuffer and CharSequence do.
 *<p>
 * @author Rakensi
 */
public class SmaxContent implements Appendable, CharSequence, Comparable<SmaxContent> {

  /**
   * We wrap a StringBuffer, because StringBuffer is final, so extending is not possible.
   */
  private StringBuffer buffer;
  private int start; // The start index within the buffer, for zero-based views.
  private int end; // The end index within the buffer, for zero-based views.

  /**
   * Constructor for a {@code SmaxContent} view on a {@code StringBuffer}.
   * @param buffer the underlying {@code StringBuffer}
   * @param start start position within {@code buffer}
   * @param end end position within {@code buffer}
   */
  public SmaxContent(StringBuffer buffer, int start, int end) {
    this.buffer = buffer;
    this.start = start;
    this.end = end;
    if (this.start < 0) {
      throw new IndexOutOfBoundsException("The start index must not be negative.");
    }
    if (this.start > this.end) {
      throw new IndexOutOfBoundsException("The start index must not be greater than the end index.");
    }
    if (this.end > buffer.length()) {
      throw new IndexOutOfBoundsException("The end index must not be greater than the end index of its parent buffer.");
    }
  }

  /**
   * Constructor for a {@code SmaxContent} view on all text in a {@code StringBuffer}.
   * @param buffer the underlying {@code StringBuffer}
   */
  public SmaxContent(StringBuffer buffer) {
    this(buffer, 0, buffer.length());
  }

  /**
   * Constructor for a sub-document view on a {@code SmaxContent}.
   * @param content
   * @param start relative start position
   * @param end relative end position
   */
  public SmaxContent(SmaxContent content, int start, int end) {
    this(content.buffer, content.start + start, content.start + end);
  }

  /**
   * Constructor equivalent to {@code StringBuffer}.
   */
  public SmaxContent() {
    this(new StringBuffer(), 0, 0);
  }

  /**
   * Constructor equivalent to {@code StringBuffer}.
   * @param capacity
   */
  public SmaxContent(int capacity) {
    this(new StringBuffer(capacity), 0, 0);
  }

  /**
   * Constructor equivalent to {@code StringBuffer}.
   * @param seq
   */
  public SmaxContent(CharSequence seq) {
    this(seq, 0, seq.length());
  }

  /**
   * Constructor for a sub-document view on a {@code StringBuffer}.
   * @param seq
   * @param start
   * @param end
   */
  public SmaxContent(CharSequence seq, int start, int end) {
    this(new StringBuffer(seq), start, end);
  }

  /**
   * Get the underlying buffer of the content.
   * This buffer is consistent with the {@code startPos} and {@code endPos}
   * of {@code SmaxElement}s in a {@code SmaxDocument}.
   * @return the underlying {@code StringBuffer}
   */
  public StringBuffer getUnderlyingBuffer() {
    return buffer;
  }

  /**
   * Get the (sub-)content as a String.
   * @return the part of the buffer that has the content, as a {@code String}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return buffer.substring(start, end);
  }

  /* Comparable implementation */

  @Override
  public int compareTo(SmaxContent that) {
    return toString().compareTo(that.toString());
  }

  /* CharSequence implementation */

  @Override
  public int length() {
    return end - start;
  }

  @Override
  public char charAt(int index) {
    return buffer.charAt(start + index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return buffer.subSequence(this.start + start, this.start + end);
  }

  /* Appendable implementation */

  @Override
  public SmaxContent append(CharSequence csq) {
    buffer = buffer.insert(end, csq);
    end = end + csq.length();
    return this;
  }

  @Override
  public SmaxContent append(CharSequence csq, int start, int end) {
    buffer = buffer.insert(this.end, csq, start, end);
    this.end = this.end + (end - start);
    return this;
  }

  @Override
  public SmaxContent append(char c) {
    buffer = buffer.insert(end, c);
    end = end + 1;
    return this;
  }

  /* StringBuffer methods */

  public void getChars(int srcStart, int srcEnd, char[] dst, int dstBegin) {
    buffer.getChars(start + srcStart, start + srcEnd, dst, dstBegin);
  }

  public void setCharAt(int index, char ch) {
    buffer.setCharAt(start + index, ch);
  }

  public SmaxContent delete(int start, int end) {
    buffer = buffer.delete(this.start + start, this.start + end);
    this.end = this.end - (end - start);
    return this;
  }

  public SmaxContent deleteCharAt(int index) {
    buffer = buffer.deleteCharAt(start + index);
    end = end - 1;
    return this;
  }

  public SmaxContent replace(int start, int end, String str) {
    if (str == null) {
      str = "";
    }
    buffer = buffer.replace(this.start + start, this.start + end, str);
    this.end = this.end - (end - start) + str.length();
    return this;
  }

  public String substring(int start) {
    return buffer.substring(this.start + start, end);
  }

  public String substring(int start, int end) {
    return buffer.substring(this.start + start, this.start + end);
  }

  public SmaxContent insert(int offset, String str) {
    if (str == null) {
      str = "";
    }
    buffer = buffer.insert(start + offset, str);
    end = end + str.length();
    return this;
  }

  public SmaxContent insert(int offset, CharSequence csq) {
    buffer = buffer.insert(start + offset, csq);
    end = end + csq.length();
    return this;
  }

  public SmaxContent insert(int offset, CharSequence csq, int srcStart, int srcEnd) {
    buffer = buffer.insert(start + offset, csq, srcStart, srcEnd);
    end = end + (srcEnd - srcStart);
    return this;
  }

  public int indexOf(String str) {
    return toString().indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return toString().indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return toString().lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return toString().lastIndexOf(str, fromIndex);
  }
}
