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
package org.apache.struts2.uel.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Taken from OGNL
 */
public class ObjectIndexedPropertyDescriptor extends PropertyDescriptor {
    private Method indexedReadMethod;
    private Method indexedWriteMethod;
    private Class propertyType;

    public ObjectIndexedPropertyDescriptor(String propertyName, Class propertyType, Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException {
        super(propertyName, null, null);
        this.propertyType = propertyType;
        this.indexedReadMethod = indexedReadMethod;
        this.indexedWriteMethod = indexedWriteMethod;
    }

    public Method getIndexedReadMethod() {
        return indexedReadMethod;
    }

    public Method getIndexedWriteMethod() {
        return indexedWriteMethod;
    }

    public Class getPropertyType() {
        return propertyType;
    }
}

