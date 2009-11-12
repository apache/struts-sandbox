/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.uel;

import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class UELStackSetValueTest extends AbstractUELTest {

    public void testPrivateMethod() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        stack.setValue("somePrivateField", "owned");
        assertNull(obj.getValue());
    }

    public void testProtectedMethod() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        stack.setValue("someProtectedField", "owned");
        assertNull(obj.getValue());
    }

    public void testPrivateField() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        stack.setValue("privateField", "owned");
        assertNull(obj.getValue());
    }

    public void testProtectedField() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        stack.setValue("protectedField", "owned");
        assertNull(obj.getValue());
    }


    public void testSetterWithoutGetter() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        assertNull(obj.getValue());
        stack.setValue("propWithoutGetter", "go");
        assertEquals("go", obj.getValue());
    }

    public void testSuperNested() {
        TestObject obj = new TestObject();
        root.push(obj);

        stack.setValue("inner.typedMap[10].inner.typedList[2].typedMap[1].value", "whoa");
        assertEquals("whoa", obj.getInner().getTypedMap().get(10).getInner().getTypedList().get(2).getTypedMap().get(1).getValue());
    }


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

        //context ref
        list = new ArrayList();
        stack.getContext().put("theTypedList", list);
        stack.setValue("#theTypedList[0]", "val");
        assertEquals(1, list.size());
        assertEquals("val", list.get(0));
    }

    public void testWriteArray() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //not null
        TestObject[] array = new TestObject[2];
        TestObject obj = new TestObject();
        obj.setTypedArray2(array);
        assertNotNull(obj.getTypedArray2());
        root.push(obj);

        stack.setValue("typedArray2[0].value", "val");
        assertNotNull(array[0]);
        assertEquals("val", ((TestObject) array[0]).getValue());
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

        //context ref
        map = new HashMap();
        stack.getContext().put("themap", map);
        stack.setValue("#themap['str']", "val");
        assertEquals(1, map.size());
        assertEquals("val", map.get("str"));
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

    public void testSetStringArray() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("${value}", new String[]{"Hello World"});
        String value = stack.findString("${value}");
        assertEquals("Hello World", value);

        stack.setValue("${age}", new String[]{"67"});
        assertEquals(new Integer(67), stack.findValue("${age}"));
    }

    public void test2LevelSet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject nestedObj = new TestObject();
        obj.setInner(nestedObj);
        root.add(obj);

        stack.setValue("${inner.age}", "66");
        assertEquals(66, obj.getInner().getAge());
    }

    public void testTypeConversion() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject inner = new TestObject();
        obj.setInner(inner);
        root.add(obj);

        stack.setValue("${age}", "22");
        assertEquals(stack.findValue("${age}"), obj.getAge());

        stack.setValue("${inner.value}", "George");
        assertEquals(stack.findValue("${inner.value}"), obj.getInner().getValue());

        stack.setValue("${inner.age}", "44");
        assertEquals(stack.findValue("${inner.age}"), obj.getInner().getAge());

        stack.setValue("${date}", new Date());
        assertEquals(stack.findString("${date}"), format.format(obj.getDate()));
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
