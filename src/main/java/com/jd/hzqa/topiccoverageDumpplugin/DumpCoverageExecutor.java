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

    public static boolean doFileCheck(String fileName) {
        boolean isFileExist = false;
        File file = new File(fileName);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            isFileExist = false;
            System.out.println("//不存在");
            //            file.mkdir();
        } else {
            isFileExist = true;
            System.out.println("//目录存在");
        }

        return isFileExist;
    }

    public static boolean isFileListContain(String base, String fileName) {
        boolean isContain = false;
        File file = new File(base);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {

            System.out.println("//不存在");
            //            file.mkdir();
        } else {
            isContain = true;
            System.out.println("//目录存在");
        }

        return isContain;
    }

}