/*
 * $Id$
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
package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A ValueStack that uses Unified EL as the underlying Expression Language.
 */
public class UELValueStack implements ValueStack, ClearableValueStack, Serializable {
    public interface ContextKey {
    }

    private static final Logger LOG = LoggerFactory.getLogger(UELValueStack.class);

    private CompoundRoot root = new CompoundRoot();
    private transient Map context;
    private Class defaultType = Object.class;
    private Map overrides;

    private ELContext elContext;
    private final Container container;
    private final XWorkConverter xworkConverter;

    private boolean logMissingProperties;
    private boolean devMode;

    public UELValueStack(Container container) {
        this(container, new CompoundRoot());
    }

    public UELValueStack(Container container, ValueStack vs) {
        this(container, new CompoundRoot(vs.getRoot()));
    }

    public UELValueStack(Container container, CompoundRoot root) {
        this.container = container;
        this.xworkConverter = container.getInstance(XWorkConverter.class);
        setRoot(new CompoundRoot(root));
    }

    @Inject("devMode")
    public void setDevMode(String mode) {
        devMode = "true".equalsIgnoreCase(mode);
    }

    @Inject(value = "logMissingProperties", required = false)
    public void setLogMissingProperties(String logMissingProperties) {
        this.logMissingProperties = "true".equalsIgnoreCase(logMissingProperties);
    }

    public String findString(String expr, boolean throwException) {
        return (String) findValue(expr, String.class);
    }

    public String findString(String expr) {
        return findString(expr, false);
    }

    public Object findValue(String expr) {
        return findValue(expr, defaultType, false);
    }

    public Object findValue(String expr, boolean throwException) {
        return findValue(expr, defaultType, throwException);
    }

    public Object findValue(String expr, Class asType) {
        return findValue(expr, asType, false);
    }

    public Object findValue(String expr, Class asType, boolean throwException) {
        String originalExpression = expr;
        try {
            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }
            if (expr != null && expr.startsWith("%{")) {
                // replace %{ with ${
                expr = "#" + expr.substring(1);
            }
            expr = toUELExpression(expr);

            //context variables
            elContext.putContext(ContextKey.class, context);
            elContext.putContext(XWorkConverter.class, xworkConverter);
            elContext.putContext(CompoundRoot.class, root);
            elContext.putContext(ValueStack.class, this);

            // parse our expression
            ExpressionFactory factory = getExpressionFactory();
            ValueExpression valueExpr = factory.createValueExpression(elContext, expr, Object.class);

            //eval expression
            Object retVal = valueExpr.getValue(elContext);

            if (!Object.class.equals(asType)) {
                retVal = xworkConverter.convertValue(context, root, null, null, retVal, asType);
            }
            return retVal;
        } catch (Exception e) {
            return handleException(e, originalExpression, throwException);
        }
    }

    private String toUELExpression(String expr) {
        if (expr != null && !expr.startsWith("${") && !expr.startsWith("#{")) {
            expr = "#{" + expr + "}";
        }
        return expr;
    }

    private Object handleException(Exception exception, String expression, boolean throwException) {
        Object ret = context.get(expression);

        if (ret != null)
            return ret;
        else {
            if (exception instanceof PropertyNotFoundException && devMode && logMissingProperties)
                LOG.warn(exception.getMessage());

            if (throwException)
                throw new XWorkException(exception);
            else
                return null;
        }

    }

    protected ExpressionFactory getExpressionFactory() {
        ExpressionFactory factory = ExpressionFactoryHolder.getExpressionFactory();
        if (factory == null) {
            String message = "********** FATAL ERROR STARTING UP STRUTS-UEL INTEGRATION **********\n" +
                    "Looks like the UEL listener was not configured for your web app! \n" +
                    "Nothing will work until UELServletContextListener is added as a listener in web.xml.\n" +
                    "You might need to add the following to web.xml: \n" +
                    "    <listener>\n" +
                    "        <listener-class>org.apache.struts2.uelplugin.UELServletContextListener</listener-class>\n" +
                    "    </listener>";
            LOG.fatal(message);
            throw new IllegalStateException("Unable to find ExpressionFactory instance. Make sure that 'UELServletContextListener' " +
                    "is configured in web.xml as a listener");
        } else
            return factory;
    }

    public Map getContext() {
        return context;
    }

    public Map getExprOverrides() {
        return overrides;
    }

    public CompoundRoot getRoot() {
        return root;
    }

    public Object peek() {
        return root.peek();
    }

    public Object pop() {
        return root.pop();
    }

    public void push(Object o) {
        root.push(o);
    }

    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    public void setExprOverrides(Map overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    public void set(String key, Object o) {
        overrides.put(key, o);
    }

    public void setValue(String expr, Object value) {
        setValue(expr, value, false);
    }

    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        try {
            expr = toUELExpression(expr);

            //context variables
            elContext.putContext(ContextKey.class, context);
            elContext.putContext(XWorkConverter.class, xworkConverter);
            elContext.putContext(CompoundRoot.class, root);
            elContext.putContext(ValueStack.class, this);

            // parse our expression
            ExpressionFactory factory = getExpressionFactory();
            ValueExpression valueExpr = factory.createValueExpression(elContext, expr, Object.class);
            valueExpr.setValue(elContext, value);
        } catch (Exception e) {
            if (e instanceof PropertyNotFoundException && devMode && logMissingProperties)
                LOG.warn("Could not find property [" + e.getMessage() + "]");

            if (throwExceptionOnFailure)
                throw new XWorkException(e);
        }
    }

    public int size() {
        return root.size();
    }

    protected void setRoot(CompoundRoot root) {
        this.context = new HashMap();
        context.put(VALUE_STACK, this);
        this.root = root;
        elContext = new CompoundRootELContext(container);
    }

    public void clearContextValues() {
        getContext().clear();
    }
}
