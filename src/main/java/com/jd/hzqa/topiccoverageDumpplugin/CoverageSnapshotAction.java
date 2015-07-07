package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * Created by qqs on 15/7/7.
 */
public class CoverageSnapshotAction implements Action {
    private final AbstractProject<?, ?> project;

    public CoverageSnapshotAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    @Override public String getIconFileName() {
        return PluginImpl.ICON_FILE_NAME;
    }

    @Override public String getDisplayName() {
        return PluginImpl.DISPLAY_NAME;
    }

    @Override public String getUrlName() {
        return PluginImpl.URL;
    }

    public AbstractProject<?, ?> getProject() {
        return project;
    }
}
