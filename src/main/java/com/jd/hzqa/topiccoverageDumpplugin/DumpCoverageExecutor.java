package com.jd.hzqa.topiccoverageDumpplugin;

import com.wangyin.qa.tools.jacoco.JacocoUtil;
import hudson.model.Job;

/**
 * Created by qqs on 15/7/15.
 */
public class DumpCoverageExecutor {

    String jobFile;
    String agentIP;
    int agentPort;

    private void dumpInt() {

    }

    private void dumpJaCoCoReport(String jobFile, String agentIP, int agentPort) {
        try {
            JacocoUtil.dump(jobFile, agentIP, agentPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}