package com.qunar.flight.extension.plugin;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by ping.miao on 2016/4/8.
 */
public class JavaCommonRulesEnginTest {
    @Test
    public void provide_extensions() {
        JavaCommonRulesEngine engine = new JavaCommonRulesEngine();
        assertThat(engine.provide()).isNotEmpty();
    }
}
