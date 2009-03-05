package org.apache.struts2.jquery.components;

import org.apache.struts2.components.Submit;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.jquery.JQueryPluginConstants;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 4:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
@StrutsTag(
    name="submit",
    tldTagClass="org.apache.struts2.jquery.views.jsp.ui.JQuerySubmitTag",
    description="Render a submit button",
    allowDynamicAttributes=true)
public class JQuerySubmit extends Submit {

    public JQuerySubmit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    @Inject(JQueryPluginConstants.DEFAULT_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }
}
