package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.uelplugin.UelServletContextListener;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public class JuelMethodInvocationTest extends XWorkTestCase {
    private XWorkConverter converter;
    private CompoundRoot root;
    private UelValueStack stack;

    public void testBasicMethods() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ActionContext.getContext().setValueStack(stack);

        assertEquals("text", stack.findValue("${' text '.trim()}"));
        assertEquals(3, stack.findValue("${'123'.length()}"));
    }

    public void testMethodsWithParams() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ActionContext.getContext().setValueStack(stack);

        assertEquals('2', stack.findValue("${'123'.charAt(1)}"));
        assertEquals("123456", stack.findValue("${'123'.concat('456')}"));
    }

    protected void setUp() throws Exception {
        super.setUp();

        MockServletContext servletContext = new MockServletContext();
        ActionContext context = new ActionContext(new HashMap());
        ActionContext.setContext(context);
        ServletActionContext.setServletContext(servletContext);

        //simulate start up
        UelServletContextListener listener = new UelServletContextListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));

        converter = container.getInstance(XWorkConverter.class);
        this.root = new CompoundRoot();
        this.stack = new UelValueStack(converter);
        stack.setRoot(root);
    }
}
