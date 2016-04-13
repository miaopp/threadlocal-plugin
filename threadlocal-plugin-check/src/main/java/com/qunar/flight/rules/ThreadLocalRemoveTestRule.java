package com.qunar.flight.rules;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.qunar.flight.plugins.java.api.JavaAllFileEndProcessorManager;
import com.qunar.flight.plugins.java.api.JavaFileEndProcessor;

/**
 * Created by ping.miao on 2016/4/10.
 */
@Rule(key = "ThreadLocalTestRemove", name = "The remove method should be used after threadLocal used in test", description = "This rule is be used to avoid threadLocal not being removed in test", priority = Priority.CRITICAL, tags = { "threadlocaltest-bug" })
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("5min")
public class ThreadLocalRemoveTestRule extends ThreadLocalRemoveVisitor {
    protected class AllTestFileEndProcessor implements JavaFileEndProcessor {
        @Override
        public void process() {
            if (MapUtils.isNotEmpty(threadLocalVariables)) {
                for (Map.Entry<String, KeyPair> entry : threadLocalVariables.entrySet()) {
                    if (!threadLocalRemoveVariables.containsKey(entry.getKey())) {
                        entry.getValue()
                                .getContext()
                                .reportIssue(entry.getValue().getJavaCheck(), entry.getValue().getTree(),
                                        "threadlocal variable should call remove method");
                    }
                }
            }
            threadLocalVariables.clear();
            threadLocalRemoveVariables.clear();
        }
    }

    public ThreadLocalRemoveTestRule() {
        JavaAllFileEndProcessorManager.addProcessor(new AllTestFileEndProcessor());
    }
}
