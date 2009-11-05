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


public class BuiltinFunctionsTest extends XWorkTestCase {
    private XWorkConverter converter;
    private CompoundRoot root;
    private UelValueStack stack;

    public void testGetText() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestAction action = new TestAction();
        stack.push(action);
        stack.getContext().put(ContextUtil.ACTION, action);

        assertEquals("This is the key!", stack.findValue("${getText('key')}"));
    }



    protected void setUp() throws Exception {
        super.setUp();

        converter = container.getInstance(XWorkConverter.class);
        this.root = new CompoundRoot();
        this.stack = new UelValueStack(converter);
        stack.setRoot(root);
        stack.getContext().put(ActionContext.CONTAINER, container);

        MockServletContext servletContext = new MockServletContext();
        ActionContext context = new ActionContext(stack.getContext());
        ActionContext.setContext(context);
        ServletActionContext.setServletContext(servletContext);

        //simulate start up
        UelServletContextListener listener = new UelServletContextListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
    }
}
