// Namespaces
f = namespace("/lib/form")
d = namespace("jelly:define")
j = namespace("jelly:core")
l = namespace("/lib/layout")
st = namespace("jelly:stapler")
t = namespace("/lib/hudson")

def configured = instance != null

f.section(title: _("Extended DumpCoverageConfig")) {
    f.optionalBlock(checked: descriptor.isAdminRequiredForTemplateTesting(), name:
            "require_admin_for_DumpCoverageReport", title: _("Require Administrator for DumpCoverageReport"))
}
