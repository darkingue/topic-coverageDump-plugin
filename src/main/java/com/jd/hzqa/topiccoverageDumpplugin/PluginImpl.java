package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.Plugin;
import hudson.model.Run;

/**
 * Entry point of TestNG Results plugin.
 * <p/>
 * TODO: should move to newer supported way of initializing plugins
 */
public class PluginImpl extends Plugin {

    public static final String DISPLAY_NAME = "Dump Coverage Snapshot";
    public static final String URL = "dumpCoveragereports";
    public static final String ICON_FILE_NAME = "/plugin/topic-coverageDump-plugin/icons/report.png";

    public void start() throws Exception {
        //this is the name with which older build actions are stored with in build.xml files
        //this will be written to the build.xml file when saving DumpCoverage build action
        Run.XSTREAM.alias("coverageSanpshitAction", CoverageSnapshotAction.class);
    }
}
