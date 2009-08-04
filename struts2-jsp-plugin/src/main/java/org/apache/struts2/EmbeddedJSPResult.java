package org.apache.struts2;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.jasper.JasperException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.jsp.JspPage;
import javax.servlet.jsp.HttpJspPage;
import javax.servlet.Servlet;


public class EmbeddedJSPResult extends StrutsResultSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJSPResult.class);

    

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
       JSPRuntime.handle(finalLocation);
    }
}
