package org.apache.struts2.uelplugin;

import java.util.Map;
import java.lang.reflect.InvocationTargetException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * A OgnlReflectionProvider based on Unified EL.
 */
public class UelReflectionProvider extends OgnlReflectionProvider {
    private ExpressionFactory factory;
    private XWorkConverter xworkConverter;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }



    @Override
    public Object getValue(String expr, Map context, Object root) throws ReflectionException {
        try {
            return PropertyUtils.getProperty(root, expr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    @Override
    public void setValue(String expr, Map context, Object root, Object value) throws ReflectionException {
        try {
            BeanUtils.setProperty(root, expr, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected String wrap(String expr) {
        if (!StringUtils.startsWith(expr, "${") && !StringUtils.startsWith(expr, "#{")) {
            StringBuilder sb = new StringBuilder("${");
            sb.append(expr);
            sb.append("}");
            return sb.toString();
        } else
            return expr;
    }
}
