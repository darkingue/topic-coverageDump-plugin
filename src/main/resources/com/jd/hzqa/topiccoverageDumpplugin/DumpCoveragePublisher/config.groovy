package com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher
// Namespaces
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
j = namespace("jelly:core")
t = namespace("/lib/hudson")
f = namespace("/lib/form")
d = namespace("jelly:define")


def configured = instance != null

f.entry(title: _("description2"), help: "/plugin/topic-coverageDump-plugin/help/projectConfig/disable.html", description: _("Allows " +
        "the user to disable the publisher, while maintaining the settings")) {
    f.checkbox(name: "project_disabled", checked: instance?.disabled)
}

//使更多菜单在扩展中限制
f.advanced(title: _("More Settings")) {
    f.entry(title: _("Project Agent Port"), help: "/plugin/topic-coverageDump-plugin/help/projectConfig/globalRecipientList.html") {
        f.textbox(name: "agent_port", value: configured ? instance.agentPort : "1001", checkUrl:
                "'${rootURL}/publisher/DumpCoveragePublisher/AgentPortCheck?value='+encodeURIComponent(this.value)")
    }
}