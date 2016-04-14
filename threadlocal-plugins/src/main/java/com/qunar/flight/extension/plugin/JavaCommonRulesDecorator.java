package com.qunar.flight.extension.plugin;

import com.qunar.flight.extension.plugin.batch.Java;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.squidbridge.commonrules.api.CommonRulesDecorator;

public class JavaCommonRulesDecorator extends CommonRulesDecorator {
    public JavaCommonRulesDecorator(FileSystem fs, CheckFactory checkFactory, ResourcePerspectives resourcePerspective) {
        super(Java.KEY, fs, checkFactory, resourcePerspective);
    }
}
