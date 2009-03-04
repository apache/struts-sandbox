package org.apache.struts2.jquery.components;

import org.apache.struts2.components.TextField;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.jquery.JQueryPluginStatics;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 2, 2009
 * Time: 3:18:30 PM
 * To change this template use File | Settings | File Templates.
 */
@StrutsTag(
    name="textfield",
    tldTagClass="org.apache.struts2.jquery.views.jsp.ui.JQueryTextFieldTag",
    description="Render an HTML input field of type text",
    allowDynamicAttributes=true)
public class JQueryTextField extends TextField {

    public JQueryTextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public String getTheme() {
        return JQueryPluginStatics.THEME_NAME;
    }
}
