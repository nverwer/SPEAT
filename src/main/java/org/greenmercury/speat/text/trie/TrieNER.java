package org.greenmercury.speat.text.trie;

import java.util.ArrayList;
import java.util.List;

import org.greenmercury.speat.text.StringUtils;

/**
 * Named Entity Recognition in a text, using a trie.
 * The {@code TrieNER} scans a text for pieces of text that match named entities using a {@code TrieScanner}.
 * This class is abstract; An implementation must specify what to do when a piece of text matches or does not match.
 * <p>
 * @author Rakensi
 */
public abstract class TrieNER {

  private String wordChars;
  private String noWordBefore;

  /**
   * Words can not start immediately after characters in this string.
   */
  private String noWordAfter;

  /**
   * The trie used for scanning. Only access this via {@code getTrie()} and {@code setTrie()}.
   * Multiple {@code TrieScanner} instances can be used in the same {@code TrieNER},
   * using {@code setTrie()} and {@code getTrie()}.
   */
  private TrieScanner trie;

  /**
   * Constructor for TrieNER.
   * @param wordChars characters that are considered part of a word, next to characters and digits.
   * @param noWordBefore characters in this string may not occur immediately after a match, next to characters and digits.
   * @param noWordAfter characters in this string may not occur immediately before a match, next to characters and digits.
   */
  public TrieNER(String wordChars, String noWordBefore, String noWordAfter) {
    this.wordChars = wordChars;
    this.noWordBefore = noWordBefore;
    this.noWordAfter = noWordAfter;
  }

  /**
   * Set the trie for this {@code TrieNER}.
   * @param trie The trie to use in this {@code TrieNER}.
   * The {@code trie} should be a trie that is obtained through {@code getTrie()} of this {@code TrieNER}.
   */
  public void setTrie(TrieScanner trie) {
    this.trie = trie;
  }

  /**
   * Get the trie of this {@code TrieNER}.
   * @return the trie used by this {@code TrieNER}.
   */
  public TrieScanner getTrie() {
    if (trie == null) {
      trie = new TrieScanner(wordChars, noWordBefore);
    }
    return trie;
  }

  /**
   * Action to perform when an entity has been matched in the text.
   * @param text the text that is being scanned.
   * @param start start position of the entity in the text.
   * @param end end position of the entity in the text.
   * @param ids The id's or keys belonging to the matched entity.
   */
  public abstract void match(CharSequence text, int start, int end, List<String> ids);

  /**
   * Action to perform for unmatched text fragments between matched entities.
   * @param text the text that is being scanned.
   * @param start start position of the unmatched text.
   * @param end end position of the unmatched text.
   */
  public abstract void noMatch(CharSequence text, int start, int end);

  /**
   * Scan a text for substrings matching an entity.
   * This function will call the functions {@code match} on matched entities and {@code noMatch} on unmatched text.
   * @param text The text that will be scanned for entities.
   * @param caseInsensitiveMinLength Matches with at least this length will be done case-insensitive.
   *        Set to -1 to always match case-sensitive. Set to 0 to always match case-insensitive.
   * @param fuzzyMinLength Matches with at least this length may be not exact, i.e. there may be non-trie characters in the match.
   *        Set to -1 to match exact. Set to 0 to match fuzzy.
   * Exact matching implies case-sensitive matching, so it does not make sense to search exact and case-insensitive.
   * Therefore, caseInsensitiveMinLength should be greater or equal than fuzzyMinLength.
   */
  public void scan(CharSequence text, int caseInsensitiveMinLength, int fuzzyMinLength) {
    TrieScanner trie = getTrie(); // Make sure that the trie is initialized.
    // Internally, we will work with normalized text.
    CharSequence normalizedText = StringUtils.normalizeOneToOne(text);
    int start = 0; // Starting position to search in text.
    final int length = normalizedText.length();
    StringBuilder unmatched = new StringBuilder(); // Collects unmatched characters, up to the next match.
    while (start < length) {
      char c;
      // Set start at the next first letter of a word.
      // A word must start with letter, digit or word-character.
      // It cannot start immediately after a word-character or a noWordAfter-character.
      while ( start < length &&
              ( !isWordChar(c = normalizedText.charAt(start)) ||
                ( start > 0 && noWordAfter(normalizedText.charAt(start-1)) )
              )
            ) {
        // c == normalizedText.charAt(start)
        unmatched.append(c);
        ++start;
        // c == normalizedText.charAt(start - 1)
      }
      // Scan for a match, starting at the word beginning at normalizedText[start].
      ArrayList<TrieScanner.ScanResult> results = trie.scan(normalizedText, start, caseInsensitiveMinLength >= 0);
      /* Determine if the match qualifies:
       * - There is a result.
       * - There may be accented characters (ticket#186), which are taken out in normalizedMatchedText.
       * - If (caseInsensitiveMinLength >= 0) the result-match was case-insensitive,
       *   which is correct if the matched text was long enough,
       *   otherwise there must be a case-sensitive match but no noise characters, so use matchedKey.
       * - The result-match ignores noise-characters.
       *   If the match is longer than fuzzyMinLength that is correct.
       *   Otherwise, the match must be exact, including noise characters.
       */
      ArrayList<String> matchedIds = new ArrayList<>();
      int matchedStart = -1;
      int matchedEnd = -1;
      if (results != null) {
        for (TrieScanner.ScanResult result : results) {
          // The following are String, because they are compared (equals), which does not work well on CharSequence.
          String onlyTrieCharsMatched = trie.toTrieChars(text.subSequence(result.start, result.end)).toString();
          String normalizedMatchedText = normalizedText.subSequence(result.start, result.end).toString();
          if (matchedStart < 0) {
            matchedStart = result.start;
          } else if (matchedStart != result.start) {
            throw new RuntimeException("Match starts at both "+result.start+" and "+matchedStart);
          }
          if (matchedEnd < 0) {
            matchedEnd = result.end;
          } else if (matchedEnd != result.end) {
            throw new RuntimeException("Match ends at both "+result.end+" and "+matchedEnd);
          }
          if ( ( caseInsensitiveMinLength >= 0 && result.end - result.start >= caseInsensitiveMinLength ||
                 onlyTrieCharsMatched.equals(result.matchedKey)
               )
               &&
               ( fuzzyMinLength >= 0 && result.end - result.start >= fuzzyMinLength ||
                 normalizedMatchedText.equals(result.matchedText)
               )
             ) { // This is a match.
            if (start == result.end) {
              throw new RuntimeException("No progress matching from '"+text.subSequence(result.start, text.length())+"'");
            }
            // Add ids that are not already present.
            for (String value : result.values) {
              if (!matchedIds.contains(value)) {
                matchedIds.add(value);
              }
            }
          }
        }
      }
      if (matchedIds.size() > 0) {
        // Output the characters before the match.
        unMatched(unmatched, text, start);
        // Process the match.
        match(text, matchedStart, matchedEnd, matchedIds);
        // Continue after the match.
        start = matchedEnd;
      } else if (start < length) { // There is no match and there is more to see.
        unmatched.append(c = text.charAt(start++));
        // Skip over the rest of a word containing letters and digits, but not wordChars.
        if (Character.isLetterOrDigit(c)) {
          while (start < length && Character.isLetterOrDigit(c = text.charAt(start))) {
            unmatched.append(c);
            ++ start;
          }
        }
      }
    } // while (start < length)
    // Output left-over characters.
    unMatched(unmatched, text, length);
  }

  /**
   * Is 'c' a character that may appear in a word?
   * @param c
   * @return
   */
  private boolean isWordChar(char c) {
    return getTrie().trieChar(c);
  }

  /**
   * Is 'c' a character that must not immediately precede a word?
   * @param c
   * @return
   */
  private boolean noWordAfter(char c) {
    return Character.isLetterOrDigit(c) || noWordAfter.indexOf(c) >= 0;
  }

  /**
   * Output unmatched characters and delete them from {@code unmatched}.
   * @param sb The characters to output.
   * @param text The string from where these characters come.
   * @param end The index in {@code text} immediately after the characters to output.
   */
  private void unMatched(StringBuilder sb, CharSequence text, int end) {
    int len = sb.length();
    if (len > 0) {
      noMatch(text, end - len, end);
      sb.delete(0, len);
    }
  }

}
