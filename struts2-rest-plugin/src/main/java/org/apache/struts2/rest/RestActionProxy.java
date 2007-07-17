/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts2.rest;

import com.opensymphony.xwork2.DefaultActionProxy;
import com.opensymphony.xwork2.XWorkMessages;
import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.util.XWorkConverter;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.rest.handler.MimeTypeHandler;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * The Default ActionProxy implementation
 *
 * @author Rainer Hermanns
 * @author Revised by <a href="mailto:hu_pengfei@yahoo.com.cn">Henry Hu</a>
 * @author tmjee
 * 
 * @version $Date: 2007-04-01 02:33:23 +1000 (Sun, 01 Apr 2007) $ $Id: DefaultActionProxy.java 1415 2007-03-31 16:33:23Z rainerh $
 * @since 2005-8-6
 */
public class RestActionProxy extends DefaultActionProxy {
	
	private final Log LOG = LogFactory.getLog(RestActionProxy.class);
    private XWorkConverter xworkConverter;
    private String defaultHandlerName;
    private Container container;

    public RestActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
        super(namespace, actionName, extraContext, executeResult, cleanupContext);
    }
    
    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject("struts.rest.defaultHandlerName")
    public void setDefaultHandlerName(String name) {
        this.defaultHandlerName = name;
    }
    
    @Inject
    public void setContainer(Container cont) {
        this.container = cont;
    }
    
    public void prepare() throws Exception {
        String profileKey = "create RestActionProxy: ";
        try {
            UtilTimerStack.push(profileKey);
            config = configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);
    
            if (config == null && unknownHandler != null) {
                config = unknownHandler.handleUnknownAction(namespace, actionName);
            }
            if (config == null) {
                String message;
    
                if ((namespace != null) && (namespace.trim().length() > 0)) {
                    message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        namespace, actionName
                    });
                } else {
                    message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        actionName
                    });
                }
                throw new ConfigurationException(message);
            }
            
            invocation = new RestActionInvocation(objectFactory, unknownHandler, this, extraContext, true, actionEventListener);
            ((RestActionInvocation)invocation).setXWorkConverter(xworkConverter);
            ((RestActionInvocation)invocation).setDefaultHandlerName(defaultHandlerName);
            Set<String> names = container.getInstanceNames(MimeTypeHandler.class);
            for (String name : names) {
                ((RestActionInvocation)invocation).addMimeTypeHandler(name, container.getInstance(MimeTypeHandler.class, name));
            }
            
            resolveMethod();
        } finally {
            UtilTimerStack.pop(profileKey);
        }
    }
    
    private void resolveMethod() {
        // if the method is set to null, use the one from the configuration
        // if the one from the configuration is also null, use "execute"
        if (!TextUtils.stringSet(this.method)) {
            this.method = config.getMethodName();
            if (!TextUtils.stringSet(this.method)) {
                this.method = "execute";
            }
        }
    }

    public String getDefaultHandlerName() {
        return defaultHandlerName;
    }

    public XWorkConverter getXWorkConverter() {
        return xworkConverter;
    }
    
    
}
