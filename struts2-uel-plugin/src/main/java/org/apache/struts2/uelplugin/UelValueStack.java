package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import javax.el.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import org.apache.struts2.uelplugin.elresolvers.AccessorsContextKey;

/**
 * A ValueStack that uses Unified EL as the underlying Expression Language.
 */
public class UelValueStack implements ValueStack, ClearableValueStack {
    private static final Logger LOG = LoggerFactory.getLogger(UelValueStack.class);

    private CompoundRoot root = new CompoundRoot();
    private transient Map context;
    private Class defaultType;
    private Map overrides;

    private ELContext elContext;
    private Container container;
    private XWorkConverter xworkConverter;

    public UelValueStack(Container container) {
        this(container, new CompoundRoot());
    }

    public UelValueStack(Container container, ValueStack vs) {
        this(container, new CompoundRoot(vs.getRoot()));
    }

    public UelValueStack(Container container, CompoundRoot root) {
        this.container = container;
        this.xworkConverter = container.getInstance(XWorkConverter.class);
        setRoot(new CompoundRoot(root));
    }


    public String findString(String expr, boolean throwException) {
        return (String) findValue(expr, String.class);
    }

    public String findString(String expr) {
        return findString(expr, false);
    }

    public Object findValue(String expr) {
        return findValue(expr, Object.class, false);
    }

    public Object findValue(String expr, boolean throwException) {
        return findValue(expr, Object.class, false);
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
            if (expr != null && !expr.startsWith("${") && !expr.startsWith("#{")) {
                expr = "#{" + expr + "}";
            }

            elContext.putContext(AccessorsContextKey.class, context);
            elContext.putContext(XWorkConverter.class, xworkConverter);
            elContext.putContext(CompoundRoot.class, root);

            // parse our expression
            ExpressionFactory factory = getExpressionFactory();
            ValueExpression valueExpr = factory.createValueExpression(elContext, expr, Object.class);
            Object retVal = valueExpr.getValue(elContext);
            if (!Object.class.equals(asType)) {
                retVal = xworkConverter.convertValue(null, retVal, asType);
            }
            return retVal;
        } catch (PropertyNotFoundException e) {
            if (context.containsKey(originalExpression)) {
                return context.get(originalExpression);
            }
            // property not found
            return null;
        } catch (ELException e) {
            // fail silently so we don't mess things up
            return null;
        }
    }

    protected ExpressionFactory getExpressionFactory() {
        ExpressionFactory factory = ExpressionFactoryHolder.getExpressionFactory();
        if (factory == null) {
            String message = "********** FATAL ERROR STARTING UP STRUTS-UEL INTEGRATION **********\n" +
                    "Looks like the UEL listener was not configured for your web app! \n" +
                    "Nothing will work until UelServletContextListener is added as a listener in web.xml.\n" +
                    "You might need to add the following to web.xml: \n" +
                    "    <listener>\n" +
                    "        <listener-class>org.apache.struts2.uelplugin.UelServletContextListener</listener-class>\n" +
                    "    </listener>";
            LOG.fatal(message);
            throw new IllegalStateException("Unable to find ExpressionFactory instance. Make sure that 'UelServletContextListener' " +
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
            if (expr != null && !expr.startsWith("${") && !expr.startsWith("#{")) {
                expr = "#{" + expr + "}";
            }
            elContext.putContext(AccessorsContextKey.class, context);
            elContext.putContext(XWorkConverter.class, xworkConverter);
            elContext.putContext(CompoundRoot.class, root);

            // parse our expression
            ExpressionFactory factory = getExpressionFactory();
            ValueExpression valueExpr = factory.createValueExpression(elContext, expr, Object.class);
            valueExpr.setValue(elContext, value);
        } catch (ELException e) {
            if (throwExceptionOnFailure) {
                throw e;
            }
        }
    }

    public int size() {
        return root.size();
    }

    protected void setRoot(CompoundRoot root) {
        this.context = new TreeMap();
        context.put(VALUE_STACK, this);
        this.root = root;
        elContext = new CompoundRootELContext(container);
    }

    public void clearContextValues() {
        getContext().clear();        
    }
}
