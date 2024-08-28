package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;


/**
 * Creates a Groovy <i>for</i> loop. Due to $velocityCount - the implicit Velocity loop counter - it is wrapped with
 * <i>push()</i> and <i>pop()</i> stack methods - to get and release a $velocityCount in a proper scope.
 * @author ejedmar
 * @since 2011
 *
 */
public class ForeachBuilder extends GroovyLangBuilderImpl {

    public ForeachBuilder(final ParserVisitor visitor) {
        super(visitor);
    }
    
    /**
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        writer.append("f:{push();")
                .append(FOR)
                .append(LPAREN)
                .append(whiteSpacesBetween(node.getFirstToken(), node.jjtGetChild(0).getFirstToken(), LPAREN,
                        "#foreach", node.jjtGetChild(0).literal()));
        writer.append(node.jjtGetChild(0).literal());//var      
        writer.append(whiteSpacesBetween(node.jjtGetChild(0).getLastToken().next, node.jjtGetChild(1).getFirstToken(), IN));
        writer.append(node.jjtGetChild(1).literal());//in     
        writer.append(whiteSpacesBetween(node.jjtGetChild(1).getLastToken().next, node.jjtGetChild(2).getFirstToken(), IN));
        writer.append(substituteRange(substituteFormalRef(node.jjtGetChild(2).literal())));//list
        writer.append(RPAREN)
                .append(LCURLY)
                .append(whiteSpacesBetween(node.jjtGetChild(2).getLastToken().next,
                        node.jjtGetChild(3).getFirstToken(), RPAREN));

        node.jjtGetChild(3).childrenAccept(visitor, data);
        writer.append(whiteSpacesBetween(node.jjtGetChild(3).getLastToken().next, node.getLastToken(), "#end"));
        writer.append("increment();").append(RCURLY).append(SEMICOLON).append("pop();").append(RCURLY).append(SEMICOLON);
        writer.append(newLineIfNecessary(node));
    }
    
    private String substituteRange(final String literal) {
        return Pattern.compile("\\[(\\d+\\.\\.\\d+)\\]").matcher(literal).replaceAll("$1");
    }
}
