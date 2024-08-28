package com.ericsson.eniq.events.server.groovy.builders;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

import com.ericsson.eniq.events.server.groovy.utils.TextSplitter;

/**
 * Base implementation of @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder.
 * It contains just some helper methods.
 * @author ejedmar
 * @since 2011
 *
 * TODO refactoring
 */
public abstract class GroovyLangBuilderImpl implements GroovyLangBuilder {

    protected final ParserVisitor visitor;

    protected final TextSplitter textSplitter = new TextSplitter();

    protected static final String FORMAL_REF_PATTERN = "\\$\\{(.+?)\\}";

    protected static final String VELOCITY_COUNT_PATTERN = "\\$velocityCount";

    private static final String VELOCITY_NULL_CHECK = "\\(!\\s*\\$(.+?)\\s*\\)\\s*?&&\\s*?\\(\"\\$!\\1\"\\s*==\\s*\"\"\\s*\\)";//+&&\\s+\\(\"\\$\\!$1\"\\s+==\\s+\"\\)";

    protected static Pattern formalRefPattern = Pattern.compile(FORMAL_REF_PATTERN);

    protected static Pattern velocityCountPattern = Pattern.compile(VELOCITY_COUNT_PATTERN);

    private static Pattern nullCheckPattern = Pattern.compile(VELOCITY_NULL_CHECK);

    public GroovyLangBuilderImpl(final ParserVisitor visitor) {
        this.visitor = visitor;
    }

    protected String newLineIfNecessary(final Node node) {
        if (endsWithNewLine(node)) {
            return TextSplitter.NEW_LINE;
        }
        return StringUtils.EMPTY;
    }

    protected boolean endsWithNewLine(final Node node) {
        return node.getLastToken().image.endsWith(TextSplitter.NEW_LINE);
    }

    protected String whiteSpacesBetween(final Token startToken, final Token endToken, final String... ignore) {
        if (startToken == null || endToken == null) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        Token t = startToken;
        while (t != null & t != endToken) {
            sb.append(removeForbidden(t.image, ignore));
            t = t.next;
        }
        return sb.toString();
    }

    private String removeForbidden(final String image, final String... ignore) {
        final String[] replacements = new String[ignore.length];
        Arrays.fill(replacements, StringUtils.EMPTY);
        return StringUtils.replaceEach(image, ignore, replacements);
    }

    protected String substituteVelocityCount(final String velocityFragment) {
        final Matcher matcher = velocityCountPattern.matcher(velocityFragment);
        return matcher.replaceAll("peek()");
    }

    protected String substituteFormalRef(final String velocityFragment) {
        final Matcher matcher = formalRefPattern.matcher(velocityFragment);
        return matcher.replaceAll("\\$$1");
    }

    //TODO refactor
    protected String transformNullCheck(final String velocityFragment) {
        final Matcher matcher = nullCheckPattern.matcher(velocityFragment);
        final String replaceAll = matcher.replaceAll("(\\$$1 == null)");
        final String isEmpty = Pattern.compile("\\$(.+?)\\s*==\\s*\"\"").matcher(replaceAll)
                .replaceAll("isEmpty(\\$$1)");
        final String isNotEmpty = Pattern.compile("\\$(.+?)\\s*!=\\s*\"\"").matcher(isEmpty)
                .replaceAll("isNotEmpty(\\$$1)");
        final String isNotEmptyArray = Pattern.compile("\\$([\\w\\.\\(\\)]+?)\\s?!=\\s?\\[\\]").matcher(isNotEmpty)
                .replaceAll("isArrayNotEmpty(\\$$1)");
        final String isEmptyArray = Pattern.compile("\\$([\\w\\.\\(\\)]+?)\\s*==\\s*\\[\\]").matcher(isNotEmptyArray)
                .replaceAll("isArrayEmpty(\\$$1)");
        return isEmptyArray;
    }
}
