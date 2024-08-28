package com.ericsson.eniq.events.server.groovy.velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;

import com.ericsson.eniq.events.server.logging.ServicesLogger;

/**
 * Basing on velocity template it generates a corresponding Groovy code.
 * @author ejedmar
 * @since 2011
 *
 */
public class GroovySourcecodeGenerator {

    private static final Logger logger = Logger.getLogger(GroovySourcecodeGenerator.class.getName());

    public static final String GROOVY_EXTENSION = ".groovy";

    private File targetDir;

    private List<File> srcDirs;

    private final VelocityEngine ve;

    private GroovySourcecodeGenerator() {
        ve = new VelocityEngine();
        initLogger();
    }

    private Properties setUpVelocityProperties(final File sourceDir) {
        final Properties props = new Properties();
        props.setProperty("resource.loader", "file");
        props.setProperty("file.resource.loader.path", sourceDir.getPath());
        props.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        return props;
    }

    private void initLogger() {
        System.setProperty(ServicesLogger.SERVICES_LOGGER_NAME + ".stdout", "true");
        System.setProperty(ServicesLogger.TRACE_MAX_MESSAGE_LENGTH, "100000");
        ServicesLogger.setLevel(Level.FINE);
        ServicesLogger.initializePropertiesAndLoggers();
    }

    private GroovySourcecodeGenerator(final Builder builder) {
        this();
        this.targetDir = builder.targetDir;
        this.srcDirs = builder.srcDirList;
    }

    /**
     * Generates groovy files.
     */
    public void doGenerate() {
        try {
            for (final File sourceDir : srcDirs) {
                ve.init(setUpVelocityProperties(sourceDir));
                generateSourceFiles(sourceDir);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSourceFiles(final File directory) throws ResourceNotFoundException, ParseErrorException,
            Exception {
        final IOFileFilter suffixFileFilter = new SuffixFileFilter(GROOVY_EXTENSION);
        final Collection<File> listFiles = FileUtils.listFiles(directory, suffixFileFilter,
                DirectoryFileFilter.INSTANCE);
        for (final File velocityFile : listFiles) {
            logger.log(Level.INFO, "Processing " + velocityFile.getPath());
            generateGroovyFile(directory, velocityFile);
        }
    }

    private void generateGroovyFile(final File directory, final File velocityFile) throws ResourceNotFoundException,
            ParseErrorException, Exception {
        final String subpath = StringUtils.removeStart(velocityFile.getPath(), directory.getPath());
        final String normalizedSubpath = FilenameUtils.normalize(subpath, true);

        final Template template = ve.getTemplate(normalizedSubpath);
        final Object ast = template.getData();
        final String packageName = calculatePackageName(normalizedSubpath);
        final File groovyFile = calculateGroovyFilename(normalizedSubpath, velocityFile.getName());
        final StringWriter sw = new StringWriter();
        final AstVisitor sourceCodeGenerator = new AstVisitor(sw);
        ((Node) ast).jjtAccept(sourceCodeGenerator, null);
        final String snippet = sw.toString();
        FileUtils.writeStringToFile(groovyFile, fixSourceCode(snippet, packageName), "UTF-8");
        logger.log(Level.INFO, "Groovy file saved to :" + groovyFile.getPath());
    }

    private String calculatePackageName(final String normalizedSubpath) {
        return StringUtils.replace(FilenameUtils.getPathNoEndSeparator(normalizedSubpath), "/", ".");
    }

    private String fixSourceCode(final String snippet, final String packageName) {
        final StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(packageName)) {
            sb.append("package ").append(packageName).append(";");
        }
        return sb.append(snippet).toString();
    }

    private File calculateGroovyFilename(final String normalizedSubpath, final String velocityFilename)
            throws IOException {
        final File targetPackageDir = new File(targetDir.getPath() + "/"
                + FilenameUtils.getPathNoEndSeparator(normalizedSubpath));
        FileUtils.forceMkdir(targetPackageDir);
        return new File(targetPackageDir, FilenameUtils.removeExtension(velocityFilename) + GROOVY_EXTENSION);
    }

    public static class Builder {

        public static final String DEFAULT_TARGET = "target/generated-sources/groovy";

        private File targetDir = new File(DEFAULT_TARGET);

        private final List<File> srcDirList = new ArrayList<File>();

        public GroovySourcecodeGenerator build() {
            return new GroovySourcecodeGenerator(this);
        }

        public Builder srcDir(final File dir) {
            srcDirList.add(dir);
            return this;
        }

        public Builder srcDir(final String dir) {
            srcDirList.add(new File(dir));
            return this;
        }

        public Builder srcDirList(final String[] srcDirList1) {
            for (final String srcDir : srcDirList1) {
                srcDir(new File(srcDir));
            }
            return this;
        }

        public Builder targetDir(final File targetDir1) {
            this.targetDir = targetDir1;
            return this;
        }
    }
}
