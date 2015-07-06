package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Sample {@link Builder}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder, {@link DescriptorImpl#newInstance(StaplerRequest)} is
 * invoked and a new {@link HelloWorldBuilder} is created. The created instance is persisted to the project
 * configuration XML by using XStream, so this allows you to use instance fields (like {@link #name}) to remember the
 * configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends Builder {

    private final String name;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        // This also shows how you can consult the global configuration of the builder
        if (getDescriptor().getUseFrench())
            listener.getLogger().println("Bonjour, " + name + "!");
        else
            listener.getLogger().println("Hello, " + name + "!");

        /* 这段代码是获得编译时的一些信息，然后输出到一个文本文件中。其中AbstractBuild类包含了编译的大部分的信息，可以查看API获得更详细的信息 http://javadoc.jenkins-ci.org/ Hudson.model.AbstractBuild。
        此外有一个内部静态类DescriptorImpl，该类通过@Extension声明告诉jenkins，告诉系统该内部类是作为BuildStepDescriptor的扩展出现。有以下几个方法。
        isApplicable方法，是否对所有项目类型可用。
        其他的方法和配置文件有关，在下面介绍配置文件时在详细说明。*/

        int number = build.getNumber();
        String version = build.getHudsonVersion();

        Calendar startedTime = build.getTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String started = simpleDateFormat.format(startedTime.getTime());
        String durationMillis = String.valueOf(build.getDuration());
        String log = null;
        try {
            log = build.getLog();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = "/Users/qqs/GitHub/topic-coverageDump-plugin/BuildLog" + number + ".txt";
        String content;
        content = version + "\n\t"
                + name + "\n\t"
                + started + "\n\t"
                + durationMillis + "\n\t"
                + log;
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
      获得job的workspace，然后根据传入的name，获取目录下面的文件名，最后写入到一个文本文件中。
*/

        //        FilePath workspace = build.getWorkspace();
        //        String filePath = workspace.toString() + "\\" + path;
        //
        //        File files = new File(filePath);
        //        File[] fileList = files.listFiles();
        //
        //        String strFile = "";
        //        for (int i = 0; i < fileList.length; i++) {
        //            strFile += fileList[i].getName() + "   \n";
        //        }
        //
        //        String fileName = "/Users/qqs/GitHub/topic-coverageDump-plugin/BuildLog" + number + ".txt";
        //        try {
        //            FileWriter writer = new FileWriter(fileName, true);
        //            writer.write(strFile);
        //            writer.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. Used as a singleton. The class is marked as public so that it can be
     * accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt> for the actual HTML fragment
     * for the configuration screen.
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
         * Note that returning {@link FormValidation#error(String)} does not prevent the form from being saved. It just
         * means that a message will be displayed to the user.
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
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
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         * <p/>
         * The method name is bit awkward because global.jelly calls this method to determine the initial state of the
         * checkbox by the naming convention.
         */
        public boolean getUseFrench() {
            return useFrench;
        }
    }
}

