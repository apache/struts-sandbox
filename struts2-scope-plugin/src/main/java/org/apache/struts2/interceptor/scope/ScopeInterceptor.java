/*
 * $Id: CreateSessionInterceptor.java 471756 2006-11-06 15:01:43Z husted $
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
package org.apache.struts2.interceptor.scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.interceptor.CreateSessionInterceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor creates the HttpSession.
 * <p/>
 * This is particular usefull when using the &lt;@s.token&gt; tag in freemarker templates.
 * The tag <b>do</b> require that a HttpSession is already created since freemarker commits
 * the response to the client immediately.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 *
 * <!-- START SNIPPET: extending -->
 *
 * <ul>
 *  <li>none</li>
 * </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <b>Example:</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="create-session"/&gt;
 *     &lt;interceptor-ref name="defaultStack"/&gt;
 *     &lt;result name="input"&gt;input_with_token_tag.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @version $Date: 2006-11-06 07:01:43 -0800 (Mon, 06 Nov 2006) $ $Id: CreateSessionInterceptor.java 471756 2006-11-06 15:01:43Z husted $
 */
public class ScopeInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -4590322556118858869L;

    private static final Log LOG = LogFactory.getLog(ScopeInterceptor.class);
    private static final Map<Class,CachedMethods> cachedMethods = Collections.synchronizedMap(new HashMap<Class,CachedMethods>());

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        
        ActionContext ctx = invocation.getInvocationContext();
        Class cls = action.getClass();
        boolean flashScopeUsed = false;
        List<Method> inMethods = findAnnotationedMethods(cls, false);
        for (Method m : inMethods) {
            In in = m.getAnnotation(In.class);
            String propName = in.value();
            if (propName.length() == 0) {
                propName = determinePropertyName(m.getName(), false);
            }
            
            flashScopeUsed |= in.scope() == ScopeType.FLASH;
            
            Object obj = findObjectInScope(in.scope(), propName, ctx);
            
            if (in.required() && obj == null) {
                throw new StrutsException("Scope object "+propName+" cannot be found in scope "+in.scope());
            }
            m.invoke(action, obj);
        }
        
        if (flashScopeUsed) {
            ctx.getSession().remove(ScopeType.FLASH.toString());
        }
        
        String ret = invocation.invoke();
        
        List<Method> outMethods = findAnnotationedMethods(cls, true);
        for (Method m : outMethods) {
            Out out = m.getAnnotation(Out.class);
            String propName = out.value();
            if (propName.length() == 0) {
                propName = determinePropertyName(m.getName(), true);
            }
            
            Object obj = m.invoke(action);
            
            putObjectInScope(out.scope(), propName, ctx, obj);
        }
        return ret;
    }


    /**
     * @param in
     * @param propName
     * @param ctx
     */
    private Object findObjectInScope(ScopeType scopeType, String propName, ActionContext ctx) {
        Object obj = null;
        if (obj == null && (scopeType == ScopeType.ACTION_CONTEXT ||scopeType == ScopeType.UNSPECIFIED)) {
            obj = ctx.get(propName);
        }
        if (obj == null && (scopeType == ScopeType.REQUEST || scopeType == ScopeType.UNSPECIFIED)) {
            obj = ServletActionContext.getRequest().getAttribute(propName);
        }
        if (obj == null && (scopeType == ScopeType.FLASH || scopeType == ScopeType.UNSPECIFIED)) {
            HttpSession session = ServletActionContext.getRequest().getSession(false);
            if (session != null) {
                Map flash = (Map)session.getAttribute(ScopeType.FLASH.toString());
                if (flash != null) {
                    obj = flash.get(propName);
                }
            }
        }
        if (obj == null && (scopeType == ScopeType.SESSION || scopeType == ScopeType.UNSPECIFIED)) {
            obj = ctx.getSession().get(propName);
        }
        if (obj == null && (scopeType == ScopeType.APPLICATION || scopeType == ScopeType.UNSPECIFIED)) {
            obj = ctx.getApplication().get(propName);
        }
        return obj;
    }
    
    private void putObjectInScope(ScopeType scopeType, String propName, ActionContext ctx, Object obj) {
        HttpSession session = null;
        switch (scopeType) {
            case ACTION_CONTEXT : ctx.put(propName, obj); 
                                  break;
            case REQUEST        : ServletActionContext.getRequest().setAttribute(propName, obj); 
                                  break;
            case FLASH          : session = ServletActionContext.getRequest().getSession(true);
                                  Map<String,Object> flash = (Map<String,Object>) session.getAttribute(ScopeType.FLASH.toString());
                                  if (flash == null) {
                                      flash = new HashMap<String,Object>();
                                  }
                                  flash.put(propName, obj);
                                  session.setAttribute(ScopeType.FLASH.toString(), flash);
                                  break;
            case SESSION        : session = ServletActionContext.getRequest().getSession(true);
                                  session.setAttribute(propName, obj);
                                  break;
            case APPLICATION    : ctx.getApplication().put(propName, obj);
                                  break;
        }
    }


    private List<Method> findAnnotationedMethods(Class cls, boolean out) {
        CachedMethods cache = cachedMethods.get(cls);
        if (cache == null) {
            cache = new CachedMethods();
            cachedMethods.put(cls, cache);
        }
        List<Method> methods = null;
        if (out) {
            methods = cache.getOutMethods();
            if (methods == null) {
                methods = AnnotationUtils.findAnnotatedMethods(cls, Out.class);
                cache.setOutMethods(methods);
            }
        } else {
            methods = cache.getInMethods();
            if (methods == null) {
                methods = AnnotationUtils.findAnnotatedMethods(cls, In.class);
                cache.setInMethods(methods);
            }
        }
        return methods;
    }
    
    private String determinePropertyName(String methodName, boolean out) {
        String name = methodName;
        if (!out && methodName.startsWith("set")) {
            name = name.substring("set".length());
        } else if (out) {
            if (methodName.startsWith("get") || methodName.startsWith("has")) {
                name = name.substring("get".length());
            } else if (methodName.startsWith("is")) {
                name = name.substring("is".length());
            }
        }
        if (Character.isUpperCase(name.charAt(0))) {
            if (name.length() == 1 || !Character.isUpperCase(name.charAt(1))) {
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);return name;
            }
        }
        return name;
    }
    
    private static class CachedMethods {
        private List<Method> inMethods;
        private List<Method> outMethods;
        /**
         * @return the inMethods
         */
        public List<Method> getInMethods() {
            return inMethods;
        }
        /**
         * @param inMethods the inMethods to set
         */
        public void setInMethods(List<Method> inMethods) {
            this.inMethods = inMethods;
        }
        /**
         * @return the outMethods
         */
        public List<Method> getOutMethods() {
            return outMethods;
        }
        /**
         * @param outMethods the outMethods to set
         */
        public void setOutMethods(List<Method> outMethods) {
            this.outMethods = outMethods;
        }
    }

}
