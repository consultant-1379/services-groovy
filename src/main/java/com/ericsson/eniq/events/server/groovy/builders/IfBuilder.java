package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

import com.ericsson.eniq.events.server.groovy.utils.TextSplitter;

/**
 * Creates a Groovy <i>if</i> statement.<br/><code>if([condition]){<br/>...<br/>}
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class IfBuilder extends GroovyLangBuilderImpl {

    public IfBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final String conditions = node.jjtGetChild(0).literal();
        final String escaped = substituteVelocityCount(transformNullCheck(substituteFormalRef(conditions)));
        
        writer.append(IF)
                .append(LPAREN)
                .append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), LPAREN, "#if"))
                .append(escaped);
        writer.append(RPAREN).append(LCURLY);
        writer.append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken().next, node.jjtGetChild(1)
                .getFirstToken(), RPAREN));
        node.jjtGetChild(1).childrenAccept(visitor, data);
        writer.append(RCURLY);
        if (node.jjtGetNumChildren() <= 2) {
            writer.append(newLineIfNecessary(node));
        }
        for (int i = 2; i < node.jjtGetNumChildren(); i++) {
            writer.append(whiteSpacesBetween(node.jjtGetChild(i - 1).getLastToken().next, node.jjtGetChild(i)
                    .getFirstToken()));
            node.jjtGetChild(i).jjtAccept(visitor, data);
        }
        if (node.jjtGetNumChildren() > 2) {
            writer.append(newLineIfNecessary(node));
        }
        writer.append(SEMICOLON);
        if (endsWithNewLine(node)) {
            writer.append(textSplitter.outAsLines(TextSplitter.NEW_LINE));
        }
    }
}
