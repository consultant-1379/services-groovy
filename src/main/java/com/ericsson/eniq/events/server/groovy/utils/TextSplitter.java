package com.ericsson.eniq.events.server.groovy.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Text processor. It's able to tokenize a given text basing on a delimiter and to assembly this text again adding
 * some prefixes and suffixes.
 * @author ejedmar
 * @since 2011
 *
 */
public class TextSplitter {

    public final static String NEW_LINE = "\n";

    private final static String ESCAPED_NEW_LINE = "\\n";

    private final static String DEFAULT_FRAGMENT_PREFIX = "out(\"";

    private final static String DEFAULT_FRAGMENT_SUFFIX = "\");";

    private String delimiter = NEW_LINE;

    private String escapedDelimiter = ESCAPED_NEW_LINE;

    public TextSplitter(final String delimiter, final String escapedDelimiter) {
        this.delimiter = delimiter;
        this.escapedDelimiter = escapedDelimiter;
    }

    public TextSplitter() {
    }

    /**
     * Splits text <i>text</i> using given <i>delimiter</i>.
     * @param text a text to be splitted
     * @param delimiter1 a delimiter
     * @return Arrays of tokens
     */
    public String[] tokenize(final String text, final String delimiter1) {
        int start = 0;
        final List<String> fragments = new ArrayList<String>();
        int index = text.indexOf(delimiter1);
        if (index != -1) {
            do {
                final String fragment = StringUtils.substring(text, start, index);
                fragments.add(fragment);
                start = index + delimiter1.length();
                index = text.indexOf(delimiter1, start);
            } while (index != -1);
            if (start != text.length()) {
                fragments.add(StringUtils.substring(text, start));
            }
        } else {
            fragments.add(text);
        }
        return fragments.toArray(new String[] {});
    }

    /**
     * Constructs a text from given <i>textFragments</i> wrapping each fragment with a prefix - <i>fragmentPrefix</i>
     * and a suffix - <i>fragmentSuffix</i>
     * @param textFragments text fragments to be merged
     * @param fragmentPrefix fragment prefix
     * @param fragmentSuffix fragment suffix
     * @param endsWithDelimiter flag indicating if text before tokenization ended with a delimiter
     * @param b 
     * @return merged text
     */
    public String buildText(final String[] textFragments, final String fragmentPrefix, final String fragmentSuffix,
            final boolean endsWithDelimiter, final boolean startsWithDelimiter) {
        final StringBuilder sb = new StringBuilder();
        final int lastIndex = textFragments.length - 1;
        if (startsWithDelimiter) {
            sb.append(fragmentPrefix).append(escapedDelimiter).append(fragmentSuffix);
        }
        for (int i = 0; i < lastIndex; i++) {
            if (StringUtils.isNotEmpty(textFragments[i])) {
                sb.append(fragmentPrefix).append(StringEscapeUtils.escapeJava(textFragments[i]))
                        .append(escapedDelimiter).append(fragmentSuffix);
            }
            sb.append(delimiter);
        }
        if (StringUtils.isNotEmpty(textFragments[lastIndex]) || endsWithDelimiter) {
            sb.append(fragmentPrefix).append(StringEscapeUtils.escapeJava(textFragments[lastIndex]));
            if (endsWithDelimiter) {
                sb.append(escapedDelimiter);
            }
            sb.append(fragmentSuffix);
        }
        return sb.toString();
    }

    /**
     * Shortcut method for @see {@link #buildText(String[], String, String, boolean)}. Splits text to lines with
     * default settings.
     * @param text text to be processed
     * @return processed text
     */
    public String outAsLines(final String text) {
        return buildText(tokenize(text, delimiter), DEFAULT_FRAGMENT_PREFIX, DEFAULT_FRAGMENT_SUFFIX,
                endsWithNewline(text), startsWithNewline(text));
    }

    /**
     * Shortcut method for @see {@link #buildText(String[], String, String, boolean)}. Splits text to lines decorating 
     * each line with <i>prefix</i> and <i>suffix</i>.
     * @param text text to be processed
     * @param fragmentPrefix prefix
     * @param fragmentSuffix suffix
     * @return processed text
     */
    public String outAsLines(final String text, final String fragmentPrefix, final String fragmentSuffix) {
        return buildText(tokenize(text, delimiter), fragmentPrefix, fragmentSuffix, endsWithNewline(text),
                startsWithNewline(text));
    }

    private boolean endsWithNewline(final String text) {
        return StringUtils.endsWith(text, delimiter);
    }

    private boolean startsWithNewline(final String text) {
        return StringUtils.startsWith(text, delimiter);
    }
}