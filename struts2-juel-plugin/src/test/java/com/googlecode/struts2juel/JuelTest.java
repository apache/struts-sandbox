package com.googlecode.struts2juel;

import java.lang.reflect.InvocationTargetException;

import javax.el.ExpressionFactory;

import junit.framework.TestCase;

import com.opensymphony.xwork2.util.CompoundRoot;

public class JuelTest extends TestCase {
    public void testBasicFind() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {
        ExpressionFactory factory = new de.odysseus.el.ExpressionFactoryImpl();

        CompoundRoot root = new CompoundRoot();
        TestObject obj = new TestObject();
        root.add(obj);
        JuelValueStack stack = new JuelValueStack(factory);
        stack.setRoot(root);
        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${value}");
        assertEquals("Hello World", value);
    }

    public void testNotFound() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {
        ExpressionFactory factory = new de.odysseus.el.ExpressionFactoryImpl();

        CompoundRoot root = new CompoundRoot();
        TestObject obj = new TestObject();
        root.add(obj);
        JuelValueStack stack = new JuelValueStack(factory);
        stack.setRoot(root);
        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${VALUENOTHERE}");
        assertNull(value);

        value = stack.findString("VALUENOTHERE");
        assertNull(value);
    }

    public class TestObject {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
