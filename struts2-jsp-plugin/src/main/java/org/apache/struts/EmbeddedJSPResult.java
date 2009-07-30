package org.apache.struts;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.jasper.JspC;
import org.apache.jasper.JasperException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.jsp.JspPage;
import javax.servlet.jsp.HttpJspPage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;


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
