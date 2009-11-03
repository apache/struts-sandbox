package org.apache.struts2.uelplugin.contextlistener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.apache.struts2.uelplugin.ExpressionFactoryHolder;
import org.apache.struts2.uelplugin.elresolvers.CompoundRootELResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkBeanELResolver;

/**
 * Responsible for registering the ELResolvers.
 */
public class UelServletContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {
        ServletContext servletContext = contextEvent.getServletContext();
        JspApplicationContext jspApplicationContext = JspFactory
                .getDefaultFactory().getJspApplicationContext(servletContext);
        jspApplicationContext.addELResolver(new CompoundRootELResolver());
        jspApplicationContext.addELResolver(new XWorkBeanELResolver());
        contextEvent.getServletContext().log(
                "CompoundRootELResolver and XWorkBeanELResolver registered");
        ExpressionFactoryHolder.setExpressionFactory(jspApplicationContext
                .getExpressionFactory());
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
    }
}
