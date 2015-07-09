// Namespaces
f = namespace("/lib/form")
d = namespace("jelly:define")
j = namespace("jelly:core")
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
t = namespace("/lib/hudson")

f.section(title: _("Extended DumpCoverageConfig")) {
    f.optionalBlock(help: "/plugin/email-ext/help/globalConfig/requireAdmin.html", checked: descriptor.isAdminRequiredForTemplateTesting(), name: "ext_mailer_require_admin_for_template_testing", title: _("Require Administrator for Template Testing"))
}