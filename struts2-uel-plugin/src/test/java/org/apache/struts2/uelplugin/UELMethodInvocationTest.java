package org.apache.struts2.uelplugin;

import java.lang.reflect.InvocationTargetException;


public class UELMethodInvocationTest extends AbstractUELTest {
    public void testBasicMethods() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        assertEquals("text", stack.findValue("${' text '.trim()}"));
        assertEquals(3, stack.findValue("${'123'.length()}"));
    }

    public void testMethodsWithParams() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        assertEquals('2', stack.findValue("${'123'.charAt(1)}"));
        assertEquals("123456", stack.findValue("${'123'.concat('456')}"));
    }

    public void testMethodsWithParamsAndContextReference() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        stack.getContext().put("s0", "Lex");
        stack.getContext().put("s1", "Luthor");
        assertEquals("Lex Luthor", stack.findValue("${#s0.concat(' ').concat(#s1)}"));
    }

     public void testCallMethodsOnCompundRoot() {
        //this shuld not fail as the property is defined on a parent class
        TestObject obj = new TestObject();
        root.push(obj);
        ChildTestAction childTestAction = new ChildTestAction();
        obj.setChildTestAction(childTestAction);

        assertSame(childTestAction, stack.findValue("top.getChildTestAction()", true));
    }
}
