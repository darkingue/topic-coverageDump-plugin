package com.jd.hzqa.topiccoverageDumpplugin;

import com.jd.hzqa.topiccoverageDumpplugin.utils.JacocoUtil;

/**
 * Created by qqs on 15/7/15.
 */
public class DumpCoverageExecutor {

    public static boolean dumpJaCoCoReport(String jobFile, String agentIP, int agentPort) {

        boolean dumpSuccess = JacocoUtil.dumpIsOk(jobFile, agentIP, agentPort);

        return dumpSuccess;

    }

}