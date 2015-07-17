package com.jd.hzqa.topiccoverageDumpplugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wangyin.qa.tools.jacoco.JacocoUtil;
import groovy.lang.GroovyClassLoader;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* 构建完成自动执行的扩展
* */
public class DumpCoveragePublisher extends Notifier {
    private static final Logger LOGGER = Logger.getLogger(DumpCoveragePublisher.class.getName());

    public boolean disabled = false;
    public String more = "12345";
    private String recipients;
    private boolean failureOnly;

    @DataBoundConstructor
    public DumpCoveragePublisher(String project_more, boolean project_disabled) {
        super();
        this.more = project_more;
        this.disabled = project_disabled;
    }

    public DumpCoveragePublisher() {

    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        return true;
    }

    /*
    * 构建完成后，Hudson会自动调用当前项目中所有Notifier（实际上应该是Pulisher）子类的的perform方法
    * */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        System.out.println("构建完成! 执行 perform ############# " + more);
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public DumpCoveragePublisherDescriptor getDescriptor() {
        return (DumpCoveragePublisherDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static DumpCoveragePublisherDescriptor descriptor() {
        return Jenkins.getInstance().getDescriptorByType(DumpCoveragePublisherDescriptor.class);
    }

}
