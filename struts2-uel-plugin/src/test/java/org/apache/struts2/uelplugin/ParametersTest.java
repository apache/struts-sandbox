package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
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

     public void testWriteArray() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //not null
        Object[] array = new Object[2];
        TestObject obj = new TestObject();
        //obj.setObjectArray(array);
        //assertNotNull(obj.getObjectArray());
        root.push(obj);

        stack.setValue("objectArray[0].value", "val");
        assertEquals("val", ((TestObject)array[0]).getValue());
    }

    public void testWriteMap() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //not null
        Map map = new HashMap();
        TestObject obj = new TestObject();
        obj.setMap(map);
        assertNotNull(obj.getMap());
        root.push(obj);

        stack.setValue("map['str']", "val");
        assertEquals(1, map.size());
        assertEquals("val", map.get("str"));

        //null list
        obj.setMap(null);
        assertNull(obj.getMap());
        stack.setValue("map['str']", "val");
        assertNotNull(obj.getMap());
        assertEquals("val", stack.findValue("map['str']"));

        //test type determiner
        obj.setTypedMap(null);
        stack.setValue("typedMap[1].value", "val");
        assertEquals(1, obj.getTypedMap().size());
        assertEquals("val", obj.getTypedMap().get(1).getValue());
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
