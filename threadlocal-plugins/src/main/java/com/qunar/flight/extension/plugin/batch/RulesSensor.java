package com.qunar.flight.extension.plugin.batch;

import com.google.common.collect.Lists;
import com.qunar.flight.RulesList;
import com.qunar.flight.extension.plugin.JavaExtensionRulesPlugin;
import com.qunar.flight.extension.plugin.bridges.DesignBridge;
import com.qunar.flight.java.DefaultJavaResourceLocator;
import com.qunar.flight.java.JavaClasspath;
import com.qunar.flight.java.JavaConfiguration;
import com.qunar.flight.java.JavaSquid;
import com.qunar.flight.java.SonarComponents;
import com.qunar.flight.java.model.JavaVersionImpl;
import com.qunar.flight.plugins.java.api.JavaVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.resources.Project;
import org.sonar.java.api.JavaUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

@Phase(name = Phase.Name.PRE)
@DependsUpon(JavaUtils.BARRIER_BEFORE_SQUID)
@DependedUpon(value = JavaUtils.BARRIER_AFTER_SQUID)
public class RulesSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(RulesSensor.class);

    private final JavaClasspath javaClasspath;
    private final SonarComponents sonarComponents;
    private final FileSystem fs;
    private final DefaultJavaResourceLocator javaResourceLocator;
    private final Settings settings;

    public RulesSensor(JavaClasspath javaClasspath, SonarComponents sonarComponents, FileSystem fs,
            DefaultJavaResourceLocator javaResourceLocator, Settings settings) {
        this.javaClasspath = javaClasspath;
        this.sonarComponents = sonarComponents;
        this.fs = fs;
        this.javaResourceLocator = javaResourceLocator;
        this.settings = settings;
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        javaResourceLocator.setSensorContext(context);
        sonarComponents.registerCheckClasses(RulesList.REPOSITORY_KEY, RulesList.getJavaChecks());
        sonarComponents.registerTestCheckClasses(RulesList.REPOSITORY_KEY, RulesList.getJavaTestChecks());
        JavaConfiguration configuration = createConfiguration();
        JavaSquid squid = new JavaSquid(configuration, sonarComponents, javaResourceLocator,
                sonarComponents.checkClasses());
        squid.scan(getSourceFiles(), getBytecodeFiles());
        boolean skipPackageDesignAnalysis = settings.getBoolean(CoreProperties.DESIGN_SKIP_PACKAGE_DESIGN_PROPERTY);
        if (!skipPackageDesignAnalysis && squid.isBytecodeScanned()) {
            DesignBridge designBridge = new DesignBridge(context, squid.getGraph(),
                    javaResourceLocator.getResourceMapping(), sonarComponents.getResourcePerspectives());
            designBridge.saveDesign(project);
        }
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        // This sensor is executed on any type of projects
        return fs.hasFiles(fs.predicates().hasLanguage(Java.KEY));
    }

    private JavaConfiguration createConfiguration() {
        boolean analyzePropertyAccessors = settings
                .getBoolean(JavaExtensionRulesPlugin.MYREPO_ANALYSE_ACCESSORS_PROPERTY);
        Charset charset = fs.encoding();
        JavaConfiguration conf = new JavaConfiguration(charset);
        conf.setSeparateAccessorsFromMethods(analyzePropertyAccessors);
        JavaVersion javaVersion = getJavaVersion();
        LOG.info("Configured Java source version (" + Java.SOURCE_VERSION + "): " + javaVersion);
        conf.setJavaVersion(javaVersion);
        return conf;
    }

    private JavaVersion getJavaVersion() {
        return JavaVersionImpl.fromString(settings.getString(Java.SOURCE_VERSION));
    }

    private Iterable<File> getSourceFiles() {
        return toFile(fs.inputFiles(fs.predicates().and(fs.predicates().hasLanguage(Java.KEY),
                fs.predicates().hasType(InputFile.Type.MAIN))));
    }

    private Iterable<File> getTestFiles() {
        return toFile(fs.inputFiles(fs.predicates().and(fs.predicates().hasLanguage(Java.KEY),
                fs.predicates().hasType(InputFile.Type.TEST))));
    }

    private List<File> getBytecodeFiles() {
        if (settings.getBoolean(CoreProperties.DESIGN_SKIP_DESIGN_PROPERTY)) {
            return Collections.emptyList();
        }
        return javaClasspath.getElements();
    }

    private static Iterable<File> toFile(Iterable<InputFile> inputFiles) {
        List<File> files = Lists.newArrayList();
        for (InputFile inputFile : inputFiles) {
            files.add(inputFile.file());
        }
        return files;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
