package org.greenmercury.speat.text.trie;

import java.util.ArrayList;

import org.greenmercury.speat.text.StringUtils;

/**
 * A memory-efficient trie-based lookup, based on the TrieST implementation
 * [http://algs4.cs.princeton.edu/52trie/TrieST.java.html].
 * This is used for scanning, therefore we omitted functions like delete, etc.
 * A node can contain multiple values, which is an extension of the usual functionality.
 * The values must be strings.
 *<p>
 * @author Rakensi
 */
public class TrieScanner {

  private String wordChars; // Characters that are considered part of a word, next to characters and digits and whitespace.
  private String noWordBefore; // May not occur immediately after a match, next to characters and digits.
  private static final int R = 128; // Low ASCII characters appear in a node as possible next branches.
  private Node root; // root of trie
  private int nrKeys; // number of keys in trie
  private long totalKeySize; // estimated size of all keys in bytes
  private int nrNodes; // number of nodes in the trie
  private int nrBigNodes; // number of nodes that have a `next` array

  /**
   * Scan result, contains information about a successful match.
   */
  public class ScanResult {
    public ArrayList<String> values; // The values for the result.
    public int start; // The position in the scanned text from where the match starts.
    public int end; // The position in the scanned text where the match has stopped.
    public String matchedText; // The text that has matched.
    public String matchedKey; // The key that has matched, may differ from scanned text in noise characters.

    public ScanResult(ArrayList<String> values, int start, int end, CharSequence matchedText, CharSequence matchedKey) {
      this.values = values;
      this.start = start;
      this.end = end;
      this.matchedText = matchedText.toString();
      this.matchedKey = matchedKey.toString();
    }
  }

  /**
   * Optimized implementation of nodes in the trie.
   * When there is only one branch, it does not allocate memory for R outgoing branches.
   */
  private class Node {
    public ArrayList<String> values = null; // The values for this key, if any.
    private Node[] next; // Multiple branches extending from this node.
    private char c = 0; // Character for a single branch.
    private Node nextc; // The single branch for this character.

    private Node() {
      nrNodes++;
    }

    /**
     * Returns the Node associated with the given key.
     * @param key The complete key, which has been matched until the d'th character.
     * @param d The current depth in the trie. The root has depth 0.
     * @return The node that matches the key, or null if the key does not match.
     */
    private Node get(String key, int d) {
      if (d == key.length()) {
        // The key has been matched, return this node.
        return this;
      }
      char c = key.charAt(d);
      if (next != null) {
        if (next[c] == null) {
          return null;
        }
        // Match one character and move deeper into the trie.
        return next[c].get(key, d + 1);
      }
      if (this.c == c && nextc != null) {
        // The single branch matches.
        return nextc.get(key, d + 1);
      } else {
        return null;
      }
    }

    /**
     * Add a key-value pair to a node.
     * @param originalKey The original key.
     * @param key The acceptable characters from the key.
     * @param val The value associated with the key.
     * @param d The current depth in the trie. The root has depth 0.
     * @return The updated node.
     */
    private Node put(String originalKey, String key, String val, int d) throws IllegalArgumentException {
      if (d == key.length()) {
        if (values == null)
         {
          values = new ArrayList<String>(1); // The most common case is a single value.
        }
        if (!values.contains(val)) {
          values.ensureCapacity(values.size()+1);
          values.add(val);
          nrKeys++;
          totalKeySize += 36 + 2 * val.length(); // http://java-performance.info/overview-of-memory-saving-techniques-java/
        }
        return this;
      }
      char c = key.charAt(d);
      if (!trieChar(c)) {
        throw new IllegalArgumentException("Illegal trie character: ["+c+"] ("+((int)c)+") in key ["+originalKey+"].");
      }
      if (next != null) {
        next[c] = (next[c] != null ? next[c] : new Node()).put(originalKey, key, val, d + 1);
      } else if (nextc != null) {
        if (this.c == 0 || this.c == c) {
          this.c = c;
          nextc = nextc.put(originalKey, key, val, d + 1);
        } else {
          next = new Node[R];
          next[this.c] = nextc;
          next[c] = new Node().put(originalKey, key, val, d + 1);
          this.c = 0;
          nextc = null;
          nrBigNodes++;
        }
      } else {
        this.c = c;
        nextc = new Node().put(originalKey, key, val, d + 1);
      }
      return this;
    }

    /**
     * Find longest prefix, assuming the first {@code d} character match and
     * we have already found a prefix match of length {@code length}.
     * @param query
     * @param d
     * @param length
     * @return the length of the longest string key in the subtrie rooted at this that is a prefix of the query string
     */
    private int longestPrefixOf(String query, int d, int length) {
      if (values != null) {
        length = d;
      }
      if (d == query.length()) {
        return length;
      }
      char c = query.charAt(d);
      if (next != null) {
        if (next[c] == null) {
          return length;
        } else {
          return next[c].longestPrefixOf(query, d + 1, length);
        }
      } else {
        if (this.c != c) {
          return length;
        } else {
          return nextc.longestPrefixOf(query, d + 1, length);
        }
      }
    }

    /**
     * Find the longest substring in {@code text}, starting at {@code start}, that matches a key in the trie.
     * @param normalizedText The normalized version of the text that we are scanning.
     * @param start The position in {@code text} from where the current scan starts.
     * @param current The position in {@code text} that holds the next character to scan.
     * @param end The position one beyond the last position in {@code text}.
     * @param caseInsensitive Indicates that matching is case-insensitive.
     * @param matchedText Fragment of the input text that has actually matched. This corresponds to text[start,current).
     * @param matchedKey The exact key in the trie that has been matched so far.
     * @return The results of the current scan. This may be null if there are no results
     * Whitespace must be normalized in {@code normalizedText}.
     * All sequences of whitespace characters will be matched like a single space.
     * If the scan is case-insensitive there can be multiple results with different matched texts.
     * The matched texts may differ in case, and in whitespace.
     */
    private ArrayList<ScanResult> scan(CharSequence normalizedText, int start, int current, int end,
        boolean caseInsensitive, StringBuilder matchedText, StringBuilder matchedKey
    ) {
      if (current < end) {
        // Look for a longer match starting at the next not-yet-matched character.
        // Within this block, matchedText may be temporarily extended.
        char ch = normalizedText.charAt(current);
        // NextPos is what current will become if there is a match.
        int nextPos = current + 1;
        // Match sequences of whitespace and ignored characters as one space.
        while ( nextPos < end && wordSeparatorChar(ch) ) {
          ch = normalizedText.charAt(nextPos);
          nextPos = nextPos + 1;
        }
        // If there were ignored characters and whitespace, match them as if it was one space.
        if (nextPos > current + 1) {
          ch = ' ';
          nextPos = nextPos - 1;
        }
        // Now ch is the trie-character or space that must be matched; nextPos points to the character after ch.
        matchedText.append(normalizedText.subSequence(current, nextPos));
        // Do the actual scan for a longer match.
        ArrayList<ScanResult> longer = null;
        if (trieChar(ch)) {
          // Do a case-insensitive match if the character has case.
          if (caseInsensitive && Character.isLetter(ch)) {
            // Try upper-case to find a longer match.
            ArrayList<ScanResult> longerUpperCase = null;
            matchedKey.append(Character.toUpperCase(ch));
            Node branch = branch(Character.toUpperCase(ch));
            if (branch != null) {
              longerUpperCase = branch.scan(normalizedText, start, nextPos, end, caseInsensitive, matchedText, matchedKey);
            }
            // Try lower-case to find a longer match.
            ArrayList<ScanResult> longerLowerCase = null;
            matchedKey.setCharAt(matchedKey.length()-1, Character.toLowerCase(ch));
            branch = branch(Character.toLowerCase(ch));
            if (branch != null) {
              longerLowerCase = branch.scan(normalizedText, start, nextPos, end, caseInsensitive, matchedText, matchedKey);
            }
            // Merge the longer matches for upper- and lower-case.
            if (longerUpperCase != null) {
              if (longerLowerCase != null) {
                if (longerUpperCase.get(0).end > longerLowerCase.get(0).end) {
                  longer = longerUpperCase;
                } else if (longerLowerCase.get(0).end > longerUpperCase.get(0).end) {
                  longer = longerLowerCase;
                } else {
                  longer = longerUpperCase;
                  longer.addAll(longerLowerCase);
                }
              } else {
                longer = longerUpperCase;
              }
            } else if (longerLowerCase != null) {
              longer = longerLowerCase;
            }
          } else {
            // Case-sensitive match.
            matchedKey.append(ch);
            Node branch = branch(ch);
            if (branch != null) {
              longer = branch.scan(normalizedText, start, nextPos, end, caseInsensitive, matchedText, matchedKey);
            }
          }
          matchedKey.deleteCharAt(matchedKey.length()-1);
        } // if (trieChar(ch))
        matchedText.delete(current-start, matchedText.length());
        if ( longer != null) {
          // We have found a longer match.
          return longer;
        }
        // We have not found a longer match, but we may have found a match here.
      } // if(current < end); We did not find longer match.
      ArrayList<ScanResult> result = null;
      // The result that we found is valid if the current node has values and the match is not followed by a noWordBefore character.
      if (values != null && ( current == end || current < end && !continuesWord(normalizedText.charAt(current)) ) ) {
        result = new ArrayList<ScanResult>();
        result.add(new ScanResult(values, start, current, matchedText, matchedKey));
      }
      return result;
    }

    /**
     * Determine the branch from the current node for a character.
     * @param c The character for which we seek a branch.
     * @return The branch for the character if there is one, or null if there is no branch for the character.
     */
    private Node branch(char c) {
      if (next != null) {
        return next[c];
      } else if (this.c == c) {
        return nextc;
      } else {
        return null;
      }
    }

    /**
     * Is the character a word separator?
     * @param c
     * @return True if the character is not a valid trie character, or if it is a space.
     */
    private boolean wordSeparatorChar(char c) {
      return !trieChar(c) || Character.isWhitespace(c);
    }

    private boolean continuesWord(char c) {
      return Character.isLetterOrDigit(c) || noWordBefore.indexOf(c) >= 0;
    }

  } // class Node

  /**
   * TrieScanner constructor. Initializes an empty string symbol table.
   * @param wordChars
   * @param noWordBefore
   */
  public TrieScanner(String wordChars, String noWordBefore) {
    this.wordChars = wordChars;
    this.noWordBefore = noWordBefore;
    nrKeys = 0;
    totalKeySize = 0L;
    nrNodes = 0;
    nrBigNodes = 0;
  }

  /**
   * Determines if c is an acceptable character to put in the Trie.
   * @param c A character
   * @return Indicates if c is acceptable.
   * Whitespace is acceptable, but will be converted to normal space when put into the trie or when matched.
   */
  public boolean trieChar(char c) {
    return Character.isLetterOrDigit(c) || Character.isWhitespace(c) || wordChars.indexOf(c) >= 0;
  }

  /**
   * Turn the normalized version a string into acceptable Trie characters.
   * Other characters are replaced by spaces. Whitespace is normalized.
   * @param s A string
   * @return The transformed version of {@code s} containing only acceptable Trie characters.
   */
  public String toTrieChars(CharSequence s) {
    if (s == null) {
      return null;
    }
    s = StringUtils.normalizeOneToOne(s);
    StringBuilder sb = new StringBuilder();
    int n = s.length();
    for (int i = 0; i < n; i++) {
      char c = s.charAt(i);
      if (trieChar(c)) {
        sb.append(c);
      } else {
        sb.append(' ');
      }
    }
    return StringUtils.normalizeSpaces(sb);
  }

  /**
   * Returns the values associated with the given key.
   * @param key the key
   * @return the values associated with the given key if the key is in the symbol table and <tt>null</tt> if the key is not in the symbol table.
   * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
   */
  public ArrayList<String> get(String key) {
    key = toTrieChars(key).toString();
    if (root == null) {
      return null;
    }
    Node x = root.get(key, 0);
    if (x == null) {
      return null;
    }
    return x.values;
  }

  /**
   * Does this symbol table contain the given key?
   * @param key
   *          the key
   * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
   *         <tt>false</tt> otherwise
   * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
   */
  public boolean contains(String key) {
    return get(key) != null;
  }

  /**
   * Inserts the key-value pair into the symbol table, overwriting the old value
   * with the new value if the key is already in the symbol table.
   * @param originalKey the key
   * @param val the value
   */
  public void put(String originalKey, String val) {
    String key = toTrieChars(originalKey);
    if (root == null) {
      root = new Node();
    }
    root.put(originalKey, key, val, 0);
  }

  /**
   * The number of key-value pairs in this symbol table.
   * @return the number of key-value pairs in this symbol table
   */
  public int nrKeys() {
    return nrKeys;
  }

  /**
   * Returns the number of nodes in this symbol table.
   * @return the number of nodes in this symbol table
   */
  public int nrNodes() {
    return nrNodes;
  }

  /**
   * Estimate the size in memory of the trie.
   * @return The estimated size in bytes.
   */
  public long sizeInBytes() {
    return nrNodes * 32L + nrBigNodes * (12L + R * 8L) + totalKeySize;
  }

  /**
   * Is this symbol table empty?
   * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise.
   */
  public boolean isEmpty() {
    return nrKeys == 0;
  }

  /**
   * Returns the string in the symbol table that is the longest prefix of
   * <tt>query</tt>, or <tt>null</tt>, if no such string.
   * @param query the query string
   * @return the string in the symbol table that is the longest prefix of <tt>query</tt>, or <tt>null</tt> if no such string
   * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
   */
  public String longestPrefixOf(String query) {
    if (root == null) {
      return "";
    }
    int length = root.longestPrefixOf(query, 0, 0);
    return query.substring(0, length);
  }

  /**
   * Scan for a longest matching key in a text, starting at a specified position
   * @param normalizedText The text to scan. This must already be normalized as desired.
   * @param start The starting position.
   * @param caseInsensitive Indicates that matching is case-insensitive.
   *   They are considered to be part of a word.
   * @return A collection of ScanResult which is null if there is no match.
   */
  public ArrayList<ScanResult> scan(CharSequence normalizedText, int start, boolean caseInsensitive) {
    if (root == null) {
      return null;
    }
    ArrayList<ScanResult> results =
      root.scan(normalizedText, start, start, normalizedText.length(),
        caseInsensitive, new StringBuilder(), new StringBuilder()
      );
    return results;
  }

}