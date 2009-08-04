package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;
import java.util.Map;

/**
 * Maintains a cache of jsp locations -> servlet instances for those jsps. When a jsp is requested
 * from the cache, the cache will block if the jsp was not compiled already, and wait for the compilation
 */
public abstract class JSPRuntime {
    //maps from jsp path -> pagelet
    private static final ServletCache servletCache = new ServletCache();

    public static void handle(String location) throws Exception {
        handle(location, false);
    }

    public static void handle(String location, boolean flush) throws Exception {
        final HttpServletResponse response = ServletActionContext.getResponse();
        final HttpServletRequest request = ServletActionContext.getRequest();

        int i = location.indexOf("?");
        if (i > 0) {
            //extract params from the url and add them to the request
            Map parameters = ActionContext.getContext().getParameters();
            String query = location.substring(i + 1);
            Map queryParams = UrlHelper.parseQueryString(query, true);
            if (queryParams != null && !queryParams.isEmpty())
                parameters.putAll(queryParams);
            location = location.substring(0, i);
        }

        Servlet servlet = servletCache.get(location);
        HttpJspPage page = (HttpJspPage) servlet;
        page.jspInit();

        page._jspService(request, response);
        if (flush)
            response.flushBuffer();
    }
}
