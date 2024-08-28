package com.ericsson.eniq.events.server.groovy.builders;

import org.apache.velocity.runtime.parser.node.ParserVisitor;

/**
 * Groovy's element builders factory.
 * 
 * @author ejedmar
 * @since 2011
 */
public enum LangBuilderFactory {

    foreach {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new ForeachBuilder(visitor);
        }
    },
    foo {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new FooBuilder(visitor);
        }
    },
    macro {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new MethodBuilder(visitor);
        }
    },
    call {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new MethodCallBuilder(visitor);
        }
    },
    ifStmt {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new IfBuilder(visitor);
        }
    },
    elseIfStmt {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new ElseIfBuilder(visitor);
        }
    },
    elseStmt {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new ElseBuilder(visitor);
        }
    },
    set {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new SetBuilder(visitor);
        }
    },
    ref {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new ReferenceBuilder(visitor);
        }
    },
    comment {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new CommentBuilder(visitor);
        }
    },
    text {
        @Override
        public GroovyLangBuilder getBuilder(final ParserVisitor visitor) {
            return new TextBuilder(visitor);
        }
    };

    
    /**
     * Constructs a Groovy element Builder.
     * 
     * @param visitor current AST visitor
     * @return the builder
     */
    public abstract GroovyLangBuilder getBuilder(ParserVisitor visitor);
}
