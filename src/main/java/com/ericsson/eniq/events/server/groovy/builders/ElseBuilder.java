package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Creates a Groovy <i>else</i> condition with the block code.<br/>
 * <code>else{<br/>...</br>}</code>
 * @author ejedmar
 * @since 2011
 *
 */
public class ElseBuilder extends GroovyLangBuilderImpl {

    public ElseBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        writer.append(ELSE).append(LCURLY)
                .append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), LCURLY, "#else"));
        node.jjtGetChild(0).childrenAccept(visitor, data);
        writer.append(RCURLY);
    }
}
