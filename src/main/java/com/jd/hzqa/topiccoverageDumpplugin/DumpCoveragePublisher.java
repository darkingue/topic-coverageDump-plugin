package com.jd.hzqa.topiccoverageDumpplugin;

import groovy.lang.GroovyClassLoader;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.util.*;

public class DumpCoveragePublisher extends Notifier implements MatrixAggregatable {

    public List<GroovyScriptPath> classpath;

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {

        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return null;
    }

    /**
     * Expand the plugin class loader with URL taken from the project descriptor and the global configuration.
     *
     * @param cl the original plugin classloader
     * @param cc
     * @return the new expanded classloader
     */
    private ClassLoader expandClassLoader(ClassLoader cl, CompilerConfiguration cc) {
        if ((classpath != null) && classpath.size() > 0) {
            cl = new GroovyClassLoader(cl, cc);
            for (GroovyScriptPath path : classpath) {
                ((GroovyClassLoader) cl).addURL(path.asURL());
            }
        }
        List<GroovyScriptPath> globalClasspath = getDescriptor().getDefaultClasspath();
        if ((globalClasspath != null) && (globalClasspath.size() > 0)) {
            if (!(cl instanceof GroovyClassLoader)) {
                cl = new GroovyClassLoader(cl, cc);
            }
            for (GroovyScriptPath path : globalClasspath) {
                ((GroovyClassLoader) cl).addURL(path.asURL());
            }
        }
        return cl;
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

    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        return null;
    }
}
