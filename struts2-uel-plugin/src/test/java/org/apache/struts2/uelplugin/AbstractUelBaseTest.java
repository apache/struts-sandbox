package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import org.springframework.mock.web.MockServletContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.uelplugin.reflection.GenericReflectionProvider;
import org.apache.struts2.util.StrutsTypeConverter;
import org.apache.commons.beanutils.converters.DateConverter;

import javax.servlet.ServletContextEvent;
import javax.el.ExpressionFactory;
import java.util.Map;
import java.text.ParseException;
import java.text.DateFormat;


public abstract class AbstractUelBaseTest extends XWorkTestCase {
    private ExpressionFactory factory = ExpressionFactory.newInstance();
    protected XWorkConverter converter;
    protected CompoundRoot root;
    protected UelValueStack stack;
    protected DateFormat format = DateFormat.getDateInstance();
    protected ReflectionProvider reflectionProvider;

    private class DateConverter extends StrutsTypeConverter {

        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            try {
                return format.parseObject(values[0]);
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public String convertToString(Map context, Object o) {
            return format.format(o);
        }

    }


    protected void setUp() throws Exception {
        super.setUp();

        loadConfigurationProviders(new StubConfigurationProvider() {
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.factory(ValueStack.class, UelValueStack.class);
                builder.factory(ValueStackFactory.class, UelValueStackFactory.class);
                builder.factory(ReflectionProvider.class, GenericReflectionProvider.class);
                //builder.factory(StrutsTypeConverter)
            }
        });

        converter = container.getInstance(XWorkConverter.class);
        reflectionProvider = container.getInstance(ReflectionProvider.class);
        converter.registerConverter("java.util.Date", new DateConverter());
        this.root = new CompoundRoot();
        this.stack = new UelValueStack(container);
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
