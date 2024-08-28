package com.ericsson.eniq.events.server.groovy.builders;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Processes a reference if it's not a part of a : <i>if</i> condition, <i>else if</i> condition,
 * method definition, method call statement, assignment statement nor <i>for</i> variable.
 * @author ejedmar
 * @since 2011
 *
 */
public class ReferenceBuilder extends GroovyLangBuilderImpl {

    public ReferenceBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final String literal = node.literal();
        final String escaped = substituteVelocityCount(substituteFormalRef(literal));
        writer.append("out(").append(escaped).append(");");
    }
}
