package com.ericsson.eniq.events.server.groovy.builders;

import static com.ericsson.eniq.events.server.groovy.utils.GroovyConstants.*;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

import com.ericsson.eniq.events.server.groovy.utils.TextSplitter;

/**
 * Creates a method call.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class MethodCallBuilder extends GroovyLangBuilderImpl {

    public MethodCallBuilder(final ParserVisitor visitor) {
        super(visitor);
    }

    /**
     * TODO refactoring
     * @see com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder#build(org.apache.velocity.runtime.parser.node.Node, java.lang.Object, java.io.Writer)
     */
    @Override
    public void build(final Node node, final Object data, final Writer writer) throws IOException {
        final ASTDirective directive = (ASTDirective) node;
        writer.append(directive.getDirectiveName());
        if (node.jjtGetNumChildren() > 0) {
            writer.append(LPAREN);
            Node currentNode = node;
            Node nextNode = node.jjtGetChild(0);
            writer.append(whiteSpacesBetween(currentNode.getFirstToken(), nextNode.getFirstToken(), LPAREN, HASH,
                    directive.getDirectiveName()));
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                currentNode = node.jjtGetChild(i);
                final String literal = currentNode.literal();
                if (ASTObjectArray.class.getName().equals(currentNode.getClass().getName())) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int k = 0; k < currentNode.jjtGetNumChildren(); k++) {
                        sb.append(substituteVelocityCount(sub(currentNode.jjtGetChild(k).literal())));
                        if (k+1 < currentNode.jjtGetNumChildren()) {
                            sb.append(",");
                        }
                    }
                    sb.append("]");
                    writer.append(sb.toString());
                } else {
                    final String substitution = substituteVelocityCount(sub(literal));
                    writer.append(substitution);
                }
                if (i + 1 < node.jjtGetNumChildren()) {
                    nextNode = node.jjtGetChild(i + 1);
                    writer.append(COMA);
                    writer.append(whiteSpacesBetween(currentNode.getLastToken().next, nextNode.getFirstToken(), LPAREN,
                            RPAREN, RBRACKET, literal));
                }
            }
            writer.append(whiteSpacesBetween(currentNode.getLastToken().next, node.getLastToken(), RBRACKET));
            writer.append(RPAREN).append(SEMICOLON);
        } else {
            writer.append(LPAREN)
                    .append(RPAREN)
                    .append(SEMICOLON)
                    .append(whiteSpacesBetween(node.getFirstToken(), node.getLastToken(), LPAREN, RPAREN, HASH,
                            directive.getDirectiveName()));
        }
        if (endsWithNewLine(node)) {
            writer.append(textSplitter.outAsLines(TextSplitter.NEW_LINE));
        }
        writer.append(newLineIfNecessary(node));
    }
    
    private String sub(final String str) {
        String replaceAll = str;
        if (StringUtils.startsWith(replaceAll, "\"") && StringUtils.endsWith(replaceAll, "\"")) {
             replaceAll = Pattern.compile("(.*?)\\$\\{?([^\\$\"\\s,\\}\\]\\[]+)\\}?").matcher(str).replaceAll("$1\\${\\$$2}");
        }
        return Pattern.compile("^\\$\\{(.+?)\\}$").matcher(replaceAll).replaceAll("\\$$1");
    }

}
