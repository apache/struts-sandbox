/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.XWorkConverter;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.MimeTypeHandler;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The Default ActionInvocation implementation
 *
 * @author Rainer Hermanns
 * @author tmjee
 * 
 * @version $Date: 2007-04-26 23:36:51 +1000 (Thu, 26 Apr 2007) $ $Id: DefaultActionInvocation.java 1499 2007-04-26 13:36:51Z mrdon $
 * 
 * @see com.opensymphony.xwork2.DefaultActionProxy
 */
public class RestActionInvocation extends DefaultActionInvocation {
    
    private final Log LOG = LogFactory.getLog(RestActionInvocation.class);
    
    private XWorkConverter converter;
    private Map<String,MimeTypeHandler> handlers = new HashMap<String,MimeTypeHandler>();
    private String defaultHandlerName;

    protected RestActionInvocation(ObjectFactory objectFactory, UnknownHandler handler, ActionProxy proxy, Map extraContext, boolean pushAction, ActionEventListener actionEventListener) throws Exception {
        super(objectFactory, handler, proxy, extraContext, pushAction,
                actionEventListener);
    }

    protected RestActionInvocation(ObjectFactory objectFactory, UnknownHandler handler, ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(objectFactory, handler, proxy, extraContext, pushAction);
    }

    protected RestActionInvocation(ObjectFactory objectFactory, UnknownHandler handler, ActionProxy proxy, Map extraContext) throws Exception {
        super(objectFactory, handler, proxy, extraContext);
    }
    
    public void setXWorkConverter(XWorkConverter conv) {
        this.converter = conv;
    }
    
    public void setDefaultHandlerName(String name) {
        this.defaultHandlerName = name;
    }
    
    public void addMimeTypeHandler(String name, MimeTypeHandler handler) {
        this.handlers.put(name, handler);
    }

    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        
        HttpServletRequest req = ServletActionContext.getRequest();
        String extension = findExtension(req.getRequestURI());
        if (extension == null) {
            extension = defaultHandlerName;
        }
        System.out.println("extension:"+extension);
        MimeTypeHandler handler = handlers.get(extension);
        if (handler != null && req.getContentLength() > 0) {
            invocationContext.getParameters().put("body", handler.toObject(req.getInputStream()));
        }
        
        String methodName = proxy.getMethod();
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing action method = " + actionConfig.getMethodName());
        }

        String timerKey = "invokeAction: "+proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);
            
            boolean methodCalled = false;
            Object methodResult = null;
            MethodMatch methodMatch = null;
            try {
                methodMatch = findMethod(action, methodName, invocationContext.getParameters());
            } catch (NoSuchMethodException e) {
                // hmm -- OK, try doXxx instead
                try {
                    String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    methodMatch = findMethod(action, altMethodName, invocationContext.getParameters());
                } catch (NoSuchMethodException e1) {
            		throw e;
                }
            }
        	
        	if (!methodCalled) {
                methodResult = callAction(action, methodMatch, invocationContext.getParameters());
        	}
        	
            if (methodResult instanceof Result) {
            	this.result = (Result) methodResult;
            	return null;
            } else {
                if (handler != null && methodResult != null) {
                    return handler.fromObject(methodResult, this);
                } else {
                    // treat as normal result code
                    return (String) methodResult;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The " + methodName + "() is not defined in action " + getAction().getClass() + "", e);
        } catch (InvocationTargetException e) {
            // We try to return the source exception.
            Throwable t = e.getTargetException();

            if (actionEventListener != null) {
                String result = actionEventListener.handleException(t, getStack());
                if (result != null) {
                    return result;
                }
            }
            if (t instanceof Exception) {
                throw(Exception) t;
            } else {
                throw e;
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    protected Object callAction(Object action, MethodMatch methodMatch, Map reqParams) throws IllegalAccessException, InvocationTargetException {
        Object methodResult;
        Class[] argTypes = methodMatch.method.getParameterTypes();
        Object[] args = new Object[argTypes.length];
        
        int x=0;
        for (String name : methodMatch.params) {
            args[x] = converter.convertValue(null, reqParams.get(name), argTypes[x]);
            x++;
        }
        
        methodResult = methodMatch.method.invoke(action, args);
        return methodResult;
    }
    
    protected MethodMatch findMethod(Object action, String actionName, Map<String,String> params) throws NoSuchMethodException {
        
        // Short cut so that only restful actions get the full method scan treatment
        if (action.getClass().getAnnotation(Restful.class) == null && !(action instanceof BasicRestful)) {
            return new MethodMatch(action.getClass().getMethod(actionName, new Class[0]), Collections.EMPTY_LIST);
        }
        Set<String> paramsInReq = params.keySet();
        
        int max = -1;
        Method method = null;
        List<String> nameParams = null;
        
        for (Method m : action.getClass().getMethods()) {
            String methodName = m.getName();
            if (methodName.startsWith(actionName)) {
                int count = 0;
                List<String> paramsInName = findParamsInName(methodName);
                for (String paramName : paramsInName) {
                    if (paramsInReq.contains(paramName)) {
                        count++;
                    } else {
                        count = -1;
                        break;
                    }
                }
                
                if (count > max) {
                    max = count;
                    method = m;
                    nameParams = paramsInName;
                }
            }
        }
        
        if (method == null) {
            throw new NoSuchMethodException("Unable to find method for "+actionName+" with params "+params.keySet());
        }
        return new MethodMatch(method, nameParams);
    }
    
    protected List<String> findParamsInName(String name) {
        List<String> list = new ArrayList<String>();
        int withPos = name.indexOf("With");
        if (withPos > -1) {
            String[] params = name.substring(withPos+4).split("And");
            for (int x=0; x<params.length; x++) {
                list.add(params[x].toLowerCase());
            }
        }
        return list;
    }
    
    protected String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos+1);
        }
        return null;
    }
    
    static class MethodMatch {
        public Method method;
        public List<String> params;
        public MethodMatch(Method method, List<String> params) {
           super();
           this.method = method;
           this.params = params;
        }
         
         
     }
    
    
}
