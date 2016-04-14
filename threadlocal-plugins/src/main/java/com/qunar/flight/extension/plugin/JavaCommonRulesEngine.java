package com.qunar.flight.extension.plugin;

import com.qunar.flight.extension.plugin.batch.Java;
import org.sonar.squidbridge.commonrules.api.CommonRulesEngine;
import org.sonar.squidbridge.commonrules.api.CommonRulesRepository;

public class JavaCommonRulesEngine extends CommonRulesEngine {
    public JavaCommonRulesEngine() {
        super(Java.KEY);
    }

    @Override
    protected void doEnableRules(CommonRulesRepository repository) {
        repository.enableDuplicatedBlocksRule().enableSkippedUnitTestsRule().enableFailedUnitTestsRule()
                .enableInsufficientBranchCoverageRule(null).enableInsufficientCommentDensityRule(null)
                .enableInsufficientLineCoverageRule(null);
    }
}
