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
 * Created by ping.miao on 2016/4/7.
 */
@Rule(key = "ThreadLocalRemove", name = "The remove method should be used after threadLocal used", description = "This rule is be used to avoid threadLocal not being removed", priority = Priority.CRITICAL, tags = { "threadlocal-bug" })
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("5min")
public class ThreadLocalRemoveRule extends ThreadLocalRemoveVisitor {
    protected class AllFileEndProcessor implements JavaFileEndProcessor {
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

    public ThreadLocalRemoveRule() {
        JavaAllFileEndProcessorManager.addProcessor(new AllFileEndProcessor());
    }
}
