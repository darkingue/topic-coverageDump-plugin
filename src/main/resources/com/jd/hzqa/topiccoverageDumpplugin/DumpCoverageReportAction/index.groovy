package com.jd.hzqa.topiccoverageDumpplugin.DumpCoverageReportAction
// Namespaces
def l = namespace(lib.LayoutTagLib)
def j = namespace("jelly:core")
def f = namespace(lib.FormTagLib)
def st = namespace("jelly:stapler")
def t = namespace("/lib/hudson")
def d = namespace("jelly:define")

http://127.0.0.1:8080/jenkins/job/wycds_web_2.2.0/ws/wycds-web/target/coveragereport/index.html
def requiresAdmin = app.getDescriptor("com.jd.hzqa.topiccoverageDumpplugin.DumpCoveragePublisher").adminRequiredForTemplateTesting
def hasPermission = requiresAdmin ? hudson.Functions.hasPermission(app.ADMINISTER) : hudson.Functions.hasPermission(it.project, it.project.CONFIGURE);
l.layout {
    st.include(it: my.project, page: "sidepanel")
    l.main_panel {
        st.bind(var: "templateTester", value: my)
        script """
        function onSubmit() {
            document.getElementById('dumpform').style.display = 'none';
            document.getElementById('show').style.display = 'none';
            document.getElementById('loading').style.display = 'block';

            var svn_Src_Dir = document.getElementById('svn_Src_Dir').value;
            var agentIP = document.getElementById('agent_Ip').value;
            var agentPort = document.getElementById('agent_Port').value;
            var buildId;
            (document.getElementById('template_build').value != null) ? (buildId = document.getElementById('template_build').value) : (buildId = "");

            templateTester.dumpReport(svn_Src_Dir, agentIP, agentPort, buildId, function(t) {
                document.getElementById('ConsoleException').innerHTML = t.responseObject()[0];
                var ConsoleException = t.responseObject()[0];
                var agentCheck = t.responseObject()[1];
                var dumpUnsuccess = t.responseObject()[2];
                var realReportUrl = t.responseObject()[3]
                document.getElementById('show').innerHTML = "";
                document.getElementById('loading').style.display = 'none';
                document.getElementById('dumpform').style.display = 'block';



                if (agentCheck.length != 0) {
                    alert(agentCheck);
                } else if (dumpUnsuccess.length != 0) {
                    alert(dumpUnsuccess);
                } else if (ConsoleException.length != 0) {
                    alert("未知异常，请联系管理员查看更多信息!");
                } else {
                    myiframe = document.createElement("iframe");
                    myiframe.name = "showframe";
                    myiframe.width = "100%";
                    myiframe.height = "500";
                    myiframe.src = "${rootURL}"+"/"+realReportUrl;
                    document.getElementById('show').appendChild(myiframe);
                    document.getElementById('show').style.display = 'block';
                }
            });
            return false;
        }
        """
        script """
        function gradeChange() {
           var template_buildObj= document.getElementById('template_build');
           var selectbuild=template_buildObj.value
           var lastbuild="${my.project.builds.id[0]}"
           if(selectbuild==lastbuild){
           document.getElementById('table1').style.display ='block' ;
           document.getElementById('table3').style.display = 'block';
           document.getElementById('show').innerHTML = "";
           }else{
           document.getElementById('table1').style.display = 'none';
           document.getElementById('table3').style.display = 'none';
           document.getElementById('show').innerHTML = "";
           templateTester.getDefaultCovReportUrl(selectbuild, function(t) {
           var historyReportUrl =  t.responseObject();
           if(historyReportUrl == "null"){
                    alert("对不起,本次构建没有覆盖率存档！请确认")
                    }else{
                    myiframe = document.createElement("iframe");
                    myiframe.name = "showframe";
                    myiframe.width = "100%";
                    myiframe.height = "500";
                    myiframe.src = "${rootURL}"+"/"+historyReportUrl+"";
                    document.getElementById('show').appendChild(myiframe);
                    document.getElementById('show').style.display = 'block';
                    }
           });
           }
        }
        """

//        <iframe src="http://localhost:63342/topic-coverageDump-plugin/src/main/webapp/site/jacoco.index
//                .html" name="ifrm" id="ifrm" width="600" height="400"></iframe>

        h1(my.displayName)
        if (hasPermission) {
            h3(_("description"))
            form(id: "dumpform", action: "", method: "post", name: "templateTest", onsubmit: "return onSubmit();") {

                table(id: "table1") {
                    f.entry(title: _("svn_Src_Dir"), description: "如果需要dump覆盖率的工程为当前project的一个module, " +
                            "请填写该module目录名,默认为当前project") {
                        f.textbox(name: "svn_Src_Dir", id: "svn_Src_Dir")
                    }
                    f.entry(title: _("input agent ip"), description: "请输入需要dump覆盖率的主机IP") {
                        f.textbox(name: "agent_Ip", id: "agent_Ip", clazz: "required")
                    }
                    f.entry(title: _("input agent port"), description: "请输入agent的端口名,如不输入,默认使用全局config中的agentport 设置") {
                        f.textbox(name: "agent_Port", id: "agent_Port")
                    }
                }
                table(id: "table2") {
                    f.entry(title: "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp") {
                        select(name: "template_build", id: "template_build", onchange:
                                "gradeChange()") {
                            my.project.builds.each { build ->
                                f.option(value: build.id, "#${build.number} (${build.result})")
                            }
                        }
                    }
                }
                table(id: "table3") {
                    f.entry(title: "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp") {
                        f.submit(value: _("DUMP NOW!"))
                    }
                }
            }

            div(id: "loading", style: "position:absolute; color:#0000FF;left:423px; top:261px; width:227px; " +
                    "height:20px; z-index:1;display:none") {
                h3(_("正在载入中,请稍等......"))
            }
            div(id: "show")
            div(id: "ConsoleException")

        } else {
            // redirect to the root in the case that someone tries to do
            // bad stuff...
            st.redirect(url: "${rootURL}")
        }
    }
}