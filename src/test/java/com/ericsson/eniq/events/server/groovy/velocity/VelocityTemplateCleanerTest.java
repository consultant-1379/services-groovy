package com.ericsson.eniq.events.server.groovy.velocity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VelocityTemplateCleanerTest {
    
    private static final String TARGET_DIR = "target";
    
    private VelocityTemplateCleaner velocityTemplateCleaner;
    
    @Before
    public void setUp() {
        velocityTemplateCleaner = new VelocityTemplateCleaner.Builder()
        .targetDir(new File(TARGET_DIR))
        .srcDir("src/test/resources/com/ericsson/eniq/events/server/groovy/velocity") 
        .build();
        FileUtils.deleteQuietly(new File(TARGET_DIR + "/templates"));
    }
    
    @Test
    public void testDoClean() throws IOException {
        velocityTemplateCleaner.doClean();
        final File expectedCleanedFile = FileUtils.toFile(this.getClass().getResource("expected/velocity.groovy"));
        final File generatedFile = new File(TARGET_DIR + "/templates/velocity.groovy");
        Assert.assertThat(generatedFile.exists(), Matchers.is(true));
        Assert.assertThat(FileUtils.contentEquals(expectedCleanedFile, generatedFile), Matchers.is(true));
    }

}
