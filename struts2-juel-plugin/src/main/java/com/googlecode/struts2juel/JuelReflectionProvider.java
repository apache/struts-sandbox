package com.googlecode.struts2juel;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionException;

/**
 * A OgnlReflectionProvider based on Juel.
 */
public class JuelReflectionProvider extends OgnlReflectionProvider {
	private ExpressionFactory factory;

	public void initExpressionFactory() {
		if (factory == null) {
			factory = ExpressionFactoryLocator.locateExpressFactory();
		}
	}
	
    @Override
    public Object getValue(String expr, Map context, Object root) throws ReflectionException {
    	initExpressionFactory();
        CompoundRoot compoundRoot = new CompoundRoot();
        compoundRoot.add(root);
        ELContext elContext = new CompoundRootELContext(compoundRoot);
        // parse our expression
        ValueExpression valueExpr = factory.createValueExpression(elContext,
            expr, String.class);
        return (String) valueExpr.getValue(elContext);
    }

    @Override
    public void setValue(String expr, Map context, Object root, Object value) throws ReflectionException {
    	initExpressionFactory();
        CompoundRoot compoundRoot = new CompoundRoot();
        compoundRoot.add(root);
        ELContext elContext = new CompoundRootELContext(compoundRoot);
        // parse our expression
        ValueExpression valueExpr = factory.createValueExpression(elContext,
            expr, String.class);
        valueExpr.setValue(elContext, value);
    }
}
