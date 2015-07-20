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
f.entry(title: _("Project more"), help: "/plugin/topic-coverageDump-plugin/help/projectConfig/globalRecipientList.html") {
    f.textbox(name: "project_more", value: configured ? instance.more : "8888")
}

//使更多菜单在扩展中限制
//f.advanced(title: _("More Settings")) {
//}