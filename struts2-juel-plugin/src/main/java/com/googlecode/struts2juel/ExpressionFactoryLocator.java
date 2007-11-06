package com.googlecode.struts2juel;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionContext;

/**
 * Locates the current ExpressFactory. The preference is to use the app server's
 * built-in ExpressFactory. If that isn't available, then JUEL's ExpressFactory
 * will be used.
 */
public class ExpressionFactoryLocator {
	private static final String JUEL_FACTORY = "de.odysseus.el.ExpressionFactoryImpl";

	public static ExpressionFactory locateExpressFactory() {
		ExpressionFactory factory = null;
		// first try to load the default ExpressFactory from the JSP engine
		try {
			ActionContext actionContext = ActionContext.getContext();
			ServletContext servletContext = (ServletContext) actionContext
					.get(StrutsStatics.SERVLET_CONTEXT);
			JspFactory jspFactory = JspFactory.getDefaultFactory();
			JspApplicationContext jspAppCtx = jspFactory
					.getJspApplicationContext(servletContext);
			factory = jspAppCtx.getExpressionFactory();
		} catch (Throwable t) {
			// fallback to juel
			try {
				factory = (ExpressionFactory) Class.forName(JUEL_FACTORY)
						.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return factory;
	}
}
