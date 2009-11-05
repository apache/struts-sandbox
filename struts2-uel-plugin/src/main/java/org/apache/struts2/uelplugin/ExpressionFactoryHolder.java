package org.apache.struts2.uelplugin;

import javax.el.ExpressionFactory;

public abstract class ExpressionFactoryHolder {
    private static ExpressionFactory expressionFactory;

    public static void setExpressionFactory(ExpressionFactory factory) {
        expressionFactory = factory;
    }

    public static ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }
}
