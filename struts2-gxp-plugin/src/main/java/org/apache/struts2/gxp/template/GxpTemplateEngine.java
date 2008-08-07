package org.apache.struts2.gxp.template;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;

import com.google.gxp.base.GxpContext;

public class GxpTemplateEngine extends BaseTemplateEngine {

    @Override
    protected String getSuffix() {
        return "gxp";
    }

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);
        Map actionContext = templateContext.getStack().getContext();
        HttpServletRequest req = (HttpServletRequest) actionContext.get(ServletActionContext.HTTP_REQUEST);

        for (Template template : templates) {
            GxpcUtil.buildAndExec(template.getDir(), getFinalTemplateName(template), true, templateContext
                    .getWriter(), new GxpContext(req.getLocale()));
        }
    }

    protected String getFinalTemplateName(Template template) {
        StringBuilder sb = new StringBuilder();
        sb.append(template.getTheme()).append("/").append(template.getName().replaceAll("-", ""));
        if (template.getName().indexOf(".") <= 0) {
            sb.append(".").append(getSuffix());
        }

        return sb.toString();
    }

}
