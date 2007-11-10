package com.googlecode.struts2juel.contextlistener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import com.googlecode.struts2juel.elresolvers.CompoundRootELResolver;
import com.googlecode.struts2juel.elresolvers.XWorkBeanELResolver;

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
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
