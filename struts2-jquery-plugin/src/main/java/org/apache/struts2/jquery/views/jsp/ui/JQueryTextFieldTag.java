package org.apache.struts2.jquery.views.jsp.ui;

import org.apache.struts2.views.jsp.ui.TextFieldTag;
import org.apache.struts2.components.Component;
import org.apache.struts2.jquery.components.JQueryTextField;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 3:21:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class JQueryTextFieldTag extends TextFieldTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQueryTextField(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JQueryTextField textField = (JQueryTextField) component;
        // add custom stuff here 
    }
}
