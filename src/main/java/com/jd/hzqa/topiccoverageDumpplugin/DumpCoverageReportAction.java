package com.jd.hzqa.topiccoverageDumpplugin;

import com.jd.hzqa.topiccoverageDumpplugin.utils.CopyDirectory;
import com.jd.hzqa.topiccoverageDumpplugin.utils.CopyToSlaveUtils;
import com.jd.hzqa.topiccoverageDumpplugin.utils.Search;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by qqs on 15/7/9.
 */
public class DumpCoverageReportAction implements Action {
    private final static Logger LOGGER = java.util.logging.Logger
            .getLogger(DumpCoverageReportAction.class.getName());
    private final AbstractProject<?, ?> project;

    public DumpCoverageReportAction(AbstractProject<?, ?> project) {
        this.project = project;
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
    public String getDefaultCovReportUrl(String buildId) {
        AbstractBuild<?, ?> build = project.getBuild(buildId);
        String buidpath = build.getLogFile().toPath().getParent() + "/archive/dumpCov/";
        File file = new File(buidpath);
        if (file.exists()) {
            return build.getUrl() + "artifact/dumpCov/index.html";
        } else {
            return "null";

        }
    }

    @JavaScriptMethod
    public String[] dumpReport(String svn_Src_Dir, String ip, String port, String buildId) {

        String[] result = new String[4];
        result[0] = StringUtils.EMPTY;
        result[1] = StringUtils.EMPTY;
        result[2] = StringUtils.EMPTY;
        result[3] = StringUtils.EMPTY;

        try {
            int realPort;

            if (port.isEmpty()) {
                realPort = DumpSettingsPerBuilder.descriptor().getGrobalAgentPort();
            } else {
                realPort = Integer.parseInt(port);
            }
            //检查是否存在构建成功的 build
            if ((buildId.equals("")) ? true : project.getBuild(buildId).getBuildStatusUrl()
                    .equals("red.png")) {
                result[2] = "请选择一个状态为成功的build!";
            }
            //检查 agent 可用性
            else if (!telentPortIsok(ip, realPort)) {
                Date time = new Date();
                String agentCheck = time + "\n" + "请检查 " + ip + ":" + realPort + " 是否为有效的agent端口!";
                LOGGER.log(Level.SEVERE, agentCheck);
                result[1] = agentCheck;
            } else {
                AbstractBuild<?, ?> build = project.getBuild(buildId);
                File workspace;
                if (build.getBuiltOn() == Jenkins.getInstance()) {
                    LOGGER.log(Level.INFO, "构建节点为 master，workspace  " + build.getWorkspace().getRemote());
                    workspace = new File(build.getWorkspace().getRemote());
                } else {
                    LOGGER.log(Level.INFO, " 构建节点为 slave");
                    FilePath projectWorkspaceOnMaster = copyToMaster(build, "**/*", "**/*.jar,**/*.zip,**/*.tar");
                    workspace = new File(projectWorkspaceOnMaster.getRemote());
                }

                String buidpath = build.getLogFile().toPath().getParent() + "/archive/dumpCov";
                result[3] = build.getUrl() + "artifact/dumpCov/index.html";

                if (svn_Src_Dir.isEmpty()) {
                    //检查当前 job的 workspace是否包含 target 目录,如果包含,则证明该 job 没有 module
                    if (Search.searchDir(workspace, "target") != null) {
                        //                   直接传递workspace 目录给覆盖率dump 用
                        if (!DumpCoverageExecutor
                                .dumpJaCoCoReport(String.valueOf(workspace), ip, realPort)) {
                            result[2] = "覆盖率报告生成失败! 详细请查看 console 日志";
                        } else {
                            LOGGER.log(Level.INFO, "转存覆盖率结果到 build 目录");
                            String reportPath = workspace.toPath() + "/target/coveragereport";

                            File src = new File(reportPath);
                            File dest = new File(buidpath);
                            CopyDirectory.copyFolder(src, dest);
                        }
                    } else {
                        result[2] = "覆盖率报告生成失败! 请确认需要dump覆盖率的工程路径是否是一个module,如果是请填入module目录名";
                    }
                } else {
                    //检查 workspace 下面是否包含传入的 module 目录
                    if (Search.searchDir(workspace, svn_Src_Dir) != null) {
                        if (!DumpCoverageExecutor
                                .dumpJaCoCoReport(String.valueOf(Search.searchDir(workspace, svn_Src_Dir)), ip,
                                        realPort)) {
                            result[2] = "覆盖率报告生成失败! 详细请查看 console 日志";
                        } else {
                            LOGGER.log(Level.INFO, "model模式，转存覆盖率结果到 build 目录");
                            String reportPath = workspace.toPath() + "/" + svn_Src_Dir + "/target/coveragereport";
                            File src = new File(reportPath);
                            File dest = new File(buidpath);
                            CopyDirectory.copyFolder(src, dest);
                        }
                    } else {
                        result[2] = "当前project不存在路径名为 " + project.getName() + "/" + svn_Src_Dir + "/ 的module !";

                    }
                }
            }

        } catch (Exception ex) {
            result[0] = renderError(ex);
            ex.printStackTrace();
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

    /**
     * 拷贝 slave node workspacce下面的指定内容到 master workspace 目录
     */
    public FilePath copyToMaster(AbstractBuild build, String includes, String excludes)
            throws IOException, InterruptedException {
        FilePath destinationFilePath = CopyToSlaveUtils.getProjectWorkspaceOnMaster(build);
        FilePath projectWorkspaceOnSlave = build.getWorkspace();
        LOGGER.log(Level.INFO, "[copy-to-master] Copying all from " + projectWorkspaceOnSlave.toURI() + " on " +
                build.getBuiltOn().getNodeName() + " to " + destinationFilePath.toURI() + " on the "
                + "master");

        projectWorkspaceOnSlave.copyRecursiveTo(includes, excludes, destinationFilePath);

        return destinationFilePath;

    }
}
