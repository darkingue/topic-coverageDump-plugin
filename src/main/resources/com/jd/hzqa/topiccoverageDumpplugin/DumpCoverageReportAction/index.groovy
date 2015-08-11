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
def url = "${rootURL}/" + my.project.url + "ws/";
def requiresAdmin = app.getDescriptor("com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher").adminRequiredForTemplateTesting
def hasPermission = requiresAdmin ? hudson.Functions.hasPermission(app.ADMINISTER) : hudson.Functions.hasPermission(it.project, it.project.CONFIGURE);
l.layout {
    st.include(it: my.project, page: "sidepanel")
    l.main_panel {
        st.bind(var: "templateTester", value: my)
        script """
        function onSubmit() {
            var svn_Src_Dir = document.getElementById('svn_Src_Dir').value;
            var agentIP = document.getElementById('agent_Ip').value;
            var agentPort = document.getElementById('agent_Port').value;
            var buildId;
            (document.getElementById('template_build').value != null) ? (buildId = document.getElementById('template_build').value) : (buildId = "");
            document.getElementById('show').style.display = 'none';


            templateTester.dumpReport(svn_Src_Dir, agentIP, agentPort, buildId, function(t) {
                document.getElementById('ConsoleException').innerHTML = t.responseObject()[0];
                var ConsoleException = t.responseObject()[0];
                var agentCheck = t.responseObject()[1];
                var dumpUnsuccess = t.responseObject()[2];
                var realFile = t.responseObject()[3]
                document.getElementById('show').innerHTML = "";
                document.getElementById('show').style.display = 'none';


                if (agentCheck.length != 0) {
                    document.getElementById('show').style.display = 'none';
                    alert(agentCheck);
                } else if (dumpUnsuccess.length != 0) {
                    document.getElementById('show').style.display = 'none';
                    alert(dumpUnsuccess);
                } else if (ConsoleException.length != 0) {
                    document.getElementById('show').style.display = 'none';
                    alert(ConsoleException);
                } else {
                    myiframe = document.createElement("iframe");
                    myiframe.name = "showframe";
                    myiframe.width = "900";
                    myiframe.height = "400";
                    myiframe.src = "${url}" + realFile + "/coveragereport/index.html";
                    document.getElementById('show').appendChild(myiframe);
                    document.getElementById('show').style.display = 'block';
                }
            });
            return false;
        }
        """

//        <iframe src="http://localhost:63342/topic-coverageDump-plugin/src/main/webapp/site/jacoco.index
//                .html" name="ifrm" id="ifrm" width="600" height="400"></iframe>

        h1(my.displayName)
        if (hasPermission) {
            h3(_("description"))
            form(action: "", method: "post", name: "templateTest", onSubmit: "return onSubmit();") {
                table {
                    f.entry(title: _("svn_Src_Dir"), description: "如果需要dump覆盖率的工程为当前project的一个module, 请填写该module" +
                            "目录名,默认为当前project") {
                        f.textbox(name: "svn_Src_Dir", id: "svn_Src_Dir")
                    }
                    f.entry(title: _("input agent ip"), description: "请输入需要dump覆盖率的主机IP") {
                        f.textbox(name: "agent_Ip", id: "agent_Ip", clazz: "required")
                    }
                    f.entry(title: _("input agent port"), description: "请输入agent的端口名,如不输入,默认使用全局config中的agentport 设置") {
                        f.textbox(name: "agent_Port", id: "agent_Port")
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

        } else {
            // redirect to the root in the case that someone tries to do
            // bad stuff...
            st.redirect(url: "${rootURL}")
        }
    }
}