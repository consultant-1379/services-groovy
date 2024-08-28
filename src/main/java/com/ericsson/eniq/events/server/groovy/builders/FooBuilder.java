package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Processes <i>#foo</i> elements and similar - it's not a macro/directive invocation. it is treated as a text.
 * @author ejedmar
 * @since 2011
 *
 */
public class FooBuilder extends GroovyLangBuilderImpl {

    public FooBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final ASTDirective directive = (ASTDirective) node;
        final StringBuilder sb = new StringBuilder();
        if (node.jjtGetNumChildren() > 0) {
            Node currentNode = node;
            Node nextNode = node.jjtGetChild(0);
            sb.append(HASH).append(directive.getDirectiveName());
            writer.append(textSplitter.outAsLines(sb.toString()));
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                writer.append(textSplitter.outAsLines(whiteSpacesBetween(currentNode.getFirstToken(),
                        nextNode.getFirstToken(), "#foo", currentNode.literal(), nextNode.literal())));
                currentNode = node.jjtGetChild(i);
                if (i + 1 < node.jjtGetNumChildren()) {
                    nextNode = node.jjtGetChild(i + 1);
                }
                node.jjtGetChild(i).jjtAccept(visitor, data);
            }
            writer.append(textSplitter.outAsLines(RPAREN)).append(
                    whiteSpacesBetween(currentNode.getLastToken(), node.getLastToken(), currentNode.literal()));
        } else {
            sb.append(HASH).append(directive.getDirectiveName()).append(" ");
            writer.append(textSplitter.outAsLines(sb.toString()));
        }
        writer.append(newLineIfNecessary(node));
    }
}
