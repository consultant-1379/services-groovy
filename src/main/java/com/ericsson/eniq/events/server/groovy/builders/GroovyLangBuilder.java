package com.ericsson.eniq.events.server.groovy.builders;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.runtime.parser.node.Node;

/**
 * Groovy language element builder. Classes implementing this interface should create a valid Groovy code fragment.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public interface GroovyLangBuilder {

    /**
     * Creates a valid Groovy code fragment.
     * @param node Velocity AST node
     * @param data data object
     * @param writer the code is written with this writer
     * @throws IOException
     */
    void build(final Node node, final Object data, Writer writer)  throws IOException;
}
