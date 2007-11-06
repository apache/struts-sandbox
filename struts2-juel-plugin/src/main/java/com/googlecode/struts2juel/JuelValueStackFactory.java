package com.googlecode.struts2juel;

import javax.el.ExpressionFactory;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Creates JuelValueStacks.
 */
public class JuelValueStackFactory implements ValueStackFactory {
	private ExpressionFactory factory;

	public void initExpressionFactory() {
		if (factory == null) {
			factory = ExpressionFactoryLocator.locateExpressFactory();
		}
	}
	
    public ValueStack createValueStack() {
    	initExpressionFactory();
        return new JuelValueStack(factory);
    }

    public ValueStack createValueStack(ValueStack stack) {
    	initExpressionFactory();
        return new JuelValueStack(factory, stack);
    }
}
