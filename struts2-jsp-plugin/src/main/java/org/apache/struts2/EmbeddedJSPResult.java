package org.apache.struts2;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.jasper.JasperException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.jsp.JspPage;
import javax.servlet.jsp.HttpJspPage;
import javax.servlet.Servlet;


public class EmbeddedJSPResult extends StrutsResultSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJSPResult.class);

    //maps from jsp path -> pagelet
    private static final ServletCache servletCache = new ServletCache(ServletActionContext.getServletContext());

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        Servlet servlet = servletCache.get(finalLocation);  
        HttpJspPage page = (HttpJspPage) servlet;
        page.jspInit();
        page._jspService(ServletActionContext.getRequest(), ServletActionContext.getResponse());
    }
}
