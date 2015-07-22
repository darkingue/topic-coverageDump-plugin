package com.jd.hzqa.topiccoverageDumpplugin;

import com.jd.hzqa.topiccoverageDumpplugin.utils.Search;
import hudson.FilePath;
import hudson.Plugin;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.Secret;
import hudson.util.StreamTaskListener;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kohsuke.stapler.*;
import org.kohsuke.stapler.bind.BoundObjectTable;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.lang.Klass;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;

/**
 * Created by qqs on 15/7/9.
 */
public class DumpCoverageReportAction implements Action {
    private final static Logger LOG = Logger.getLogger(DumpCoverageReportAction.class.getName());

    private final AbstractProject<?, ?> project;

    String agentport = "xxxxx";

    public DumpCoverageReportAction(AbstractProject<?, ?> project) {
        this.project = project;
        System.out.println("###project.getCustomWorkspace()" + project.getCustomWorkspace());

    }

    public String getIconFileName() {
        // returning null allows us to have our own action.jelly
        return null;
    }

    public String getDisplayName() {
        return DumpConfig.getDisplayName();
    }

    public String getUrlName() {
        return DumpConfig.getUrlName();
    }

    //    格式化 exception
    private String renderError(Exception ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h3>An error occured trying to render the template:</h3><br/>");
        builder.append("<span style=\"color:red; font-weight:bold\">");
        builder.append(ex.toString().replace("\n", "<br/>"));
        builder.append("</span>");
        return builder.toString();
    }

    //    检查覆盖率文件是否存在
    public FormValidation doJaCoCoReportCheck(@QueryParameter final String value) {
        // first check in the default resources area...
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("com/jd/hzqa/topiccoverageDumpplugin/jacoco/" + value);
        if (inputStream == null) {
            final File jacocoReportFolder = new File(Jenkins.getInstance().getRootDir(), "jacoco-report");
            final File jacocoReportFile = new File(jacocoReportFolder, value);
            if (!jacocoReportFile.exists()) {
                return FormValidation.error("The file '" + value + "' does not exist");
            }
        }

        return FormValidation.ok();
    }

    private boolean telentPortIsok(String ip, int port) {

        boolean isok = false;
        TelnetClient telnet;
        telnet = new TelnetClient();
        try {
            telnet.connect(ip, port);
            isok = true;
        } catch (IOException e) {
            e.printStackTrace();
            isok = false;
        } finally {
            try {
                telnet.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isok;
    }

    @JavaScriptMethod
    public String loadBuildJobSpace(String buildId) {
        return project.getUrl();
    }

    @JavaScriptMethod
    public String[] dumpReport(String svn_Src_Dir, String ip, int port, String buildId) {

        String[] result = new String[4];
        result[0] = StringUtils.EMPTY;
        result[1] = StringUtils.EMPTY;
        result[2] = StringUtils.EMPTY;
        result[3] = StringUtils.EMPTY;

        try {

            //检查 agent 可用性
            if (!telentPortIsok(ip, port)) {
                Date time = new Date();
                String agentCheck = time + "\n" + ip + ":" + port + " is not reachable!";
                System.out.println(agentCheck);
                result[1] = agentCheck;
            } else {
                //Plugin plugin = Jenkins.getInstance().getPlugin("topic-coverageDump-plugin");
                AbstractBuild<?, ?> build = project.getBuild(buildId);
                System.out.println("!!!!!build.getDescription() =" + build.getDescription());
                System.out.println("!!!!!build.getDisplayName() =" + build.getDisplayName());
                System.out.println("!!!!!build.getFullDisplayName() =" + build.getFullDisplayName());
                System.out.println("!!!!!build.getUpUrl() =" + build.getUpUrl());
                System.out.println("!!!!!build.getUrl() =" + build.getUrl());
                System.out.println("!!!!!build.getBuildStatusUrl() =" + build.getBuildStatusUrl());
                System.out.println("!!!!!build.getSearchUrl() =" + build.getSearchUrl());
                System.out.println("!!!!!project.getLastSuccessfulBuild() =" + project.getLastSuccessfulBuild());

                //
                if (!build.getBuildVariables().isEmpty()) {
                    System.out.println("build.getBuildVariables() " + build.getBuildVariables());

                    for (Map.Entry<String, String> entry : build.getBuildVariables().entrySet()) {
                        System.out.println("########### " + entry.getKey() + "--->" + entry.getValue());
                    }
                } else {
                    System.out.println("build.getBuildVariables()  is null!!!!!!!!");
                }
                //
                for (int i = 0; i < build.getEnvironments().size(); i++) {
                    System.out.println("!!!!!build.getEnvironments().size() " + build.getEnvironments().get(i));
                }
                System.out.println("!!!!!build.getModuleRoot().getName =" + build.getModuleRoot().getName());

                for (FilePath module : build.getModuleRoots()) {
                    System.out.println("!!!!!build.getModuleRoots() =" + module);
                }

                //可以通过 config 读出当前job属性
                System.out.println("########### project.getConfigFile() " + project.getConfigFile());
                System.out.println(
                        "########### project.getConfigFile() " + project.getConfigFile().getXStream());
                File workspace = new File(build.getWorkspace().getRemote());
                //检查当前 job的 workspace是否包含 target 目录,如果包含,则证明该 job 没有 module
                if (Search.searchDir(workspace, "target") != null) {
                    //                   直接传递workspace 目录给覆盖率dump 用
                    result[3] = "./target";
                    if (!DumpCoverageExecutor
                            .dumpJaCoCoReport(String.valueOf(workspace), ip, port)) {
                        result[2] = "覆盖率报告生成失败!";
                    }

                } else {
                    //                    检查 workspace 下面是否包含传入的 module 目录
                    if (Search.searchDir(workspace, svn_Src_Dir) != null) {
                        result[3] = "./" + svn_Src_Dir + "/target";
                        //传递 module 目录给覆盖率dump 用
                        if (!DumpCoverageExecutor
                                .dumpJaCoCoReport(String.valueOf(Search.searchDir(workspace, svn_Src_Dir)), ip, port)) {
                            result[2] = "覆盖率报告生成失败!";
                        }
                    }
                }
            }

        } catch (Exception ex) {
            result[0] = renderError(ex);
        }
        return result;
    }

    private String nullify(String v) {
        if (v != null && v.length() == 0) {
            v = null;
        }
        return v;
    }

    public AbstractProject<?, ?> getProject() {
        return project;
    }
}
