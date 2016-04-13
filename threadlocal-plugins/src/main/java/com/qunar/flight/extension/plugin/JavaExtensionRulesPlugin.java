package com.qunar.flight.extension.plugin;

import java.util.Arrays;
import java.util.List;

import com.qunar.flight.extension.plugin.batch.Java;
import com.qunar.flight.java.DefaultJavaResourceLocator;
import com.qunar.flight.java.JavaClasspath;
import com.qunar.flight.java.JavaTestClasspath;
import com.qunar.flight.java.SonarComponents;
import com.qunar.flight.java.filters.SuppressWarningsFilter;
import org.sonar.api.SonarPlugin;
import com.qunar.flight.extension.plugin.batch.RulesSensor;
import com.qunar.flight.extension.plugin.rules.JavaExtensionRulesDefinition;

/**
 * Created by ping.miao on 2016/4/7.
 */
public class JavaExtensionRulesPlugin extends SonarPlugin {
    public static final String MYREPO_ANALYSE_ACCESSORS_PROPERTY = "sonar.myrepo.analyse.property.accessors";

    @Override
    public List getExtensions() {
        return Arrays.asList(JavaClasspath.class, JavaTestClasspath.class, SuppressWarningsFilter.class, Java.class,
                RulesSensor.class, DefaultJavaResourceLocator.class, SonarComponents.class,
                JavaExtensionRulesCheckRegistrar.class, JavaExtensionRulesDefinition.class);
    }
}
