package org.apache.struts2.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class ContentTypeInterceptor implements Interceptor {

    ContentTypeHandlerSelector selector;
    
    @Inject
    public void setContentTypeHandlerSelector(ContentTypeHandlerSelector sel) {
        this.selector = sel;
    }
    
    public void destroy() {}

    public void init() {}

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ContentTypeHandler handler = selector.getHandlerForRequest(request);
        
        Object target = invocation.getAction();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven)target).getModel();
        }
        
        if (request.getContentLength() > 0) {
            handler.toObject(request.getInputStream(), target);
        }
        return invocation.invoke();
    }

}
