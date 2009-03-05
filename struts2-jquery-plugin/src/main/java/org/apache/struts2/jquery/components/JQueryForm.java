package org.apache.struts2.jquery.components;

import org.apache.struts2.components.Form;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.jquery.JQueryPluginConstants;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 1, 2009
 * Time: 8:11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@StrutsTag(
    name="form",
    tldTagClass="org.apache.struts2.jquery.views.jsp.ui.JQueryFormTag",
    description="Renders an input form",
    allowDynamicAttributes=true)
public class JQueryForm extends Form {

    private String ajaxResult ;
    private String ajaxResultHandler ;


    public JQueryForm(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    @Inject(JQueryPluginConstants.DEFAULT_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

    @Override
    public void evaluateParams() {
        super.evaluateParams();

        if (ajaxResult != null ) {
            addParameter("ajaxResult", findValue(ajaxResult, Boolean.class));
        }
        if (ajaxResultHandler != null ) {
            addParameter("ajaxResultHandler", findString(ajaxResultHandler));
        }
    }

    @StrutsTagAttribute(description="for specifying whether the result of the action of this form will be processed " +
        "by javascript, or if the form should be posted like a normal", type="Boolean", defaultValue="true")
    public void setAjaxResult(String ajaxResult) {
        this.ajaxResult = ajaxResult;
    }

    @StrutsTagAttribute(description="if the form posting will result in an ajax response, then a handler for the data "+
        " must be specified. Whether the response is XML, JSON, text or an HTML snippet, it will be passed to this "
            + "handler", type="String")
    public void setAjaxResultHandler(String ajaxResultHandler) {
        this.ajaxResultHandler = ajaxResultHandler ;
    }
    
}
