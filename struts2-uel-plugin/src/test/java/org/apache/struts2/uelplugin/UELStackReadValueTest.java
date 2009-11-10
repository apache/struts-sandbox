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
package org.apache.struts2.uelplugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class UELStackReadValueTest extends AbstractUELTest {

    public void testPrivateMethod() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        obj.setValue("private");
        assertNull(stack.findValue("somePrivateField"));
        assertNull(stack.findValue("top.somePrivateField()"));
    }

    public void testProtectedMethod() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        obj.setValue("protected");
        assertNull(stack.findValue("someProtectedField"));
        assertNull(stack.findValue("top.someProtectedField()"));
    }

    public void testPrivateField() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        obj.setValue("private");
        assertNull(stack.findValue("privateField"));
    }

    public void testProtectedField() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        obj.setValue("protected");
        assertNull(stack.findValue("protectedField"));
    }


    public void testGetterWithoutSetter() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("getter");
        root.push(obj);

        assertEquals("getter", stack.findValue("propWithoutSetter"));
    }

    public void testTop() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("0");
        root.push(obj);

        obj = new TestObject();
        obj.setValue("1");
        root.push(obj);

        assertEquals("1", stack.findValue("top.value"));
    }

    public void testReadList() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.push(obj);

        //list
        List someList = new ArrayList(3);
        obj.setObject(someList);
        someList.add(10);
        assertEquals(10, stack.findValue("object[0]"));
    }

    public void testReadArray() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        int[] ints = {10, 20};
        obj.setTypedArray(ints);
        root.push(obj);

        //list
        assertEquals(20, stack.findValue("typedArray[1]"));
    }


    public void testReadMap() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        HashMap map = new HashMap();
        map.put("nameValue", "Lex");
        TestObject obj = new TestObject();
        obj.setParameters(map);
        root.add(obj);

        assertEquals("Lex", stack.findValue("parameters.nameValue"));
        assertEquals("Lex", stack.findValue("parameters['nameValue']"));
    }


    public void testContextReferencesWithSameObjectInStack() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //if there as object in the stack with the property "X" and there is an object in the
        //stack context under the key "X" then:
        //${X} : should return the value of X from object in stack
        //${#X} : should return object from the stack context

        TestObject obj = new TestObject();
        obj.setValue("ref");
        stack.push(obj);

        TestObject obj2 = new TestObject();
        stack.getContext().put("value", obj2);

        //simple
        assertEquals("ref", stack.findValue("value"));
        assertSame(obj2, stack.findValue("#value"));

    }

    public void testExpressionSyntax() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("val");
        obj.setAge(1);
        stack.getContext().put("obj", obj);

        assertEquals("val", stack.findValue("${#obj.value}"));
        assertEquals("val", stack.findValue("%{#obj.value}"));
        assertEquals("val", stack.findValue("#{#obj.value}"));
    }

    public void testSuperNested() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj0 = new TestObject("0");
        root.push(obj0);

        TestObject obj1 = new TestObject("1");
        obj0.setInner(obj1);

        TestObject obj2 = new TestObject("2");
        Map map = new HashMap();
        map.put("key0", obj2);
        obj1.setParameters(map);

        TestObject obj3 = new TestObject("3");
        List list = new ArrayList();
        list.add(obj3);
        obj2.setObject(obj3);

        TestObject obj4 = new TestObject("4");
        TestObject[] array = new TestObject[]{obj4};
        obj3.setObject(array);

        stack.getContext().put("obj", obj0);

        assertEquals("4", stack.findValue("${inner.parameters['key0'].object.object[0].value}"));
    }

    public void testContextReferences() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("val");
        obj.setAge(1);
        stack.getContext().put("obj", obj);

        //simple
        assertSame(obj, stack.findValue("#obj"));
        assertEquals("val", stack.findValue("#obj.value"));

        //more expressions
        TestObject obj2 = new TestObject();
        obj2.setValue("val2");
        obj2.setAge(2);
        stack.getContext().put("obj2", obj2);

        //addition
        assertSame(3L, stack.findValue("#obj.age + #obj2.age"));

        //string addition
        assertEquals("valval2", stack.findValue("#obj.value + #obj2.value"));
        assertEquals("1val2", stack.findValue("#obj.age + #obj2.value"));

        //map
        Map someMap = new HashMap();
        obj.setInner(obj2);
        someMap.put("val", obj);
        stack.getContext().put("map", someMap);
        assertEquals("val", stack.findValue("#map[#obj.value].value"));

        //list
        List someList = new ArrayList(3);
        obj.setAge(0);
        someList.add(obj);
        stack.getContext().put("list", someList);
        assertEquals("val", stack.findValue("#list[#obj.age].value"));
    }

    public void testBasicFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);
        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${value}");
        assertEquals("Hello World", value);

        stack.setValue("${age}", "56");
        Integer age = (Integer) stack.findValue("${age}");
        assertEquals(56, (int) age);
    }

    public void testNestedFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject obj2 = new TestObject();
        obj2.setAge(100);
        obj.setInner(obj2);
        root.add(obj);

        assertSame(obj2, stack.findValue("${inner}"));
        assertEquals(100, stack.findValue("${inner.age}"));
    }

    public void testDeferredFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("#{value}", "Hello World");
        String value = stack.findString("#{value}");
        assertEquals("Hello World", value);

        stack.setValue("#{age}", "56");
        String age = stack.findString("#{age}");
        assertEquals("56", age);

        stack.setValue("#{date}", new Date());
        assertEquals(stack.findString("#{date}"), format.format(obj.getDate()));
    }

    public void testNotFound() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${VALUENOTHERE}");
        assertNull(value);

        value = stack.findString("VALUENOTHERE");
        assertNull(value);
    }
}
