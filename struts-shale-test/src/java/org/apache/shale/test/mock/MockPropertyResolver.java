/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.test.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * <p>Mock implementation of <code>PropertyResolver</code>.</p>
 *
 * <p>This implementation supports only getValue() and setValue(), to the
 * extent that <code>PropertyUtils.getSimpleProperty()</code> and
 * <code>PropertyUtils.setSimpleProperty()</code> execute correctly.</p>
 *
 * $Id$
 */

public class MockPropertyResolver extends PropertyResolver {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a default instance.</p>
     */
    public MockPropertyResolver() {
    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // ------------------------------------------------ PropertyResolver Methods


    public Object getValue(Object base, Object property)
        throws EvaluationException, PropertyNotFoundException {

        if (base == null) {
            throw new NullPointerException();
        }
	String name = property.toString();
        try {
            if (base instanceof Map) {
                Map map = (Map) base;
                if (map.containsKey(name)) {
                    return map.get(name);
                } else {
                    throw new PropertyNotFoundException(name);
                }
            } else {
                return PropertyUtils.getSimpleProperty(base, name);
            }
        } catch (IllegalAccessException e) {
            throw new EvaluationException(e);
        } catch (InvocationTargetException e) {
            throw new EvaluationException(e.getTargetException());
        } catch (NoSuchMethodException e) {
            throw new PropertyNotFoundException(name);
        }

    }


    public Object getValue(Object base, int index)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public void setValue(Object base, Object property, Object value)
        throws PropertyNotFoundException {

        if (base == null) {
            throw new NullPointerException();
        }
	String name = property.toString();
        try {
            if (base instanceof Map) {
                ((Map) base).put(name, value);
            } else {
                PropertyUtils.setSimpleProperty(base, name, value);
            }
        } catch (IllegalAccessException e) {
            throw new EvaluationException(e);
        } catch (InvocationTargetException e) {
            throw new EvaluationException(e.getTargetException());
        } catch (NoSuchMethodException e) {
            throw new PropertyNotFoundException(name);
        }

    }


    public void setValue(Object base, int index, Object value)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public boolean isReadOnly(Object base, Object property)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public boolean isReadOnly(Object base, int index)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public Class getType(Object base, Object property)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public Class getType(Object base, int index)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


}
