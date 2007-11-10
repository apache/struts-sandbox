package org.apache.struts2.uelplugin;

import javax.el.ExpressionFactory;

public class ExpressionFactoryHolder {
	private static ThreadLocal<ExpressionFactory> expressionFactoryInstance = new ThreadLocal<ExpressionFactory>();

	public static void setExpressionFactory(ExpressionFactory factory) {
		expressionFactoryInstance.set(factory);
	}

	public static ExpressionFactory getExpressionFactory() {
		try {
			return ExpressionFactory.newInstance();
		} catch (Throwable t) {
			ExpressionFactory factory = expressionFactoryInstance.get();
			if (factory == null) {
				throw new RuntimeException("Expression Factory Not Found!");
			}
			return factory;
		}
	}
}
