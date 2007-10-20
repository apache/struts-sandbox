package org.apache.struts2.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.handler.ContentTypeHandler;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class ContentTypeHandlerSelector {

    private Map<String,ContentTypeHandler> handlers = new HashMap<String,ContentTypeHandler>();
    private String defaultHandlerName;

    @Inject("struts.rest.defaultHandlerName")
    public void setDefaultHandlerName(String name) {
        this.defaultHandlerName = name;
    }
    
    @Inject
    public void setContainer(Container container) {
        Set<String> names = container.getInstanceNames(ContentTypeHandler.class);
        for (String name : names) {
            ContentTypeHandler handler = container.getInstance(ContentTypeHandler.class, name);
            this.handlers.put(handler.getExtension(), handler);
        }
    }
    
    public ContentTypeHandler getHandlerForRequest(HttpServletRequest req) {
        String extension = findExtension(req.getRequestURI());
        if (extension == null) {
            extension = defaultHandlerName;
        }
        return handlers.get(extension);
    }
    
    protected String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos+1);
        }
        return null;
    }
}
