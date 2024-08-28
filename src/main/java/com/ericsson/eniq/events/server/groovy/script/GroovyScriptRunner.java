package com.ericsson.eniq.events.server.groovy.script;

import java.util.Map;

/**
 * Runs a Groovy script for SQL query generation.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public interface GroovyScriptRunner {
    
    /**
     * @param macroClass class with global method definitions
     * @param velocityClass class with SQL query generation code
     * @param context execution context(input parameters)
     * @return SQL query
     */
    String run(String macroClass, String velocityClass, Map<String, ?> context);

}
