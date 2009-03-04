package org.apache.struts2.jquery.views.jsp.ui;

import org.apache.struts2.views.jsp.ui.HeadTag;
import org.apache.struts2.components.Component;
import org.apache.struts2.jquery.components.JQueryHead;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 1:14:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class JQueryHeadTag extends HeadTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQueryHead(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JQueryHead head = (JQueryHead) component ;
        // set new parameters in here, if we take some
    }

    
}
