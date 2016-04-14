package com.qunar.flight.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.qunar.flight.RulesList;
import com.qunar.flight.java.bytecode.visitor.BytecodeVisitor;

@Rule(key = CycleBetweenPackagesCheck.KEY, name = "Cycles between packages should be removed", priority = Priority.MAJOR, tags = { "design" })
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_CHANGEABILITY)
@SqaleConstantRemediation("1d")
public class CycleBetweenPackagesCheck extends BytecodeVisitor {
    public static final String KEY = "CycleBetweenPackages";
    public static final RuleKey RULE_KEY = RuleKey.of(RulesList.REPOSITORY_KEY, KEY);

    @Override
    public String toString() {
        return KEY + " rule";
    }
}
