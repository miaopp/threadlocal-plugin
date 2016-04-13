package com.qunar.flight.extension.plugin.rules;

import com.qunar.flight.RulesList;
import com.qunar.flight.extension.plugin.batch.Java;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

/**
 * Created by ping.miao on 2016/4/7.
 */
public class JavaExtensionRulesDefinition implements RulesDefinition {
    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(RulesList.REPOSITORY_KEY, Java.KEY);
        repository.setName(RulesList.REPOSITORY_KEY);
        AnnotationBasedRulesDefinition.load(repository, Java.KEY, RulesList.getChecks());
        repository.done();
    }
}
