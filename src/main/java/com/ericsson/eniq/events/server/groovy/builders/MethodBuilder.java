package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

import com.ericsson.eniq.events.server.groovy.velocity.AstVisitor;

/**
 * Creates a method definition.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class MethodBuilder extends GroovyLangBuilderImpl {

    public MethodBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final String macroName = node.jjtGetChild(0).literal();
        writer.append("void ")
                .append(macroName)
                .append(LPAREN)
                .append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), LPAREN, "#macro"));
        if (node.jjtGetNumChildren() > 2) {
            writer.append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken(), node.jjtGetChild(1).getFirstToken(),
                    macroName));
            final StringBuilder sbIn = new StringBuilder();
            for (int i = 2; i < node.jjtGetNumChildren(); i++) {
                final String macroArg = node.jjtGetChild(i - 1).literal();
                sbIn.append(macroArg);
                if (i + 1 < node.jjtGetNumChildren()) {
                    sbIn.append(COMA);
                } else {
                    sbIn.append(RPAREN).append(LCURLY);
                }
                sbIn.append(whiteSpacesBetween(node.jjtGetChild(i - 1).getLastToken(), node.jjtGetChild(i)
                        .getFirstToken(), macroArg, RPAREN));
            }
            writer.append(sbIn);
        } else {
            writer.append(RPAREN).append(LCURLY);
            writer.append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken(), node.jjtGetChild(1).getFirstToken(),
                    macroName, RPAREN));
        }
        final StringWriter sw = new StringWriter();
        final AstVisitor macroVisitor = new AstVisitor(sw);
        node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(macroVisitor, null);
        final String body = sw.toString();
        writer.append(body).append(RCURLY).append(SEMICOLON);
        writer.append(newLineIfNecessary(node));
    }
}
