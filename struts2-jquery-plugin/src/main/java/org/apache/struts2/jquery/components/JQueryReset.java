package org.apache.struts2.jquery.components;

import org.apache.struts2.components.Reset;
import org.apache.struts2.jquery.JQueryPluginConstants;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 4, 2009
 * Time: 4:01:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class JQueryReset extends Reset {

    public JQueryReset(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    @Inject(JQueryPluginConstants.DEFAULT_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }
}
