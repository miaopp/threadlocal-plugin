package com.qunar.flight;

import com.google.common.collect.ImmutableList;
import com.qunar.flight.plugins.java.api.JavaCheck;
import com.qunar.flight.rules.CycleBetweenPackagesCheck;
import com.qunar.flight.rules.ThreadLocalRemoveRule;

import java.util.List;

public class RulesList {
    public static final String REPOSITORY_KEY = "myrepo";

    private RulesList() {
    }

    public static List<Class> getChecks() {
        return ImmutableList.<Class> builder().addAll(getJavaChecks()).addAll(getJavaTestChecks()).build();
    }

    public static List<Class<? extends JavaCheck>> getJavaChecks() {
        return ImmutableList.<Class<? extends JavaCheck>> builder().add(ThreadLocalRemoveRule.class)
                .add(CycleBetweenPackagesCheck.class).build();
    }

    public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
        return ImmutableList.<Class<? extends JavaCheck>> builder().build();
    }
}
