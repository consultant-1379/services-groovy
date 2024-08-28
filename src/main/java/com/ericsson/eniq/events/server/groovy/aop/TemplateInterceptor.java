package com.ericsson.eniq.events.server.groovy.aop;

import java.io.IOException;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.ericsson.eniq.events.server.groovy.templates.QueryBuilder;

/**
 * This aspect is intended to intercept a SQL query retrieval method call and provide a Groovy implementation of this method.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
@Aspect
public class TemplateInterceptor {
    
    QueryBuilder queryBuilder = new QueryBuilder();

    /**
     * AspectJ pointcut and advice for intercepting <i>getQueryFromTemplate</i> method call.
     * @param pjp joinpoint reference
     * @param templateFile Velocity template file name
     * @param parameters Velocity context parameters
     * @return SQL query
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Around("execution(* com.ericsson.eniq.events.server.templates.utils.TemplateUtils.getQueryFromTemplate(java.lang.String, java.util.Map+)) && args(templateFile, parameters)")
    public Object queryForTemplate(final ProceedingJoinPoint pjp, final String templateFile, final Map<String, ?> parameters) throws ClassNotFoundException, IOException {
        return queryBuilder.getQueryFromTemplate(templateFile, parameters);
    }
}
