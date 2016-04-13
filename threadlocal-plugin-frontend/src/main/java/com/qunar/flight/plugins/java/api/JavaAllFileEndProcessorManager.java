package com.qunar.flight.plugins.java.api;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by ping.miao on 2016/4/8.
 */
public class JavaAllFileEndProcessorManager {
    private static List<JavaFileEndProcessor> processors = Lists.newArrayList();

    public static void addProcessor(JavaFileEndProcessor processor) {
        processors.add(processor);
    }

    public static void success() {
        if (CollectionUtils.isNotEmpty(processors)) {
            for (JavaFileEndProcessor processor : processors) {
                processor.process();
            }
        }
    }
}
