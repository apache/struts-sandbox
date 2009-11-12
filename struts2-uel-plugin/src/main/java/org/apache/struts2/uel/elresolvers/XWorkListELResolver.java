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
package org.apache.struts2.uel.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.util.List;
import java.util.Map;

/**
 * Sets and gets values froma  list
 */
public class XWorkListELResolver extends AbstractELResolver {
    public XWorkListELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        if (target != null && property != null && target instanceof List) {

            Map<String, Object> valueStackContext = getValueStackContext(elContext);
            List list = (List) target;

            Class lastClass = (Class) valueStackContext.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) valueStackContext.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer numericValue = null;
            try {
                numericValue = Integer.valueOf(property.toString());
            } catch (NumberFormatException e) {
                //ignore
            }

            if (numericValue != null) {
                if (ReflectionContextState.isCreatingNullObjects(valueStackContext) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {
                    int index = numericValue.intValue();
                    int listSize = list.size();

                    /*if (lastClass == null || lastProperty == null) {
                        return super.getProperty(context, target, name);
                    }*/
                    Class beanClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, property);
                    if (listSize <= index) {
                        Object result;

                        for (int i = listSize; i < index; i++) {
                            list.add(null);
                        }
                        try {
                            list.add(index, result = objectFactory.buildBean(beanClass, valueStackContext));
                        } catch (Exception exc) {
                            throw new ELException(exc);
                        }

                        elContext.setPropertyResolved(true);
                        return result;
                    } else if (list.get(index) == null) {
                        Object result = null;
                        try {
                            list.set(index, result = objectFactory.buildBean(beanClass, valueStackContext));
                        } catch (Exception exc) {
                            throw new ELException(exc);
                        }

                        elContext.setPropertyResolved(true);
                        return result;
                    } else {
                        elContext.setPropertyResolved(true);
                        return list.get(index);
                    }
                } else {
                    //try normal list
                    if (numericValue < list.size()) {
                        elContext.setPropertyResolved(true);
                        return list.get(numericValue);
                    }
                }
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        if (target != null && property != null && target instanceof List) {
            Map<String, Object> valueStackContext = getValueStackContext(elContext);
            Class lastClass = (Class) valueStackContext.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) valueStackContext.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
            Class convertToClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, property);

            Object realValue = getRealValue(valueStackContext, value, convertToClass);

            Long numericValue = null;
            try {
                numericValue = Long.valueOf(property.toString());
            } catch (NumberFormatException e) {
                //ignore
            }

            if (numericValue != null) {
                //make sure there are enough spaces in the List to set
                List list = (List) target;
                int listSize = list.size();
                int count = numericValue.intValue();
                if (count >= listSize) {
                    for (int i = listSize; i <= count; i++) {
                        list.add(null);
                    }
                }

                ((List) target).set(numericValue.intValue(), realValue);
            }
        }
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }
}
