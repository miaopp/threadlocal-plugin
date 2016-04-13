package com.qunar.flight.extension.plugin;

import com.qunar.flight.extension.plugin.batch.Java;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.squidbridge.commonrules.api.CommonRulesDecorator;

/**
 * Created by ping.miao on 2016/4/8.
 */
public class JavaCommonRulesDecorator extends CommonRulesDecorator {
    public JavaCommonRulesDecorator(FileSystem fs, CheckFactory checkFactory, ResourcePerspectives resourcePerspective) {
        super(Java.KEY, fs, checkFactory, resourcePerspective);
    }
}
