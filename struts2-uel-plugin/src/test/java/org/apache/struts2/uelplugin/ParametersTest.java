package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;


public class ParametersTest extends AbstractUelBaseTest {
    public void testWriteList() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //not null
        List list = new ArrayList();
        TestObject obj = new TestObject();
        obj.setList(list);
        assertNotNull(obj.getList());
        root.push(obj);

        stack.setValue("list[0]", "val");
        assertEquals(1, list.size());
        assertEquals("val", list.get(0));

        //null list
        obj.setList(null);
        assertNull(obj.getList());
        stack.setValue("list[0]", "val");
        assertNotNull(obj.getList());
        assertEquals("val", stack.findValue("list[0]"));

        //test out of index
        obj.setList(null);
        stack.setValue("list[3]", "val");
        assertEquals(4, obj.getList().size());
        assertEquals("val", obj.getList().get(3));

        //test type determiner
        obj.setTypedList(null);
        stack.setValue("typedList[1].value", "val");
        assertEquals(2, obj.getTypedList().size());
        assertEquals("val", obj.getTypedList().get(1).getValue());
    }

    public void testAnnotatedTypeConverter() {
        TestAction action = new TestAction();
        assertNull(action.getConverted());
        root.push(action);

        stack.setValue("converted", "someval");
        assertEquals("converted", action.getConverted());
    }


    public void testSetPropertiesOnNestedNullObject() {
        TestObject obj = new TestObject();
        assertNull(obj.getInner());
        root.push(obj);

        //inner is null, it will be catched bye the CompoundRoolELResolver
        stack.setValue("inner.value", "val");
        assertNotNull(obj.getInner());
        assertEquals("val", obj.getInner().getValue());


        //second nested property null
        stack.setValue("inner.inner.value", "val");
        assertNotNull(obj.getInner().getInner());
        assertEquals("val", obj.getInner().getInner().getValue());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Map context = stack.getContext();

        ReflectionContextState.setCreatingNullObjects(context, true);
        ReflectionContextState.setDenyMethodExecution(context, true);
        ReflectionContextState.setReportingConversionErrors(context, true);
    }
}
