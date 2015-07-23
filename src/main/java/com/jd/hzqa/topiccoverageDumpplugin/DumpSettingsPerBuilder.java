package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.ParametersAction;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by qqs on 15/7/20.
 */
public class DumpSettingsPerBuilder extends Builder {

    private final String name;
    private final String port;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public DumpSettingsPerBuilder(String name, String port) {
        this.name = name;
        this.port = port;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    public String getPort() {
        return port;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        // This also shows how you can consult the global configuration of the builder

        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public static DescriptorImpl descriptor() {
        return Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
    }

    /**
     * Descriptor for {@link com.jd.hzqa.topiccoverageDumpplugin.DumpSettingsPerBuilder}. Used as a singleton. The class
     * is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>topic-coverageDump-plugin/src/main/resources/com/jd/hzqa/topiccoverageDumpplugin/DumpSettingsPerBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information, simply store it in a field and call save().
         * <p/>
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;
        private int grobalAgentPort;

        /**
         * In order to load the persisted global configuration, you have to call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link hudson.util.FormValidation#error(String)} does not prevent the form from being
         * saved. It just means that a message will be displayed to the user.
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public FormValidation doCheckPort(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a number");
            if (value.length() != 4)
                return FormValidation.warning("wrong port number");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Say hello world";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            //            grobalAgentPort = formData.getInt("globalAgentPort");
            grobalAgentPort = Integer.parseInt(nullify(req.getParameter("globalAgentPort")));
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        private String nullify(String v) {
            if (v != null && v.length() == 0) {
                v = null;
            }
            return v;
        }

        public int getGrobalAgentPort() {
            return grobalAgentPort;
        }
    }
}
