package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

import java.util.LinkedHashMap;
import java.util.Map;


public class UELValueStackOtherTests extends AbstractUELTest {

    public void testExpOverridesCanStackExpUp() throws Exception {
        Map expr1 = new LinkedHashMap();
        expr1.put("expr1", "'expr1value'");

        stack.setExprOverrides(expr1);

        assertEquals(stack.findValue("expr1"), "expr1value");

        Map expr2 = new LinkedHashMap();
        expr2.put("expr2", "'expr2value'");
        expr2.put("expr3", "'expr3value'");
        stack.setExprOverrides(expr2);

        assertEquals(stack.findValue("expr2"), "expr2value");
        assertEquals(stack.findValue("expr3"), "expr3value");
    }

    public void testArrayAsString() {
        TestObject obj = new TestObject();
        obj.setTypedArray(new int[]{1, 2});
        root.push(obj);

        assertEquals("1, 2", stack.findValue("typedArray", String.class));
    }

    public void testFailsOnExceptionWithThrowException() {
        TestObject obj = new TestObject();
        root.push(obj);
        try {
            stack.findValue("fail", true);
            fail("Failed to throw exception on EL error");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testFailsOnMissingPropertyWithThrowException() {
        TestObject obj = new TestObject();
        root.push(obj);
        try {
            stack.findValue("someprop12", true);
            fail("Failed to throw exception on EL error");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testFailsOnMissingNestedPropertyWithThrowException() {
        TestObject obj = new TestObject();
        root.push(obj);
        try {
            stack.findValue("top.someprop12", true);
            fail("Failed to throw exception on EL error");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testFailsOnMissingMethodWithThrowException() {
        TestObject obj = new TestObject();
        root.push(obj);
        try {
            stack.findValue("top.somethingweird()", true);
            fail("Failed to throw exception on EL error");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testDoesNotFailOnExceptionWithoutThrowException() {
        TestObject obj = new TestObject();
        root.push(obj);
        stack.findValue("fail", false);
        stack.findValue("fail");
    }

    public void testDoesNotFailOnInheritedPropertiesWithThrowException() {
        //this shuld not fail as the property is defined on a parent class
        TestObject obj = new TestObject();
        root.push(obj);
        ChildTestAction childTestAction = new ChildTestAction();
        obj.setChildTestAction(childTestAction);

        assertNull(childTestAction.getConverted());
        stack.findValue("childTestAction.converted", true);
    }

    public void testDoesNotFailOnInheritedMethodsWithThrowException() {
        //this shuld not fail as the property is defined on a parent class
        TestObject obj = new TestObject();
        root.push(obj);
        ChildTestAction childTestAction = new ChildTestAction();
        obj.setChildTestAction(childTestAction);

        assertNull(childTestAction.getConverted());
        stack.findValue("top.getChildTestAction().converted", true);
    }

    public void testFailsOnInheritedMethodsWithThrowException() {
        //this shuld not fail as the property is defined on a parent class
        TestObject obj = new TestObject();
        root.push(obj);
        ChildTestAction childTestAction = new ChildTestAction();
        obj.setChildTestAction(childTestAction);

        assertNull(childTestAction.getConverted());

        try {
            stack.findValue("top.getChildTestAction().converted2", true);
            fail("should have failed because of missing property");
        } catch (Exception e) {
        }
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInNonDevMode() {
        TestAction action = new TestAction();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setDevMode("false");
        stack.push(action);
        stack.setValue("bar", "3x");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInDevMode() {
        TestAction action = new TestAction();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setDevMode("true");
        stack.push(action);
        stack.setValue("bar", "3x");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testObjectSettingWithInvalidValueDoesNotCauseSetCalledWithNull() {
        TestObject obj = new TestObject();
        root.push(obj);
        ChildTestAction obj2 = new ChildTestAction();
        obj.setChildTestAction(obj2);

        stack.setValue("childTestAction", "whoa");
        assertNotNull(obj.getChildTestAction());
        assertSame(obj2, obj.getChildTestAction());
    }

}
