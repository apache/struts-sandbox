package com.googlecode.struts2juel;

import javax.el.ExpressionFactory;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Creates JuelValueStacks.
 */
public class JuelValueStackFactory implements ValueStackFactory {
	private ExpressionFactory factory;

	private XWorkConverter xworkConverter;

	@Inject
	public void setXWorkConverter(XWorkConverter conv) {
		this.xworkConverter = conv;
	}

	public void initExpressionFactory() {
		if (factory == null) {
			factory = ExpressionFactoryLocator.locateExpressFactory();
		}
	}

	public ValueStack createValueStack() {
		initExpressionFactory();
		return new JuelValueStack(factory, xworkConverter);
	}

	public ValueStack createValueStack(ValueStack stack) {
		initExpressionFactory();
		return new JuelValueStack(factory, xworkConverter, stack);
	}
}
