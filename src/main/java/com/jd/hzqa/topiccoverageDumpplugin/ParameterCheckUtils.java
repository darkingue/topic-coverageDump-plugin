package com.jd.hzqa.topiccoverageDumpplugin;

import hudson.EnvVars;
import hudson.util.FormValidation;

import javax.mail.internet.AddressException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * Created by qqs on 15/7/15.
 */
public class ParameterCheckUtils {
    private static final Logger LOGGER = Logger.getLogger(ParameterCheckUtils.class.getName());

    public FormValidation validateFormParameter_1(String parameter_1) {
        // Try and convert the recipient string to a list of InternetAddress. If this fails then the validation fails.
        if (parameter_1 != null) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("参数 parameter_1 格式异常");
        }

    }
}
