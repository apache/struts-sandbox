package com.googlecode.struts2juel;

import javax.el.ExpressionFactory;

import junit.framework.TestCase;

public class ExpressionFactoryLocatorTest extends TestCase {
	public void testJuelLoad() {
		ExpressionFactory factory = ExpressionFactoryLocator
				.locateExpressFactory();
		assertNotNull(factory);
	}
}
