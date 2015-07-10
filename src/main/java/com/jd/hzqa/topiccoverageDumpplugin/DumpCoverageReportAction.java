package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * Created by qqs on 15/7/9.
 */
public class DumpCoverageReportAction implements Action {

    private final AbstractProject<?, ?> project;

    public DumpCoverageReportAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        // returning null allows us to have our own action.jelly
        return null;
    }

    public String getDisplayName() {
        return DumpConfig.getDisplayName();
    }

    public String getUrlName() {
        return DumpConfig.getUrlName();
    }

    private String renderError(Exception ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h3>An error occured trying to render the template:</h3><br/>");
        builder.append("<span style=\"color:red; font-weight:bold\">");
        builder.append(ex.toString().replace("\n", "<br/>"));
        builder.append("</span>");
        return builder.toString();
    }

    //    public FormValidation doTemplateFileCheck(@QueryParameter final String value) {
    //        if(!StringUtils.isEmpty(value)) {
    //            if(value.startsWith("managed:")) {
    //                return checkForManagedFile(value);
    //            } else {
    //                // first check in the default resources area...
    //                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("hudson/plugins/emailext/templates/" + value);
    //                if(inputStream == null) {
    //                    final File scriptsFolder = new File(Jenkins.getInstance().getRootDir(), "email-templates");
    //                    final File scriptFile = new File(scriptsFolder, value);
    //                    if(!scriptFile.exists()) {
    //                        return FormValidation.error("The file '" + value + "' does not exist");
    //                    }
    //                }
    //            }
    //        }
    //        return FormValidation.ok();
    //    }
    //
    //    private FormValidation checkForManagedFile(final String value) {
    //        Plugin plugin = Jenkins.getInstance().getPlugin("config-file-provider");
    //        if(plugin != null) {
    //            Config config = null;
    //            Collection<ConfigProvider> providers = getTemplateConfigProviders();
    //            for(ConfigProvider provider : providers) {
    //                for(Config c : provider.getAllConfigs()) {
    //                    if(c.name.equalsIgnoreCase(value) && provider.isResponsibleFor(c.id)) {
    //                        return FormValidation.ok();
    //                    }
    //                }
    //            }
    //        } else {
    //            return FormValidation.error(Messages.EmailExtTemplateAction_ConfigFileProviderNotAvailable());
    //        }
    //        return FormValidation.error(Messages.EmailExtTemplateAction_ManagedTemplateNotFound());
    //    }
    //
    //    private static Collection<ConfigProvider> getTemplateConfigProviders() {
    //        Collection<ConfigProvider> providers = new ArrayList<ConfigProvider>();
    //        ExtensionList<ConfigProvider> all = ConfigProvider.all();
    //        ConfigProvider p = all.get(GroovyTemplateConfig.GroovyTemplateConfigProvider.class);
    //        if(p != null) {
    //            providers.add(p);
    //        }
    //
    //        p = all.get(JellyTemplateConfig.JellyTemplateConfigProvider.class);
    //        if(p != null) {
    //            providers.add(p);
    //        }
    //        return providers;
    //    }
    //
    //    @JavaScriptMethod
    //    public String[] renderTemplate(String templateFile, String buildId) {
    //        String[] result = new String[2];
    //        result[0] = StringUtils.EMPTY;
    //        result[1] = StringUtils.EMPTY;
    //
    //        try {
    //            AbstractBuild<?,?> build = project.getBuild(buildId);
    //            ByteArrayOutputStream stream = new ByteArrayOutputStream();
    //            StreamTaskListener listener = new StreamTaskListener(stream);
    //
    //            if(templateFile.endsWith(".jelly")) {
    //                JellyScriptContent jellyContent = new JellyScriptContent();
    //                jellyContent.template = templateFile;
    //                result[0] = jellyContent.evaluate(build, listener, "JELLY_SCRIPT");
    //            } else {
    //                ScriptContent scriptContent = new ScriptContent();
    //                scriptContent.template = templateFile;
    //                result[0] = scriptContent.evaluate(build, listener, "SCRIPT");
    //            }
    //            result[1] = stream.toString(ExtendedEmailPublisher.descriptor().getCharset());
    //        } catch (Exception ex) {
    //            result[0] = renderError(ex);
    //        }
    //        return result;
    //    }

    public AbstractProject<?, ?> getProject() {
        return project;
    }
}