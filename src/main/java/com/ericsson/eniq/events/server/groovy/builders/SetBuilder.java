package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Creates an assignment statement. Velocities variables are global-scoped, hence it's not a standard<br/>
 * <code>$var = value</code><br/>
 * statement, but it's in a form<br/>
 * <code>set("var", value)</code><br/>It allows to put variables in a global-scoped map.
 * @author ejedmar
 * @since 2011
 *
 * TODO refactoring
 */
public class SetBuilder extends GroovyLangBuilderImpl {

    public SetBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        try {
            final String varName = ((ASTReference) node.jjtGetChild(0)).getRootString();
            if (ASTObjectArray.class.getName().equals(node.jjtGetChild(1).jjtGetChild(0).getClass().getName())) {
                final Node array = node.jjtGetChild(1).jjtGetChild(0);
                final StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < array.jjtGetNumChildren(); i++) {
                    sb.append(sub(array.jjtGetChild(i).literal()));
                    if (i+1 < array.jjtGetNumChildren()) {
                        sb.append(",");
                    }
                }
                sb.append("]");
                writer.append(String.format("set(\"%s\", %s);", varName, sb.toString()));
            } else {
                final String varValue = node.jjtGetChild(1).literal();
                final boolean isConcatenation = StringUtils.contains(varValue, "\"");
                if (isConcatenation) {

                    final String substringBetween = StringUtils.substringBetween(varValue, "\"", "\"");
                    final String processedString = StringUtils.defaultString(StringUtils.substringBefore(varValue, "\""))
                            + "\""
                            + Pattern.compile("\\$\\{?([^\\$\"\\s,\\}\\]\\[]+)\\}?").matcher(substringBetween)
                                    .replaceAll("\\${\\$$1}") + "\""
                            + StringUtils.defaultString(StringUtils.substringAfterLast(varValue, "\""));
                    writer.append(String.format("set(\"%s\", %s);", varName, processedString));
                } else {
                    final Matcher matcher = formalRefPattern.matcher(varValue);
                    writer.append(String.format("set(\"%s\", %s);", varName, matcher.replaceAll("\\$$1")));
                }
            }
            writer.append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), "#set", LPAREN,
                    RPAREN));
            writer.append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken(), node.jjtGetChild(1).getFirstToken(),
                    EQUALS, varName, DOLLAR, RPAREN));
            writer.append(newLineIfNecessary(node));

        } catch (final Exception e) {
            System.out.println(node.literal());
            throw new IOException(e);
        }
    }

    private String sub(final String str) {
        String replaceAll = str;
        if (StringUtils.startsWith(replaceAll, "\"") && StringUtils.endsWith(replaceAll, "\"")) {
             replaceAll = Pattern.compile("(.*?)\\$\\{?([^\\$\"\\s,\\}\\]\\[]+)\\}?").matcher(str).replaceAll("$1\\${\\$$2}");
        }
        return replaceAll;
    }
}
