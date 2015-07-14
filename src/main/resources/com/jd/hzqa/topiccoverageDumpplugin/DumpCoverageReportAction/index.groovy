package com.jd.hzqa.topiccoverageDumpplugin.DumpCoverageReportAction
// Namespaces
def l = namespace(lib.LayoutTagLib)
def j = namespace("jelly:core")
def f = namespace(lib.FormTagLib)
def st = namespace("jelly:stapler")
def t = namespace("/lib/hudson")
def d = namespace("jelly:define")

import hudson.Functions

def url = "${rootURL}/plugin/topic-coverageDump-plugin/site/jacoco/index.html";
def requiresAdmin = app.getDescriptor("com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher").adminRequiredForTemplateTesting
def hasPermission = requiresAdmin ? hudson.Functions.hasPermission(app.ADMINISTER) : hudson.Functions.hasPermission(it.project, it.project.CONFIGURE);
l.layout {
    st.include(it: my.project, page: "sidepanel")
    l.main_panel {
        st.bind(var: "templateTester", value: my)
        script """function onSubmit() {
            myiframe=document.createElement("iframe");
            myiframe.name="showframe" ;
            myiframe.width="600";
            myiframe.height="400";
            myiframe.src="${url}";
            document.getElementById('show').innerHTML = "";
            document.getElementById('show').appendChild(myiframe);
            alert("${rootURL}")
            return false;
                   }"""

//        <iframe src="http://localhost:63342/topic-coverageDump-plugin/src/main/webapp/site/jacoco.index
//                .html" name="ifrm" id="ifrm" width="600" height="400"></iframe>

        h1(my.displayName)
        if (hasPermission) {
            h3(_("description"))
            form(action: "", method: "post", name: "templateTest", onSubmit: "return onSubmit();") {
                table {
//                    f.entry(title: _("Jelly/Groovy Template File Name")) {
//                        f.textbox(name: "template_file_name", id: "template_file_name", clazz: "required", checkUrl: "'templateFileCheck?value='+this.value")
//                    }
                    f.entry(title: _("Build To Dump")) {
                        select(name: "template_build", id: "template_build") {
                            my.project.builds.each { build ->
                                f.option(value: build.id, "#${build.number} (${build.result})")
                            }
                        }
                    }
                    f.entry {
                        f.submit(value: _("DUMP NOW!"))
                    }
                }
            }
            div(id: "show") {
                hr()
                h3(_("Template Console Output1"))
            }


        } else {
            // redirect to the root in the case that someone tries to do
            // bad stuff...
            st.redirect(url: "${rootURL}")
        }
    }
}