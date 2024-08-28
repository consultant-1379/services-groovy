/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.groovy.templates;

import groovy.lang.GroovyClassLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.groovy.script.GroovyScriptRunner;

/**
 * Class providing utilities for executing groovy template scripts
 * 
 * @author ejedmar
 *
 */
public class QueryBuilder {

    private static final String GROOVY_SCRIPT_RUNNER_CLASS = "com.ericsson.eniq.events.server.groovy.script.GroovyScriptRunnerImpl";
    /** The Constant EMPTY_PARAMETER_MAP. */
    private static final Map<String, ?> EMPTY_PARAMETER_MAP = new HashMap<String, Object>();
    private static final String VM_MACRO = "VM_query_macros.vm";

    /**
     * Gets the query from template.
     *
     * @param templateFile the template file
     * @return the query from template
     */
    public String getQueryFromTemplate(final String templateFile) {
        return getQueryFromTemplate(templateFile, EMPTY_PARAMETER_MAP);
    }

    /**
     * Gets the query from template.
     *
     * @param templateFile the template file
     * @param parameters the parameters
     * @return the query from template
     * @throws ResourceInitializationException the resource initialization exception
     */
    public String getQueryFromTemplate(final String templateFile, final Map<String, ?> parameters) {
        try {
            final GroovyScriptRunner runner = buildGroovyScriptRunner();
            final String velocityClass = calculateClassName(templateFile);
            final String macroClass = calculateClassName(VM_MACRO);
            return runner.run(macroClass, velocityClass, buildVelocityContext(parameters));
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GroovyScriptRunner buildGroovyScriptRunner() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        final GroovyClassLoader gcl = new GroovyClassLoader();
        final Class<?> clazzRunner = gcl.loadClass(GROOVY_SCRIPT_RUNNER_CLASS);
        final GroovyScriptRunner runner = (GroovyScriptRunner) clazzRunner.newInstance();
        return runner;
    }

    private Map<String, Object> buildVelocityContext(final Map<String, ?> parameters) {
        final Map<String, Object> context = new HashMap<String, Object>();
        if (parameters != null && parameters.size() > 0) {
            final Set<String> keys = parameters.keySet();
            for (final String key : keys) {
                context.put("$"+key, parameters.get(key));
            }
        }
        context.put("$ApplicationConstants", ApplicationConstants.getInstance());
        context.put("$ApplicationMethods", ApplicationConstants.getInstance());
        return context;
    }

    private String calculateClassName(final String templateFile) {
        return StringUtils.replace(FilenameUtils.removeExtension(templateFile), "/", ".");
    }

}
