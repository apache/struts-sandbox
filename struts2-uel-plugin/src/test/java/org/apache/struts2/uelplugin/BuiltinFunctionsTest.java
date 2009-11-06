package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

import java.lang.reflect.InvocationTargetException;

import org.springframework.mock.web.MockServletContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.struts2.uelplugin.UelServletContextListener;

import javax.servlet.ServletContextEvent;


public class BuiltinFunctionsTest extends AbstractUelBaseTest {
    public void testGetText() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestAction action = new TestAction();
        stack.push(action);
        stack.getContext().put(ContextUtil.ACTION, action);

        assertEquals("This is the key!", stack.findValue("${getText('key')}"));
    }
}
