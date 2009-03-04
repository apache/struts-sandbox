package org.apache.struts2.jquery.views.jsp.ui;

import org.apache.struts2.views.jsp.ui.ResetTag;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.components.Component;
import org.apache.struts2.jquery.components.JQueryReset;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 4, 2009
 * Time: 4:02:54 PM
 * To change this template use File | Settings | File Templates.
 */
@StrutsTag(
    name="reset",
    tldTagClass="org.apache.struts2.jquery.views.jsp.ui.JQueryResetTag",
    description="Render a reset button",
    allowDynamicAttributes=true)
public class JQueryResetTag extends ResetTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQueryReset(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JQueryReset reset = (JQueryReset) component;
        // add custom params here
    }
}
