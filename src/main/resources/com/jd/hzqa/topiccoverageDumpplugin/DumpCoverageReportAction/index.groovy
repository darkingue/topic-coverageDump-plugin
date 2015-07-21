package com.jd.hzqa.topiccoverageDumpplugin.DumpCoverageReportAction
// Namespaces
def l = namespace(lib.LayoutTagLib)
def j = namespace("jelly:core")
def f = namespace(lib.FormTagLib)
def st = namespace("jelly:stapler")
def t = namespace("/lib/hudson")
def d = namespace("jelly:define")

import hudson.Functions
import com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher

http://127.0.0.1:8080/jenkins/job/wycds_web_2.2.0/ws/wycds-web/target/coveragereport/index.html
def url = "${rootURL}/plugin/topic-coverageDump-plugin/site/jacoco/index.html";
def requiresAdmin = app.getDescriptor("com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher").adminRequiredForTemplateTesting
def hasPermission = requiresAdmin ? hudson.Functions.hasPermission(app.ADMINISTER) : hudson.Functions.hasPermission(it.project, it.project.CONFIGURE);
l.layout {
    st.include(it: my.project, page: "sidepanel")
    l.main_panel {
        st.bind(var: "templateTester", value: my)
        script """function onSubmit() {
            var agentIP = document.getElementById('agent_Ip').value;
            var agentPort = document.getElementById('agent_Port').value;
            var buildId = document.getElementById('template_build').value;
            myiframe=document.createElement("iframe");
            myiframe.name="showframe" ;
            myiframe.width="600";
            myiframe.height="400";
            myiframe.src="${url}";
            document.getElementById('show').innerHTML = "";
            document.getElementById('show').appendChild(myiframe);

            templateTester.dumpReport(agentIP,agentPort,buildId, function(t) {
                    document.getElementById('ConsoleException').innerHTML = t.responseObject()[0];
                    var consoleOutput = t.responseObject()[1];
                    if(consoleOutput.length == 0) {
                        document.getElementById('output').style.display = 'none';
                    } else {
                        document.getElementById('output').style.display = 'block';
                        document.getElementById('console_output').innerHTML = consoleOutput;
                        document.getElementById('show').style.display = 'none';
                        alert(consoleOutput);
                        console.log(consoleOutput);

                    }
                });
            return false;
                   }"""

//        <iframe src="http://localhost:63342/topic-coverageDump-plugin/src/main/webapp/site/jacoco.index
//                .html" name="ifrm" id="ifrm" width="600" height="400"></iframe>

        h1(my.displayName)
        if (hasPermission) {
            h3(_("description"))
            form(action: "", method: "post", name: "templateTest", onSubmit: "return onSubmit();") {
                table {
                    f.entry(title: _("input agent ip")) {
                        f.textbox(name: "agent_Ip", id: "agent_Ip", clazz: "required"
                        )
                    }
                    f.entry(title: _("input agent port")) {
                        f.textbox(name: "agent_Port", id: "agent_Port", clazz: "required"
                        )
                    }
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
                h3(_("Coverage Report"))
            }
            div(id: "ConsoleException")
            div(id: "output", style: "display:none;") {
                hr()
                h3(_("Console Output"))
                pre(id: "console_output", clazz: "console-output")
            }


        } else {
            // redirect to the root in the case that someone tries to do
            // bad stuff...
            st.redirect(url: "${rootURL}")
        }
    }
}