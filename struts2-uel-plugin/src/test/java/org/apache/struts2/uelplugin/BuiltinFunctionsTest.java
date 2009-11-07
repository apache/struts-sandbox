package org.apache.struts2.uelplugin;

import java.lang.reflect.InvocationTargetException;

import org.apache.struts2.views.util.ContextUtil;


public class BuiltinFunctionsTest extends AbstractUELTest {
    public void testGetText() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestAction action = new TestAction();
        stack.push(action);
        stack.getContext().put(ContextUtil.ACTION, action);

        assertEquals("This is the key!", stack.findValue("${getText('key')}"));
    }
}
