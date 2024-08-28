package com.ericsson.eniq.events.server.groovy.builders;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Processes any text that is not a part of a language structure. It's translated to<br>
 * <code>out("text")</code><br/>code snippet.
 * @author ejedmar
 * @since 2011
 *
 */
public class TextBuilder extends GroovyLangBuilderImpl {

    public TextBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final String literal = node.literal();
        if (node.jjtGetParent().getClass().equals(ASTprocess.class.getName())) {
            writer.append(literal);
        }
        writer.append(textSplitter.outAsLines(literal));
        writer.append(newLineIfNecessary(node));
    }
}
