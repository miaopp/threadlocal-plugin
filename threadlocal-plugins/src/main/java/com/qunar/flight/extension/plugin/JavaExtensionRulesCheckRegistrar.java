package com.qunar.flight.extension.plugin;

import com.qunar.flight.RulesList;
import com.qunar.flight.plugins.java.api.CheckRegistrar;
import com.qunar.flight.plugins.java.api.JavaCheck;
import com.qunar.flight.rules.ThreadLocalRemoveRule;

import java.util.Arrays;

public class JavaExtensionRulesCheckRegistrar implements CheckRegistrar {
    @Override
    public void register(RegistrarContext registrarContext) {
        registrarContext.registerClassesForRepository(RulesList.REPOSITORY_KEY, Arrays.asList(checkClasses()),
                Arrays.asList(testCheckClasses()));
    }

    /**
     * Lists all the checks provided by the plugin
     */
    public static Class<? extends JavaCheck>[] checkClasses() {
        return new Class[] { ThreadLocalRemoveRule.class };
    }

    /**
     * Lists all the test checks provided by the plugin
     */
    public static Class<? extends JavaCheck>[] testCheckClasses() {
        return new Class[] {};
    }

}
