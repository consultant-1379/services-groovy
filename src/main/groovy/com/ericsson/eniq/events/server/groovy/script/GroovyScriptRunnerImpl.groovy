package com.ericsson.eniq.events.server.groovy.script

import org.codehaus.groovy.runtime.InvokerHelper
import java.util.Stack
import org.apache.commons.lang.StringUtils

/**
 * Actual implementation of @see com.ericsson.eniq.events.server.groovy.script.GroovyScriptRunner
 */
class GroovyScriptRunnerImpl implements GroovyScriptRunner {

        /**
         * @see com.ericsson.eniq.events.server.groovy.script.GroovyScriptRunner#run
         */
        String run(final String macroClass, final String velocityClass, final Map context) {
                try {
                        final GroovyClassLoader gcl = new GroovyClassLoader()
                        final Class scriptClazz = gcl.loadClass(velocityClass)
                        final Class macroClazz = gcl.loadClass(macroClass)
                        final StringBuilder sb = new StringBuilder()

                        def macroClazzNewInstance = buildMacro(macroClazz, context, sb)
                        final Script dslScript = buildScript(scriptClazz, sb, macroClazzNewInstance)
                        dslScript.run()
                        return sb.toString()
                } catch (Exception e) {
                        throw new RuntimeException(e)
                }
        }

        private buildScript(Class scriptClazz, StringBuilder sb, macroClazzNewInstance) {
                final Script dslScript = InvokerHelper.createScript(scriptClazz, new Binding());
                dslScript.metaClass.macro = macroClazzNewInstance
                dslScript.metaClass.propertyMissing = {String name ->
                        return macro.get(name)
                }
                dslScript.metaClass.methodMissing = {String name, args ->
                        macro.invokeMethod(name, args)
                }
                dslScript.metaClass.sb = sb
                dslScript.metaClass.out = queryBuilder()
                return dslScript
        }

        private buildMacro(Class macroClazz, Map context, StringBuilder sb) {
                def macroClazzNewInstance = macroClazz.newInstance()
                macroClazzNewInstance.metaClass.props = context
                macroClazzNewInstance.metaClass.sb = sb
                final Stack<Integer> velocityCountStack = new Stack<Integer>();
                
                macroClazzNewInstance.metaClass.set = {String name, value ->
                        if (!props.containsKey("\$"+name) || value != null) {
                                props["\$"+name] = value
                        }
                }
                macroClazzNewInstance.metaClass.get = {String name ->
                        return props[name]
                }
                macroClazzNewInstance.metaClass.propertyMissing = {String name ->
                        return props[name]
                }
                macroClazzNewInstance.metaClass.sb = sb
                macroClazzNewInstance.metaClass.out = queryBuilder()
                
                macroClazzNewInstance.metaClass.velocityCountStack = velocityCountStack
                macroClazzNewInstance.metaClass.push = { ->
                        velocityCountStack.push(1)
                }
                    
                macroClazzNewInstance.metaClass.pop = { ->
                        velocityCountStack.pop()
                }
                    
                macroClazzNewInstance.metaClass.peek = { ->
                        return velocityCountStack.peek();
                }
                    
                macroClazzNewInstance.metaClass.increment = { ->
                        final Integer peek = velocityCountStack.pop();
                        velocityCountStack.push(peek+1);
                }
                
                macroClazzNewInstance.metaClass.isArrayEmpty = { arg ->
                    return arg == null || arg == []
                }
                
                macroClazzNewInstance.metaClass.isArrayNotEmpty = { arg ->
                        return arg != null && arg != []
                }
                
                macroClazzNewInstance.metaClass.isEmpty = { arg ->
                        return arg == null || arg == ""
                }
                
                macroClazzNewInstance.metaClass.isNotEmpty = { arg ->
                        return arg != null && arg != ""
                }
                
                return macroClazzNewInstance
        }

        private Closure queryBuilder() {
                return {txt -> sb.append(txt) }
        }
}