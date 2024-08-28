package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Creates a Groovy comment line.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class CommentBuilder extends GroovyLangBuilderImpl {

    public CommentBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        writer.append(COMMENT);
        writer.append(newLineIfNecessary(node));
    }
}
