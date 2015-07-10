/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by qqs on 15/7/9.
 */
@Extension
public class DumpCoverageReportActionFactory extends TransientProjectActionFactory {

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {

        return Collections.singletonList(new DumpCoverageReportAction(target));

    }
}
