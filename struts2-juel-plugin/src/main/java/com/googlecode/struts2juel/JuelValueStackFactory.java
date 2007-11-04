package com.googlecode.struts2juel;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Creates JuelValueStacks.
 */
public class JuelValueStackFactory implements ValueStackFactory {

    public ValueStack createValueStack() {
        return new JuelValueStack();
    }

    public ValueStack createValueStack(ValueStack stack) {
        return new JuelValueStack(stack);
    }
}
