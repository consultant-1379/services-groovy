package com.ericsson.eniq.events.server.groovy.ant;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import com.ericsson.eniq.events.server.groovy.velocity.VelocityTemplateCleaner;
import com.ericsson.eniq.events.server.groovy.velocity.VelocityTemplateCleaner.Builder;

/**
 * Ant task that performs some preliminary pre-processing of Velocity templates - removing block comments, unifying
 * end of line character. Modified Velocity templates are then copied to the <i>targetDir</i> directory.
 * @author ejedmar
 * @since 2011
 *
 */
public class VelocityTemplateCleanerAntTask extends Task {

    private String targetDir = Builder.DEFAULT_TARGET_DIR;

    private Reference srcRefId;

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() {
        log("Executing velocity cleaner...");
        try {
            final VelocityTemplateCleaner velocityCleaner = buildVelocityTemplateCleaner();
            velocityCleaner.doClean();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private VelocityTemplateCleaner buildVelocityTemplateCleaner() {
        final Path sourceDirectories = (Path) srcRefId.getReferencedObject();
        return new VelocityTemplateCleaner.Builder()
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
