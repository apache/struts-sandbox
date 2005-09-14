/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.pageflow.internal;

import org.apache.commons.el.ExpressionEvaluatorImpl;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import java.util.Map;

/**
 * Internal class used to evaluate simple action expressions.
 * <p/>
 * todo: need to merge this down into the expression language registration infrastructure
 * todo: need to provdie an ImplicitObjectFactory that will create Maps for requestScope, sessionScope, etc.
 */
public class InternalExpressionUtils {

    public static final boolean evaluateCondition(String expression)
            throws ELException {
        return ((Boolean) evaluate(expression, Boolean.class)).booleanValue();
    }

    public static final String evaluateMessage(String expression)
            throws ELException {
        return (String) evaluate(expression, String.class);
    }

    /* do not construct */
    private InternalExpressionUtils() {
    }

    private static final Object evaluate(String expression, Class expectedType)
            throws ELException {
        // todo: can this be static / final?
        ExpressionEvaluator ee = getExpressionEvaluator();
        return ee.evaluate(expression, expectedType, getVariableResolver(), null);
    }

    private static final ExpressionEvaluator getExpressionEvaluator() {
        return new ExpressionEvaluatorImpl();
    }

    private static final VariableResolver getVariableResolver() {
        return new SimpleActionVariableResolver();
    }

    private static class SimpleActionVariableResolver
            implements VariableResolver {

        public Object resolveVariable(String name) {
            // requestScope, sessionScope, applicationScope, param, paramValues, header, headerValues, cookie, initParam, <default>
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            Map requestScope = actionContext.getRequestScope();
            Map sessionScope = actionContext.getSession();

            if (name.equals("actionForm"))
                return actionContext.getFormBean();
            else if (name.equals("requestScope"))
                return requestScope;
            else if (name.equals("sessionScope"))
                return sessionScope;
            else if (name.equals("applicationScope"))
                return actionContext.getApplication();
            else if (name.equals("param"))
                return actionContext.getWebContext().getParam();
            else if (name.equals("paramValues"))
                return actionContext.getWebContext().getParamValues();
            /* TODO: re-add these in a Servlet-specific implementation
            else if(name.equals("header"))
                return buildHeaderMap();
            else if(name.equals("headerValues"))
                return buildHeadersMap();
            else if(name.equals("cookie"))
                return buildCookieMap();
            else if(name.equals("initParam"))
                return buildInitParamMap();
            */
            // chain up the request > session (if exists) > application
            // note, this should handle pageFlow, sharedFlow, and bundle if they're in the request
            // attribute map already
            else {
                Object val;
                if ((val = requestScope.get(name)) != null)
                    return val;
                else if (sessionScope != null && (val = sessionScope.get(name)) != null)
                    return val;
                else
                    return actionContext.getApplication().get(name);
            }
        }

        /*
        private static final Map buildCookieMap(HttpServletRequest httpServletRequest)
        {
            HttpServletRequest servletRequest = httpServletRequest;
            Map cookieMap = new HashMap();
            Cookie[] cookies = servletRequest.getCookies();
            for(int i = 0; i < cookies.length; i++)
            {
                if(!cookieMap.containsKey(cookies[i].getName()))
                    cookieMap.put(cookies[i].getName(), cookies[i]);
            }
            return cookieMap;
        }

        private static final Map buildHeadersMap(HttpServletRequest httpServletRequest)
        {
            final HttpServletRequest _servletRequest = httpServletRequest;
            return new EnumeratedMap()
            {
                public Enumeration enumerateKeys()
                {
                    return _servletRequest.getHeaderNames();
                }

                public Object getValue(Object key)
                {
                    return (key instanceof String ? _servletRequest.getHeaders((String)key) : null);
                }

                public boolean isMutable() {return false;}
            };
        }

        private static final Map buildHeaderMap(HttpServletRequest httpServletRequest)
        {
            final HttpServletRequest _servletRequest = httpServletRequest;
            return new EnumeratedMap()
            {
                public Enumeration enumerateKeys()
                {
                    return _servletRequest.getHeaderNames();
                }

                public Object getValue(Object key)
                {
                    return (key instanceof String ? _servletRequest.getHeader((String)key) : null);
                }

                public boolean isMutable() {return false;}
            };
        }
        */

    }
}
