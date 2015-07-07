package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.model.*;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by qqs on 15/7/3.
 */
public class CoverageSnapshotActionFactory extends TransientProjectActionFactory {

    @Override public Collection<? extends Action> createFor(AbstractProject abstractProject) {

        return Collections.singletonList(new CoverageSnapshotAction(abstractProject));

    }
}
