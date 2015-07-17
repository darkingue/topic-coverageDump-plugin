package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.Extension;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * These settings are global configurations
 */
@Extension
public final class DumpCoveragePublisherDescriptor extends BuildStepDescriptor<Publisher> {
    private final static Logger LOG = Logger.getLogger(DumpCoveragePublisherDescriptor.class.getName());

    /**
     * Jenkins's own URL
     */
    private transient String hudsonUrl;

    private boolean requireAdminForTemplateTesting = false;
    private String agentPort = "";
    private String more = " --";

    //    @DataBoundConstructor
    //    public DumpCoveragePublisherDescriptor(String project_agentPort) {
    //        super();
    //        this.agentPort = project_agentPort;
    //    }

    @Override
    public String getDisplayName() {
        return DumpConfig.getDisplayName();
    }

    public String getAdminAddress() {
        return JenkinsLocationConfiguration.get().getAdminAddress();
    }

    public String getHudsonUrl() {
        return Jenkins.getInstance().getRootUrl();
    }

    public String getAgent_port() {
        return agentPort;
    }

    public String getMore() {
        return more;
    }

    public boolean isAdminRequiredForTemplateTesting() {
        LOG.debug("#############" + requireAdminForTemplateTesting);
        return requireAdminForTemplateTesting;
    }

    @Override public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    public DumpCoveragePublisherDescriptor() {
        super(DumpCoveragePublisher.class);
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData)
            throws FormException {

        //        if (req.hasParameter("ext_mailer_require_admin_for_template_testing")) {
        //            requireAdminForTemplateTesting = nullify(req.getParameter("ext_mailer_list_id")) != null;
        //        } else {
        //            requireAdminForTemplateTesting = false;
        //        }
        more = nullify(req.getParameter("project_more"));
        agentPort = nullify(req.getParameter("project_agentPort"));
        requireAdminForTemplateTesting = req.hasParameter("ext_mailer_require_admin_for_template_testing");

        //        // specify List-ID information
        //        if (req.hasParameter("ext_mailer_use_list_id")) {
        //            listId = nullify(req.getParameter("ext_mailer_list_id"));
        //        } else {
        //            listId = null;
        //        }
        save();
        return super.configure(req, formData);
    }

    private String nullify(String v) {
        if (v != null && v.length() == 0) {
            v = null;
        }
        return v;
    }

    /*
    * 提供校验参数的 url 访问形式
    * */
    public FormValidation doAgentPortCheck(@QueryParameter final String value)
            throws IOException, ServletException {
        return new ParameterCheckUtils().validateFormParameter_1(value);
    }

}
