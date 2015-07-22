package com.jd.hzqa.topiccoverageDumpplugin.utils;

/**
 * Created by qqs on 15/7/22.
 */

import java.io.File;
import java.util.regex.Pattern;

public class Search {
    public static void search(File folder, Pattern regex) {
        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("不能访问" + folder.getAbsolutePath());
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                search(file, regex);
            } else {
                if (regex.matcher(file.getName()).matches()) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }

    public static void search(String path, String regex) {
        search(new File(path), Pattern.compile(regex));
    }

    public static File searchDir(File folder, String fileName) {
        File filePath = null;
        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("不能访问" + folder.getAbsolutePath());
            return null;
        }
        for (File file : files) {
            if (file.isDirectory() & file.getName().equals(fileName)) {
                filePath = file;
            }
        }
        return filePath;

    }

    public static void main(String[] args) {
        File base = new File("/Users/qqs/GitHub/topic-coverageDump-plugin/work/jobs/wycds_web_2.2.0/workspace");
        System.out.println(Search.searchDir(base, "wycds-web"));
    }
}
