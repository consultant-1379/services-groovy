package com.ericsson.eniq.events.server.groovy.ant;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import com.ericsson.eniq.events.server.groovy.velocity.GroovySourcecodeGenerator;
import com.ericsson.eniq.events.server.groovy.velocity.GroovySourcecodeGenerator.Builder;

/**
 * Ant task for generating Groovy code from Velocity templates.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class GroovySourcecodeGeneratorAntTask extends Task {

    private String targetDir =  Builder.DEFAULT_TARGET;

    private Reference srcRefId;

    /** 
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() {
        log("Executing groovy source files generation...");
        try {
            final GroovySourcecodeGenerator groovySourcecodeGenerator = buildGenerator();
            groovySourcecodeGenerator.doGenerate();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private GroovySourcecodeGenerator buildGenerator() {
        final Path sourceDirectories = (Path) srcRefId.getReferencedObject();
        return new GroovySourcecodeGenerator.Builder()
                .targetDir(new File(targetDir))
                .srcDirList(sourceDirectories.list())
                .build();
    }

    public void setTargetDir(final String targetDir) {
        this.targetDir = targetDir;
    }

    public void setSrcRefId(final Reference srcRefId) {
        this.srcRefId = srcRefId;
    }
}
