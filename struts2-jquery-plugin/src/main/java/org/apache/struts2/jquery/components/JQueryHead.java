package org.apache.struts2.jquery.components;

import org.apache.struts2.components.Head;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.jquery.JQueryPluginConstants;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 1:14:11 PM
 * To change this template use File | Settings | File Templates.
 */
@StrutsTag(
    name="head",
    tldTagClass="org.apache.struts2.jquery.views.jsp.ui.JQueryHeadTag",
    description="Renders an Javscript tags and CSS links appropriate for using the struts2-jquery-plugin",
    allowDynamicAttributes=false)
public class JQueryHead extends Head {

    public JQueryHead(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    @Inject(JQueryPluginConstants.DEFAULT_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

}
