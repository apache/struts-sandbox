package org.apache.struts2.jquery.views.jsp.ui;

import org.apache.struts2.views.jsp.ui.FormTag;
import org.apache.struts2.components.Component;
import org.apache.struts2.jquery.components.JQueryForm;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 1, 2009
 * Time: 8:54:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class JQueryFormTag extends FormTag {

    // TODO the ajaxResult is making its way to the HTML, need to fix that
    private String ajaxResult ;
    private String ajaxResultHandler ;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQueryForm(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JQueryForm form = ((JQueryForm) component);
        form.setAjaxResult(ajaxResult);
        form.setAjaxResultHandler(ajaxResultHandler);
    }

    public void setAjaxResult(String ajaxResult) {
        this.ajaxResult = ajaxResult;
    }

    public void setAjaxResultHandler(String ajaxResultHandler) {
        this.ajaxResultHandler = ajaxResultHandler ;
    }
}
