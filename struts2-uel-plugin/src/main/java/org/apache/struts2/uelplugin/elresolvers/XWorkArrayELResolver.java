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
package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.lang.reflect.Array;
import java.util.Map;


public class XWorkArrayELResolver extends AbstractELResolver {
    public XWorkArrayELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        if (target != null && property != null && target.getClass().isArray()) {

            Map<String, Object> valueStackContext = getValueStackContext(elContext);

            Class lastClass = (Class) valueStackContext.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) valueStackContext.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer index = null;

            if (property instanceof Number)
                index = ((Number) property).intValue();
            else {
                try {
                    index = Integer.valueOf(property.toString());
                } catch (NumberFormatException e) {
                    //ignore
                }
            }

            if (index != null) {
                if (ReflectionContextState.isCreatingNullObjects(valueStackContext) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {
                    Class clazz = target.getClass().getComponentType();

                    if (index < 0 || index >= Array.getLength(target)) {
                        //nothing to do here, as we cannot set a new array
                        throw new ELException("Index [" + index + "] is out of bounds");
                    } else {
                        //valid index
                        Object obj = Array.get(target, index);
                        if (obj == null) {
                            try {
                                obj = objectFactory.buildBean(clazz, valueStackContext);
                                Array.set(target, index, obj);
                            } catch (Exception e) {
                                throw new ELException("unable to instantiate a new object for property [" + lastProperty + "]", e);
                            }
                        }

                        elContext.setPropertyResolved(true);
                        return obj;
                    }
                } else {
                    //try normal list
                    if (index < Array.getLength(target)) {
                        elContext.setPropertyResolved(true);
                        return Array.get(target, index);
                    }
                }
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        if (target != null && property != null && target.getClass().isArray()) {

            Map<String, Object> valueStackContext = getValueStackContext(elContext);

            Class lastClass = (Class) valueStackContext.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) valueStackContext.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer index = null;

            if (property instanceof Number)
                index = ((Number) property).intValue();
            else {
                try {
                    index = Integer.valueOf(property.toString());
                } catch (NumberFormatException e) {
                    //ignore
                }
            }

            Class clazz = target.getClass().getComponentType();

            if (index != null) {
                if (ReflectionContextState.isCreatingNullObjects(valueStackContext) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {

                    if (index < 0 || index >= Array.getLength(target)) {
                        //nothing to do here, as we cannot set a new array
                        throw new ELException("Index [" + index + "] is out of bounds");
                    } else {
                        //valid index
                        Object convertedValue = xworkConverter.convertValue(valueStackContext, value, clazz);
                        Array.set(target, index, convertedValue);
                        elContext.setPropertyResolved(true);
                    }
                }
            } else {
                //try normal list
                if (index < Array.getLength(target)) {
                    Object convertedValue = xworkConverter.convertValue(valueStackContext, value, clazz);
                    Array.set(target, index, convertedValue);
                    elContext.setPropertyResolved(true);
                }
            }
        }
    }
}
