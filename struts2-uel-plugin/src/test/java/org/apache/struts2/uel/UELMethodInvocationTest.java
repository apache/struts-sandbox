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

import java.lang.reflect.InvocationTargetException;


public class UELMethodInvocationTest extends UELBaseTest {
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
