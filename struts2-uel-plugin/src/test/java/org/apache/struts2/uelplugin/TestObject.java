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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TestObject {
    private String value;
    private int age;
    private Date date;
    private TestObject inner;
    private Map parameters;
    private Object object;
    private List list;
    private List<TestObject> typedList;
    private Map map;
    private Map<Integer, TestObject> typedMap;
    private Object[] objectArray;
    private int[] typedArray;
    private TestObject[] typedArray2;
    private Set set;

    private ChildTestAction childTestAction;
    private boolean invoked;

    private String privateField = "private";
    protected String protectedField = "protected";

    private void setSomePrivateField(String privateField) {
        this.value = privateField;
    }

    private String getSomePrivateField() {
        return value;
    }

    protected String getSomeProtectedField() {
        return value;
    }

    protected void setSomeProtectedField(String protectedField) {
        this.value = protectedField;
    }

    public String getPropWithoutSetter() {
        return value;
    }

    public void setPropWithoutGetter(String value) {
        this.value = value;
    }

    public boolean wasInvoked() {
        return invoked;
    }

    public ChildTestAction getChildTestAction() {
        return childTestAction;
    }

    public void setChildTestAction(ChildTestAction childTestAction) {
        this.childTestAction = childTestAction;
    }

    public void invoke() {
        this.invoked = true;
    }

    public Object getFail() {
        throw new RuntimeException("kaboom");
    }

    public TestObject[] getTypedArray2() {
        return typedArray2;
    }

    public void setTypedArray2(TestObject[] typedArray2) {
        this.typedArray2 = typedArray2;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    public Object[] getObjectArray() {
        return objectArray;
    }

    public void setObjectArray(Object[] objectArray) {
        this.objectArray = objectArray;
    }

    public int[] getTypedArray() {
        return typedArray;
    }

    public void setTypedArray(int[] typedArray) {
        this.typedArray = typedArray;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Map<Integer, TestObject> getTypedMap() {
        return typedMap;
    }

    public void setTypedMap(Map<Integer, TestObject> typedMap) {
        this.typedMap = typedMap;
    }

    public List<TestObject> getTypedList() {
        return typedList;
    }

    public void setTypedList(List<TestObject> typedList) {
        this.typedList = typedList;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public TestObject() {
    }

    public TestObject(String value) {
        this.value = value;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TestObject getInner() {
        return inner;
    }

    public void setInner(TestObject inner) {
        this.inner = inner;
    }

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
}
