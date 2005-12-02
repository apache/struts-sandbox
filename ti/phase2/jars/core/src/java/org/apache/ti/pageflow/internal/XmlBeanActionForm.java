/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.pageflow.internal;

import org.apache.ti.util.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XmlBeanActionForm {

    private static final Logger _log = Logger.getInstance(XmlBeanActionForm.class);

    private Object _bean;
    private String _xmlBeanInterfaceClassName;


    public XmlBeanActionForm(Object xml, String formClassName) {
        setBean(xml);
        _xmlBeanInterfaceClassName = formClassName;
    }

    public Object getBean() {
        return _bean;
    }

    public void setBean(Object bean) {
        _bean = bean;
    }

    public String getXmlString() {
        Object xmlBean = getBean();

        if (xmlBean == null) return null;

        try {
            return (String) xmlBean.getClass().getMethod("xmlText", (Class[]) null).invoke(xmlBean, (Object[]) null);
        } catch (InvocationTargetException e) {
            _log.error("Error while getting XML String", e.getCause());
        } catch (Exception e) {
            assert e instanceof NoSuchMethodException || e instanceof IllegalAccessException : e.getClass().getName();
            _log.error("Error while getting XML String", e);
        }

        return null;
    }

    public void setXmlString(String xml) {
        setBean(invokeFactoryMethod("parse", new Class[]{String.class}, new Object[]{xml}));
    }

    public void reset() {
        if (getBean() == null) {
            setBean(invokeFactoryMethod("newInstance", new Class[0], new Object[0]));
        }
    }

    private Object invokeFactoryMethod(String methodName, Class[] argTypes, Object[] args) {
        String factoryClassName = _xmlBeanInterfaceClassName + "$Factory";

        try {
            Class factoryClass = Class.forName(factoryClassName);
            Method newInstanceMethod = factoryClass.getMethod(methodName, argTypes);
            return newInstanceMethod.invoke(factoryClass, args);
        } catch (Exception e) {
            // Can be any exception -- not just the reflection-related exceptions...
            // because the exception could be thrown while creating the XML bean.
            if (_log.isErrorEnabled()) {
                _log.error("Error while creating XML object of type " + _xmlBeanInterfaceClassName, e);
            }

            return null;
        }
    }
}
