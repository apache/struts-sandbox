 package org.apache.struts2.gxp.template;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.google.gxp.com.google.common.collect.ImmutableSet;
import com.google.gxp.com.google.common.collect.ImmutableSortedSet;
import com.google.gxp.com.google.common.collect.Lists;
import com.google.gxp.com.google.common.collect.Sets;
import com.google.gxp.compiler.Configuration;
import com.google.gxp.compiler.Phase;
import com.google.gxp.compiler.alerts.AlertPolicy;
import com.google.gxp.compiler.base.OutputLanguage;
import com.google.gxp.compiler.codegen.CodeGeneratorFactory;
import com.google.gxp.compiler.codegen.DefaultCodeGeneratorFactory;
import com.google.gxp.compiler.fs.FileRef;
import com.google.gxp.compiler.fs.FileSystem;
import com.google.gxp.compiler.fs.InMemoryFileSystem;
import com.google.gxp.compiler.fs.ResourceFileSystem;
import com.google.gxp.compiler.fs.SourcePathFileSystem;
import com.google.gxp.compiler.parser.FileSystemEntityResolver;
import com.google.gxp.compiler.parser.SourceEntityResolver;

public class GxpcConfiguration implements Configuration {
    private final FileSystem fs = new ResourceFileSystem();
    private SourcePathFileSystem sourcePathFs;
    private Set<FileRef> sourceFiles;
    private Set<FileRef> schemaFiles = Sets.newHashSet();
    private List<FileRef> sourcePaths;
    private FileRef outputDir;
    private AlertPolicy alertPolicy;
    private ImmutableSortedSet<Phase> dotPhases;

    private String srcpaths;
    private String destdir;
    private String target;
    private boolean dynamic = false;

    private InMemoryFileSystem memoryFileSystem = new InMemoryFileSystem();

    private GxpcConfiguration() {
    }

    public void configure(List<String> files) {
        List<FileRef> underlyingInputFiles = Lists.newArrayList();
        for (String file : files) {
            underlyingInputFiles.add(fs.parseFilename(file));
        }

        if (destdir == null) {
            // log("Attribute 'destdir' was not set, the current working
            // directory will be used.",
            // Project.MSG_WARN);
            destdir = System.getProperty("user.dir");
        }
        outputDir = dynamic ? memoryFileSystem.getRoot() : fs.parseFilename(destdir);

        sourcePaths = Lists.newArrayList();
        sourcePaths.addAll(fs.parseFilenameList(srcpaths));

        sourcePathFs = new SourcePathFileSystem(fs, sourcePaths, underlyingInputFiles, outputDir);
        dotPhases = computeDotPhases();

        sourceFiles = ImmutableSet.copyOf(sourcePathFs.getSourceFileRefs());
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Setters
    // //////////////////////////////////////////////////////////////////////////////

    public void setSrcpaths(String srcpaths) {
        this.srcpaths = srcpaths;
    }

    public void setDestdir(String destdir) {
        this.destdir = destdir;
    }

    public void setSchemas(String schemas) {
        for (String schema : schemas.split(",")) {
            schemaFiles.add(fs.parseFilename(schema));
        }
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Getters (Configuration implementation)
    // //////////////////////////////////////////////////////////////////////////////

    public Set<FileRef> getSourceFiles() {
        return sourceFiles;
    }

    public Set<FileRef> getSchemaFiles() {
        return schemaFiles;
    }

    public Set<OutputLanguage> getOutputLanguages() {
        Set<OutputLanguage> result = EnumSet.noneOf(OutputLanguage.class);
        result.add(OutputLanguage.JAVA);
        return Collections.unmodifiableSet(result);
    }

    public CodeGeneratorFactory getCodeGeneratorFactory() {
        DefaultCodeGeneratorFactory result = new DefaultCodeGeneratorFactory();
        result.setRuntimeMessageSource(target);
        result.setDynamicModeEnabled(dynamic);
        result.setSourceFiles(getSourceFiles());
        result.setSchemaFiles(getSchemaFiles());
        result.setSourcePaths(sourcePaths);
        return result;
    }

    public Set<FileRef> getAllowedOutputFileRefs() {
        Set<FileRef> result = Sets.newHashSet();
        return Collections.unmodifiableSet(result);
    }

    public FileRef getDependencyFile() {
        return null;
    }

    public FileRef getPropertiesFile() {
        return (target != null) ? outputDir.join("/" + target.replace(".", "/") + "_en.properties") : null;
    }

    public boolean isVerboseEnabled() {
        return false;
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public AlertPolicy getAlertPolicy() {
        return alertPolicy;
    }

    public SortedSet<Phase> getDotPhases() {
        return dotPhases;
    }

    public SourceEntityResolver getEntityResolver() {
        return new FileSystemEntityResolver(sourcePathFs);
    }

    private static ImmutableSortedSet<Phase> computeDotPhases() {
        return ImmutableSortedSet.<Phase> of();
    }

    public void setAlertPolicy(AlertPolicy alertPolicy) {
        this.alertPolicy = alertPolicy;
    }

    public InMemoryFileSystem getMemoryFileSystem() {
        return memoryFileSystem;
    }

    public static class Builder {
        private GxpcConfiguration target = new GxpcConfiguration();

        public Builder setAlertPolicy(AlertPolicy alertPolicy) {
            target.setAlertPolicy(alertPolicy);
            return this;
        }

        public Builder setDestdir(String destdir) {
            target.setDestdir(destdir);
            return this;
        }

        public Builder setDynamic(boolean dynamic) {
            target.setDynamic(dynamic);
            return this;
        }

        public Builder setSchemas(String schemas) {
            target.setSchemas(schemas);
            return this;
        }

        public Builder setSrcpaths(String srcpaths) {
            target.setSrcpaths(srcpaths);
            return this;
        }

        public Builder setTarget(String targetDir) {
            target.setTarget(targetDir);
            return this;
        }

        public GxpcConfiguration build(List<String> files) {
            target.configure(files);
            return target;
        }
    }
}
