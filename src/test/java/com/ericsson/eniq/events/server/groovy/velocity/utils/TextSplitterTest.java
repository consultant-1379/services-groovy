package com.ericsson.eniq.events.server.groovy.velocity.utils;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.ericsson.eniq.events.server.groovy.utils.TextSplitter;

public class TextSplitterTest {
 
    private static final String DOUBLE_QUOTE = "\"";

    private static final String DELIMITER = "\r\n";
    
    private static final String ESCAPED_DELIMITER = "\\r\\n";

    private final TextSplitter textSplitter = new TextSplitter(DELIMITER, ESCAPED_DELIMITER);

    @Test 
    public void testOneNewLine() {
        final String oneNewLine = "\r\n";
        final String outAsLines = textSplitter.outAsLines(oneNewLine, DOUBLE_QUOTE, DOUBLE_QUOTE);
        final String operand = "\"\\r\\n\"\"\\r\\n\"";
        Assert.assertThat(outAsLines,
                Matchers.equalTo(operand));
    }

    @Test
    public void testOneEmptyLine() {
        final String oneEmptyLine = "";
        Assert.assertThat(textSplitter.outAsLines(oneEmptyLine, DOUBLE_QUOTE, DOUBLE_QUOTE),
                Matchers.equalTo(""));
    }
    
    @Test
    public void testOneLine() {
        final String oneLine = "sample text";
        Assert.assertThat(textSplitter.outAsLines(oneLine, DOUBLE_QUOTE, DOUBLE_QUOTE),
                Matchers.equalTo("\"sample text\""));
    }

    @Test
    public void testOneLineWithNewLine() {
        final String oneLineWithNewLine = "sample text\r\n";
        Assert.assertThat(textSplitter.outAsLines(oneLineWithNewLine, DOUBLE_QUOTE, DOUBLE_QUOTE),
                Matchers.equalTo("\"sample text\\r\\n\""));
    }

    @Test
    public void testMultiNewLines() {
        final String multiNewLines = "\r\n\r\n\r\n";
        Assert.assertThat(textSplitter.outAsLines(multiNewLines, DOUBLE_QUOTE, DOUBLE_QUOTE),
                Matchers.equalTo("\"\\r\\n\"\r\n\r\n\"\\r\\n\"")); 
    }

    @Test
    public void testMultiLines() {
        final String multiLines = "sample line1\r\nsample line2\r\nsample line3\r\n";
        Assert.assertThat(textSplitter.outAsLines(multiLines, DOUBLE_QUOTE, DOUBLE_QUOTE),
                Matchers.equalTo("\"sample line1\\r\\n\"\r\n\"sample line2\\r\\n\"\r\n\"sample line3\\r\\n\""));
    }
}
