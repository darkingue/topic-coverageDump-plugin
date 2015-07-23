package com.jd.hzqa.topiccoverageDumpplugin;

import com.wangyin.qa.tools.jacoco.JacocoUtil;
import hudson.model.Job;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by qqs on 15/7/15.
 */
public class DumpCoverageExecutor {

    public static boolean dumpJaCoCoReport(String jobFile, String agentIP, int agentPort) {

        boolean dumpSuccess = false;
        if (JacocoUtil.dumpIsOk(jobFile, agentIP, agentPort)) {
            dumpSuccess = true;
        }
        return dumpSuccess;

    }

}