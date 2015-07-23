package com.jd.hzqa.topiccoverageDumpplugin.DumpSettingsPerBuilder
// Namespaces
f = namespace("/lib/form")
d = namespace("jelly:define")
j = namespace("jelly:core")
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
t = namespace("/lib/hudson")

f.section(title: _("DumpCoverageConfig")) {
    f.entry(title: _("globalAgentPort"), field: _("globalAgentPort"), description: _("please set grobalAgentPort!")) {
        f.textbox(name: "globalAgentPort", value: descriptor.grobalAgentPort)
    }
}


