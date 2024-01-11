package org.greenmercury.speat.smax.to.smax;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenmercury.speat.Attribute;
import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.PipelineException;
import org.greenmercury.speat.io.input.InputSource;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;
import org.greenmercury.speat.text.TextLineStreamApi;
import org.greenmercury.speat.text.TextLineStreamReader;
import org.greenmercury.speat.text.trie.TrieNER;

/**
 * A SMAX document transformer that inserts markup around named entities specified by a trie grammar.
 *<p>
 * The transformer takes the following parameters:
 * <ul>
 *   <li>matchNodeTemplate The template for the XML element that is inserted for each content fragment that matches a named entity.
 *       This template must have one empty attribute, which will be filled with the id/names of the matched named entities.</li>
 *   <li>transformWithin A {@code SmaxElement} that specifies parts of the document that will be scanned for named entities.
 *       The transformer will only do named entity recognition within elements that match {@code scanWithin}.</li>
 *   <li>grammarSource The input source for the grammar.</li>
 *   <li>wordChars Characters that may appear in a word (next to letters and digits).
 *       They are significant for matching. Default is "", so spaces are not included.</li>
 *   <li>noWordBefore Characters that may not immediately follow a word (next to letters and digits).
 *       They cannot follow the end of a match. Default is "".</li>
 *   <li>noWordAfter Characters that may not immediately precede a word (next to letters and digits).
 *       Matches can only start on a letter or digit, and not after noWordAfter characters. Default is "".</li>
 *   <li>caseInsensitiveMinLength The minimum entity-length for case-insensitive matching.
 *       Text fragments larger than this will be scanned case-insensitively.
 *       This prevents short words to be recognized as abbreviations.
 *       Set to -1 to always match case-sensitive. Set to 0 to always match case-insensitive.
 *       Default is -1.</li>
 *   <li>fuzzyMinLength The minimum entity-length for fuzzy matching.
 *       Text fragments larger than this may contain characters that are not significant for the trie.
 *       This prevents short words with noise to be recognized as abbreviations.
 *       Set to -1 to match exact. Set to 0 to match fuzzy.
 *       Default is -1.</li>'
 * </ul>
 * All sequences of whitespace characters will be treated like a single space,
 * both in the grammar input and the text that is scanned for named entities.
 *<p>
 * @see <a href="https://en.wikipedia.org/wiki/Named-entity_recognition">Wikipedia: Named Entity Recognition</a>
 * @author Rakensi
 */
public class NamedEntityRecognizer extends SmaxDocumentTransformer {

  // Node insertion template.
  private SmaxElement matchNodeTemplate;
  // Name of the attribute that will hold the id's that are found for a named entity.
  private String attributeName;

  // The minimum entity-lengths for case-insensitive or fuzzy matching.
  private int caseInsensitiveMinLength;
  private int fuzzyMinLength;

  // Other configuration of the named entity recognizer.
  private String wordChars;
  private String noWordBefore;
  private String noWordAfter;

  // The input source of the grammar.
  private InputSource grammarSource;

  // The InputSourceReader from which the trie grammar is read.
  private TextLineStreamReader grammarReader;

  // The time when the grammar was compiled.
  private long grammarLastCompiled;

  // The TrieNER instance used for scanning.
  private TrieNER triener;

  // The (sub-)document that is being transformed.
  private SmaxDocument transformedDocument;

  // Compiled tries can get big. Keep a global map from grammarSource URL to size in bytes.
  private static Map<String, Long> trieStoreBytes = new HashMap<String, Long>();

  /**
   * Constructor for @code{NamedEntityRecognizer}.
   * @param grammarSource Given in the constructor, because it is fixed in the trie.
   * @param wordChars Given in the constructor, because it is fixed in the trie.
   * @param noWordBefore Given in the constructor, because it is fixed in the trie.
   * @param noWordAfter Given in the constructor, because it is fixed in the trie.
   */
  public NamedEntityRecognizer(InputSource grammarSource, String wordChars, String noWordBefore, String noWordAfter) {
    matchNodeTemplate = null;
    attributeName = null;
    caseInsensitiveMinLength = -1;
    fuzzyMinLength = -1;
    this.wordChars = wordChars != null ? wordChars : "";
    this.noWordBefore = noWordBefore != null ? noWordBefore : "";
    this.noWordAfter = noWordAfter != null ? noWordAfter : "";
    this.grammarSource = grammarSource;
    grammarReader = new TextLineStreamReader();
    grammarReader.setInputSource(grammarSource);
    grammarLastCompiled = 0L;
    triener = null;
  }

  /**
   * Set the named entity template for this @code{NamedEntityRecognizer}.
   * @param matchNodeTemplate the template for the XML element that is inserted for each named entity
   * @return this @code{NamedEntityRecognizer}.
   */
  public NamedEntityRecognizer setMatchNodeTemplate(SmaxElement matchNodeTemplate) throws ConfigurationException {
    this.matchNodeTemplate = matchNodeTemplate;
    for (Attribute attr : Attribute.iterable(matchNodeTemplate.getAttributes())) {
      if (attr.getValue() == null || attr.getValue().length() == 0) {
        if (attributeName != null) {
          throw new ConfigurationException("The match node template must have exactly one empty attribute."+
              " Found "+attributeName+" and "+attr.getQName());
        }
        attributeName = attr.getQName();
      }
    }
    if (attributeName == null) {
      throw new ConfigurationException("The match node template must have exactly one empty attribute. Found none.");
    }
    return this;
  }

  /**
   * Set the minimum entity length for case-insensitive matching.
   * @param caseInsensitiveMinLength minimum length for case-insensitive matching
   * @return this @code{NamedEntityRecognizer}.
   */
  public NamedEntityRecognizer setCaseInsensitiveMinLength(int caseInsensitiveMinLength) {
    this.caseInsensitiveMinLength = caseInsensitiveMinLength;
    return this;
  }

  /**
   * Set the minimum entity length for fuzzy matching.
   * @param fuzzyMinLength minimum length for fuzzy matching
   * @return this @code{NamedEntityRecognizer}.
   */
  public NamedEntityRecognizer setFuzzyMinLength(int fuzzyMinLength) {
    this.fuzzyMinLength = fuzzyMinLength;
    return this;
  }

  /**
   * Set the grammar source for this @code{NamedEntityRecognizer}.
   * @param grammarSource
   * @return this @code{NamedEntityRecognizer}.
   * Setting the grammar source requires recompilation, which is inefficient. Use with care.
   */
  public NamedEntityRecognizer setGrammarSource(InputSource grammarSource) {
    this.grammarSource = grammarSource;
    grammarReader = new TextLineStreamReader();
    grammarReader.setInputSource(grammarSource);
    grammarLastCompiled = 0L;
    triener = null;
    return this;
  }

  /**
   * Set the word characters for this @code{NamedEntityRecognizer}.
   * @param wordChars
   * @return this @code{NamedEntityRecognizer}.
   * Setting the word characters requires recompilation, which is inefficient. Use with care.
   */
  public NamedEntityRecognizer setWordChars(String wordChars) {
    if (! wordChars.equals(this.wordChars)) {
      this.wordChars = wordChars != null ? wordChars : "";
      triener = null;
    }
    return this;
  }

  /**
   * Set the noWordBefore characters for this @code{NamedEntityRecognizer}.
   * @param noWordBefore
   * @return this @code{NamedEntityRecognizer}.
   * Setting the noWordBefore characters requires recompilation, which is inefficient. Use with care.
   */
  public NamedEntityRecognizer setNoWordBefore(String noWordBefore) {
    if (! noWordBefore.equals(this.noWordBefore)) {
      this.noWordBefore = noWordBefore != null ? noWordBefore : "";
      triener = null;
    }
    return this;
  }

  /**
   * Set the noWordAfter characters for this @code{NamedEntityRecognizer}.
   * @param noWordAfter
   * @return this @code{NamedEntityRecognizer}.
   * Setting the noWordAfter characters requires recompilation, which is inefficient. Use with care.
   */
  public NamedEntityRecognizer setNoWordAfter(String noWordAfter) {
    if (! noWordAfter.equals(this.noWordAfter)) {
      this.noWordAfter = noWordAfter != null ? noWordAfter : "";
      triener = null;
    }
    return this;
  }

  /**
   * Make a handler for the grammar events, which initializes the triener.
   * @param triener
   * @return a {@code TextLineStreamApi} event handler
   */
  private TextLineStreamApi makeTrieGrammarHandler(TrieNER triener) throws ConfigurationException, PipelineException {
    return new TextLineStreamApi() {

      private int lineNumber = 0;

      @Override
      public void line(String line) {
        ++lineNumber;
        if (line.trim().length() > 0) {
          String[] parts = line.split("\\s*<-\\s*", 2);
          if (parts.length != 2) {
            throw new IllegalArgumentException("Bad trie syntax for "+grammarSource.toString()+" in line "+lineNumber+": "+line+
                "\n\tThis line contains "+parts.length+" parts (must be 2).");
          }
          if (parts[1].equals("")) {
            throw new IllegalArgumentException("Bad trie syntax for "+grammarSource.toString()+" in line "+lineNumber+": "+line+
                "\n\tSecond part of a rule must not be empty).");
          }
          String nttid = parts[0];
          parts = parts[1].split("\\t");
          for (int i = 0; i < parts.length; ++i) {
            String ntt = parts[i];
            triener.getTrie().put(ntt, nttid);
          }
        }
      }

    };
  }

  /**
   * Make a {@code TrieNER} which inserts markup according to the matchNodeTemplate when text is recognized.
   * @param wordChars
   * @param noWordBefore
   * @param noWordAfter
   * @return
   */
  private TrieNER makeTrieNER(String wordChars, String noWordBefore, String noWordAfter) {
    return new TrieNER(wordChars, noWordBefore, noWordAfter) {
      @Override
      public void match(CharSequence text, int start, int end, List<String> ids) {
        SmaxElement matchNode = matchNodeTemplate.shallowCopy();
        matchNode.setAttribute(attributeName, String.join("\t", ids));
        transformedDocument.insertMarkup(matchNode, balancing, start, end);
      }
      @Override
      public void noMatch(CharSequence text, int start, int end) {
        // No action is needed.
      }
    };
  }

  /**
   * Process a complete document.
   * The synchronized ensures that instance variables are thread-safe.
   * The trie-NER will be instantiated when the grammar has not yet been read, or when the grammar has changed.
   * @param completeDocument
   * @see org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer#process(org.greenmercury.speat.smax.SmaxDocument)
   */
  @Override
  public synchronized void process(SmaxDocument completeDocument) throws ConfigurationException, PipelineException, IOException {
    ConfigurationException.ifNull("matchNodeTemplate", matchNodeTemplate);
    ConfigurationException.ifNull("grammarReader", grammarReader);
    long grammarSourceLastModified = grammarSource.getLastModified();
    // Reset the trie-NER when the grammar has been modified.
    if (triener != null && grammarLastCompiled < grammarSourceLastModified) {
      triener = null;
    }
    // Set the trie-NER if we do not have it.
    if (triener == null) {
      Instant startTime = Instant.now();
      triener = makeTrieNER(wordChars, noWordBefore, noWordAfter);
      grammarReader.setHandler(makeTrieGrammarHandler(triener));
      grammarLastCompiled = grammarSourceLastModified;
      grammarReader.read();
      // Log sizes of all grammars that are in memory.
      String grammarURL = grammarSource.getUrl() != null ? grammarSource.getUrl().toString() : grammarSource.getClass().getName();
      trieStoreBytes.put(grammarURL, triener.getTrie().sizeInBytes());
      Instant endTime = Instant.now();
      getLogger().info("Trie for "+grammarURL+" has been compiled in "+Duration.between(startTime, endTime).toMillis()+" ms, from "+startTime.toString()+" to "+endTime.toString());
      getLogger().info("  The trie-store contains "+trieStoreBytes.size()+" compiled tries. Sizes (MBytes):");
      trieStoreBytes.entrySet().stream().forEach(entry ->
        getLogger().info("  "+entry.getKey()+": "+String.format("%.3f", entry.getValue() / 1048576f))
      );
    }

    String grammarURL = grammarSource.getUrl() != null ? grammarSource.getUrl().toString() : grammarSource.getClass().getName();
    Instant startTime = Instant.now();
    int fragments = 0;

    // Process the input document with the triener.
    //super.process(completeDocument);
    this.completeDocument = completeDocument;
    if (transformWithinNode == null) {
      ++ fragments;
      transform(completeDocument);
    } else {
      for (SmaxElement subMarkup : completeDocument.matchingNodes(transformWithinNode)) {
        ++ fragments;
        transform(new SmaxDocument(subMarkup, completeDocument.getContentView()));
      }
    }

    Instant endTime = Instant.now();
    getLogger().info("Named entity recogition on "+fragments+" fragments with "+grammarURL+" took "+Duration.between(startTime, Instant.now()).toMillis()+" ms, from "+startTime.toString()+" to "+endTime.toString());

    // Pass on to the handler.
    handler.process(completeDocument);
  }

  /**
   * Do named entity recognition on (parts of) a document.
   * It is more efficient to scan fragments from the content, because the TrieNER makes a normalized copy.
   * @param document
   * @see org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer#transform(org.greenmercury.speat.smax.SmaxDocument)
   */
  @Override
  protected void transform(SmaxDocument document) throws ConfigurationException, PipelineException {
    transformedDocument = document;
    triener.scan(transformedDocument.getContentView(), caseInsensitiveMinLength, fuzzyMinLength);
}

}
