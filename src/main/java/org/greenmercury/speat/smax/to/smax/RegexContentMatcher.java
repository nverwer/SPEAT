package org.greenmercury.speat.smax.to.smax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.greenmercury.speat.ConfigurationException;
import org.greenmercury.speat.smax.SmaxDocument;
import org.greenmercury.speat.smax.SmaxElement;

/**
 * A SMAX document transformer that inserts markup around text content that matches a given regular expression.
 *<p>
 * @author Rakensi
 */
public class RegexContentMatcher extends SmaxDocumentTransformer {

  private Pattern pattern;
  private SmaxElement matchNodeTemplate;

  /**
   * @param regex
   * @return the {@code RegexContentMatcher} itself.
   */
  public RegexContentMatcher setPattern(String regex) {
    pattern = Pattern.compile(regex);
    return this;
  }

  /**
   * @param pattern regular expression pattern to match
   * @return the {@code RegexContentMatcher} itself.
   */
  public RegexContentMatcher setPattern(Pattern pattern) {
    this.pattern = pattern;
    return this;
  }

  /**
   * @param matchNodeTemplate template for the XML element that is inserted for each matching content fragment
   * @return the {@code RegexContentMatcher} itself.
   */
  public RegexContentMatcher setMatchNodeTemplate(SmaxElement matchNodeTemplate) {
    this.matchNodeTemplate = matchNodeTemplate;
    return this;
  }

  /* Transform a SMAX document.
   * @see org.greenmercury.speat.smax.to.smax.SmaxDocumentTransformer#transform()
   */
  @Override
  protected void transform(SmaxDocument document) throws ConfigurationException {
    ConfigurationException.ifNull("pattern", pattern);
    ConfigurationException.ifNull("matchNodeTemplate", matchNodeTemplate);
    Matcher matcher = pattern.matcher(document.getContentView());
    while (matcher.find()) {
      SmaxElement matchNode = matchNodeTemplate.shallowCopy();
      document.insertMarkup(matchNode, balancing, matcher.start(), matcher.end());
    }
  }

}
