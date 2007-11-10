package com.googlecode.struts2juel;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionException;

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

	public void initExpressionFactory() {
		if (factory == null) {
			factory = ExpressionFactory.newInstance();
		}
	}
	
    @Override
    public Object getValue(String expr, Map context, Object root) throws ReflectionException {
    	initExpressionFactory();
        CompoundRoot compoundRoot = new CompoundRoot();
        compoundRoot.add(root);
        ELContext elContext = new CompoundRootELContext();
        elContext.putContext(XWorkConverter.class, xworkConverter);
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
        ELContext elContext = new CompoundRootELContext();
        elContext.putContext(XWorkConverter.class, xworkConverter);
        // parse our expression
        ValueExpression valueExpr = factory.createValueExpression(elContext,
            expr, String.class);
        valueExpr.setValue(elContext, value);
    }
}
