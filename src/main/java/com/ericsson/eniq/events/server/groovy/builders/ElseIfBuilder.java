package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Creates <i>else if</i> Groovy structure.
 * <br/><code>else if([condition]){<br/>...<br/>}
 * @author ejedmar
 * @since 2011
 *
 */
public class ElseIfBuilder extends GroovyLangBuilderImpl {

    public ElseIfBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final String conditions = node.jjtGetChild(0).literal();
        final String escaped = substituteVelocityCount(transformNullCheck(substituteFormalRef(conditions)));
        writer.append(ELSE_IF)
                .append(LPAREN)
                .append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), LPAREN,
                        "#elseif")).append(escaped);
        writer.append(RPAREN)
                .append(LCURLY)
                .append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken().next, node.jjtGetChild(1)
                        .getFirstToken(), RPAREN));
        node.jjtGetChild(1).childrenAccept(visitor, data);
        writer.append(RCURLY);
    }
}
