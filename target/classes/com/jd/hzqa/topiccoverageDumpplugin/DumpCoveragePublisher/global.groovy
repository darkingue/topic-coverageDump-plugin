// Namespaces
f = namespace("/lib/form")
d = namespace("jelly:define")
j = namespace("jelly:core")
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
t = namespace("/lib/hudson")

def configured = instance != null

f.section(title: _("Extended DumpCoverageConfig")) {
    f.optionalBlock(help: "/plugin/topic-coverageDump-plugin/help/globalConfig/requireAdmin.html", checked: descriptor
            .isAdminRequiredForTemplateTesting(), name: "ext_mailer_require_admin_for_template_testing", title: _("Require Administrator for Template Testing"))
}
f.entry(title: _("Project Agent Port"), help: "/plugin/topic-coverageDump-plugin/help/projectConfig/globalRecipientList.html") {
    f.textbox(name: "project_agentPort", value: configured ? instance.agentPort : "1001")
}
