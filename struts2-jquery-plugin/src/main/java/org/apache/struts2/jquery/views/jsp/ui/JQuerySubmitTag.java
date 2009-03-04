package org.apache.struts2.jquery.views.jsp.ui;

import org.apache.struts2.views.jsp.ui.SubmitTag;
import org.apache.struts2.components.Component;
import org.apache.struts2.jquery.components.JQuerySubmit;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 4:04:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class JQuerySubmitTag extends SubmitTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQuerySubmit(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JQuerySubmit submit = (JQuerySubmit) component;
        // add custom params here
    }
}
