package com.jd.hzqa.topiccoverageDumpplugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by qqs on 15/7/9.
 */
public class DumpConfig {

    private final static Properties env;

    static {
        env = new Properties();
        try {
            env.load(DumpConfig.class.getClassLoader().getResourceAsStream("dump.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDisplayName() {
        return env.getProperty("DISPLAYNAME");
    }

    public static String getIconFileName() {
        return env.getProperty("ICONFILENAME");
    }

    public static String getUrlName() {
        return env.getProperty("URLNAME");
    }
}
