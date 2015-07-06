package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by qqs on 15/7/3.
 */
public class CoverageSnapshot extends TransientProjectActionFactory {

    private final String name;

    @DataBoundConstructor
    public CoverageSnapshot(String name) {
        this.name = name;
    }

    @Override public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        return null;

    }
}
