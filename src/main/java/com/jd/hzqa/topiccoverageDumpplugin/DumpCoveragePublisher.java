package com.jd.hzqa.topiccoverageDumpplugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class DumpCoveragePublisher extends Notifier implements MatrixAggregatable {
    private static final Logger LOGGER = Logger.getLogger(DumpCoveragePublisher.class.getName());

    public List<GroovyScriptPath> classpath;
    public boolean disabled = false;

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {

        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        return true;
    }

    private boolean _perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener,
            boolean forPreBuild) {
        if (disabled) {
            listener.getLogger().println("Extended Email Publisher is currently disabled in project settings");
            return true;
        }

        //执行 dump 操作
        LOGGER.info("执行 dumpCoverage success!");
        //
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

    public MatrixAggregator createAggregator(MatrixBuild matrixbuild, Launcher launcher, BuildListener buildlistener) {

        return new MatrixAggregator(matrixbuild, launcher, buildlistener) {
            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINER, "end build of " + this.build.getDisplayName());

                // Will be run by parent so we check if needed to be executed by parent
                if (getMatrixTriggerMode().forParent) {
                    return DumpCoveragePublisher.this._perform(this.build, this.launcher, this.listener, false);
                }
                return true;
            }

            @Override
            public boolean startBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINER, "end build of " + this.build.getDisplayName());
                // Will be run by parent so we check if needed to be executed by parent
                if (getMatrixTriggerMode().forParent) {
                    return DumpCoveragePublisher.this._perform(this.build, this.launcher, this.listener, true);
                }
                return true;
            }
        };
    }
}
}
