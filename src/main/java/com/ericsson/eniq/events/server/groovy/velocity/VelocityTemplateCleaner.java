package com.ericsson.eniq.events.server.groovy.velocity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.IncompleteArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.ericsson.eniq.events.server.groovy.utils.TextSplitter;
import com.ericsson.eniq.events.server.logging.ServicesLogger;

/**
 * Velocity template preprocessor. Removes block comments and unifies end of line characters.
 * 
 * @author ejedmar
 * @since 2011
 *
 */
public class VelocityTemplateCleaner {

    public static final String VELOCITY_EXTENSION = ".vm";

    private static final Logger logger = Logger.getLogger(VelocityTemplateCleaner.class.getName());

    private static final String COMMENT_REPLACEMENT = "";

    private static final String BLOC_COMMENT_PATTERN = "#\\*\\*.*?\\*#";

    private static final String WINDOWS_NEW_LINE_PATTERN = "\r\n";

    private static final String INLINE_COMMENT_PATTERN = "##.*?";

    private static Pattern blockCommentPattern = Pattern.compile(BLOC_COMMENT_PATTERN, Pattern.DOTALL);

    private static Pattern inlineCommentPattern = Pattern.compile(INLINE_COMMENT_PATTERN, Pattern.MULTILINE);

    private static Pattern windowsNewLinePattern = Pattern.compile(WINDOWS_NEW_LINE_PATTERN);

    private final File targetDir;

    private final List<File> srcDirList;

    private VelocityTemplateCleaner(final Builder builder) {
        this.targetDir = builder.targetDir;
        this.srcDirList = builder.srcDirList;
        init();
    }

    protected void init() {
        System.setProperty(ServicesLogger.SERVICES_LOGGER_NAME + ".stdout", "true");
        System.setProperty(ServicesLogger.TRACE_MAX_MESSAGE_LENGTH, "100000");
        ServicesLogger.setLevel(Level.FINE);
        ServicesLogger.initializePropertiesAndLoggers();
    }

    /**
     * Processing execution.
     */
    public void doClean() {
        try {
            for (final File srcDir : srcDirList) {
                if (srcDir.exists() && srcDir.isDirectory()) {
                    cleanFiles(srcDir);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanFiles(final File directory) throws ResourceNotFoundException, ParseErrorException, Exception {
        final IOFileFilter suffixFileFilter = new SuffixFileFilter(VELOCITY_EXTENSION);
        final Collection<File> listFiles = FileUtils.listFiles(directory, suffixFileFilter,
                DirectoryFileFilter.INSTANCE);
        for (final File velocityFile : listFiles) {
            logger.log(Level.INFO, "Processing " + velocityFile.getPath());
            cleanVelocityFile(directory, velocityFile);
        }
    }

    private void cleanVelocityFile(final File directory, final File velocityFile) throws ResourceNotFoundException,
            ParseErrorException, Exception {

        final String subpath = StringUtils.removeStart(velocityFile.getPath(), directory.getPath());
        final String normalizedSubpath = FilenameUtils.normalize(subpath, true);

        final String cleanedVelocityTemplate = cleanVelocityTemplate(FileUtils.readFileToString(velocityFile));
        final File calculatedVelocityFilename = calculateVelocityFilename(normalizedSubpath, velocityFile.getName());
        if (!calculatedVelocityFilename.exists()) {
            FileUtils.writeStringToFile(calculatedVelocityFilename, cleanedVelocityTemplate, "UTF-8");
            logger.log(Level.INFO, "Cleand velocity file saved to :" + calculatedVelocityFilename.getPath());
        } else {
            logger.log(Level.WARNING, "File :" + calculatedVelocityFilename.getPath() + " already exists!");
        }
    }

    private String cleanVelocityTemplate(final String velocityTemplate) {
        final String noBlockComments = removeBlockComments(velocityTemplate);
        final String windowsNewLinesAndNoBlockComments = convertNewLines(noBlockComments);
        return new StringBuilder().append(packageLine()).append(windowsNewLinesAndNoBlockComments).toString();
    }

    private String removeBlockComments(final String velocityTemplate) {
        final Matcher matcher = blockCommentPattern.matcher(velocityTemplate);
        return matcher.replaceAll(COMMENT_REPLACEMENT);
    }

    @SuppressWarnings("unused")
    private String removeInlineComments(final String velocityTemplate) {
        final Matcher matcher = inlineCommentPattern.matcher(velocityTemplate);
        return matcher.replaceAll(COMMENT_REPLACEMENT);
    }

    private String convertNewLines(final String velocityTemplate) {
        final Matcher matcher = windowsNewLinePattern.matcher(velocityTemplate);
        return matcher.replaceAll(TextSplitter.NEW_LINE);
    }

    private String packageLine() {
        return TextSplitter.NEW_LINE;
    }

    private File calculateVelocityFilename(final String normalizedSubpath, final String velocityFilename)
            throws IOException {
        final File targetPackageDir = new File(targetDir.getPath() + "/"
                + FilenameUtils.getPathNoEndSeparator(normalizedSubpath));
        return new File(targetPackageDir, FilenameUtils.removeExtension(velocityFilename)
                + GroovySourcecodeGenerator.GROOVY_EXTENSION);
    }

    public static class Builder {

        public static final String DEFAULT_TARGET_DIR = "target/generated-sources/velocity";

        private File targetDir = new File(DEFAULT_TARGET_DIR);

        private final List<File> srcDirList = new ArrayList<File>();

        public VelocityTemplateCleaner build() {
            if (!srcDirList.isEmpty()) {
                return new VelocityTemplateCleaner(this);
            }
            throw new IncompleteArgumentException("Source directory not specified!");
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
