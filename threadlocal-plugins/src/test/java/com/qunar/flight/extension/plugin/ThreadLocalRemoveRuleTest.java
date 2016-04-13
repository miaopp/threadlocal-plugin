package com.qunar.flight.extension.plugin;

import com.qunar.flight.rules.ThreadLocalRemoveRule;
import org.junit.Test;
import org.sonar.squid.api.CodeVisitor;

/**
 * Created by ping.miao on 2016/4/10.
 */
public class ThreadLocalRemoveRuleTest {

    @Test
    public void run() {
        ThreadLocalRemoveRule threadLocalRemoveRule = new ThreadLocalRemoveRule();
        CodeVisitor codeVisitor = (CodeVisitor) threadLocalRemoveRule;
        System.out.println(codeVisitor);
    }
}
