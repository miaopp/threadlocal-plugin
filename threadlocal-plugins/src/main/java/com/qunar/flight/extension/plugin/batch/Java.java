package com.qunar.flight.extension.plugin.batch;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import java.util.List;

public class Java extends AbstractLanguage {

    /**
     * Java key
     */
    public static final String KEY = "java";
    /**
     * Java name
     */
    public static final String NAME = "Java";

    /**
     * Key of the file suffix parameter
     */
    public static final String FILE_SUFFIXES_KEY = "sonar.java.file.suffixes";

    /**
     * Default Java files knows suffixes
     */
    public static final String DEFAULT_FILE_SUFFIXES = ".java,.jav";

    /**
     * Key of the java version used for sources
     */
    public static final String SOURCE_VERSION = "sonar.java.source";

    /**
     * Settings of the plugin.
     */
    private final Settings settings;

    /**
     * Default constructor
     */
    public Java(Settings settings) {
        super(KEY, NAME);
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.sonar.api.resources.AbstractLanguage#getFileSuffixes()
     */
    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(settings.getStringArray(Java.FILE_SUFFIXES_KEY));
        if (suffixes.length == 0) {
            suffixes = StringUtils.split(DEFAULT_FILE_SUFFIXES, ",");
        }
        return suffixes;
    }

    private static String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = Lists.newArrayList();
        for (String string : stringArray) {
            if (StringUtils.isNotBlank(string.trim())) {
                nonEmptyStrings.add(string.trim());
            }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
    }
}
