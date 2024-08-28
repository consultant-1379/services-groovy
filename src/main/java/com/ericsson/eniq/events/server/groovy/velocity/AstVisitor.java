package com.ericsson.eniq.events.server.groovy.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.velocity.runtime.parser.node.ASTComment;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.visitor.BaseVisitor;

import com.ericsson.eniq.events.server.groovy.builders.GroovyLangBuilder;
import com.ericsson.eniq.events.server.groovy.builders.LangBuilderFactory;

/**
 * A Vistor that traverses a Velocity AST and constructs a Groovy code.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class AstVisitor extends BaseVisitor {

    private static final String REF = "ref";
    private static final String ELSE = "elseStmt";
    private static final String ELSE_IF = "elseIfStmt";
    private static final String IF = "ifStmt";
    private static final String COMMENT = "comment";
    private static final String TEXT = "text";
    private static final String CALL = "call";
    private static final String MACRO = "macro";
    private static final String FOREACH = "foreach";
    private static final String FOO = "foo";
    private static final String SET = "set";
    
    private final Writer writer;

    public AstVisitor(final Writer writer) {
        this.writer = writer;
    }
    
    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTSetDirective, java.lang.Object)
     */
    @Override
    public Object visit(final ASTSetDirective node, final Object data) {
        try {
            getBuilder(SET).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTDirective, java.lang.Object)
     */
    @Override
    public Object visit(final ASTDirective node, final Object data) {
        try {
            final String directiveName = node.getDirectiveName();
            GroovyLangBuilder builder = null;
            if (isFoo(directiveName)) {
                builder = getBuilder(FOO);
            } else if (isMethodDefinition(directiveName)) {
                builder = getBuilder(directiveName);
            } else {
                builder = getBuilder(CALL);
            }
            builder.build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTText, java.lang.Object)
     */
    @Override
    public Object visit(final ASTText node, final Object data) {
        try {
            getBuilder(TEXT).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Object visit(final ASTWord node, final Object data) {
        try {
            getBuilder(TEXT).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTComment, java.lang.Object)
     */
    @Override
    public Object visit(final ASTComment node, final Object data) {
        try {
            getBuilder(COMMENT).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTIfStatement, java.lang.Object)
     */
    @Override
    public Object visit(final ASTIfStatement node, final Object data) {
        try {
            getBuilder(IF).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTElseIfStatement, java.lang.Object)
     */
    @Override
    public Object visit(final ASTElseIfStatement node, final Object data) {
        try {
            getBuilder(ELSE_IF).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTElseStatement, java.lang.Object)
     */
    @Override
    public Object visit(final ASTElseStatement node, final Object data) {
        try {
            getBuilder(ELSE).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.velocity.runtime.visitor.BaseVisitor#visit(org.apache.velocity.runtime.parser.node.ASTReference, java.lang.Object)
     */
    @Override
    public Object visit(final ASTReference node, final Object data) {
        try {
            getBuilder(REF).build(node, data, writer);
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private GroovyLangBuilder getBuilder(final String builderName) {
        return LangBuilderFactory.valueOf(builderName).getBuilder(this);
    }
    
    private boolean isMethodDefinition(final String directiveName) {
        return Arrays.asList(FOREACH, MACRO).contains(directiveName);
    }

    private boolean isFoo(final String directiveName) {
        return Arrays.asList(FOO, "temp_imsi_ranking_table", "EVENT_TYPE_TABLE", "TOPOLOGY_DATA_TABLE", "RAW_VIEW", "NETWORKDATA").contains(directiveName);
    }
}
