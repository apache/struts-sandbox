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
package org.apache.ti.script.el.tokens;

import org.apache.ti.script.el.util.ParseUtils;
import org.apache.ti.util.internal.cache.PropertyCache;
import org.apache.ti.util.logging.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class ExpressionToken {

    private static final Logger LOGGER = Logger.getInstance(ArrayIndexToken.class);

    private static final PropertyCache PROPERTY_CACHE = new PropertyCache();

    public abstract Object evaluate(Object value);

    public abstract void update(Object root, Object newValue);

    public abstract String getTokenString();


    /*
     *
     * Property Lookup
     *
     */
    protected final Object mapLookup(Map map, Object identifier) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("mapLookup: " + identifier);
        return map.get(identifier);
    }

    protected final Object beanLookup(Object bean, Object identifier) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("beanLookup: " + identifier);

        return ParseUtils.getProperty(bean, identifier.toString(), PROPERTY_CACHE);
    }

    protected final Object listLookup(List list, int index) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("listLookup: " + index);

        return list.get(index);
    }

    protected final Object arrayLookup(Object array, int index) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("arrayLookup: " + index);

        return Array.get(array, index);
    }

    protected final void mapUpdate(Map map, Object identifier, Object value) {
        Object converted = value;

        Object o = map.get(identifier);
        if (o != null) {
            Class type = o.getClass();
            value = ParseUtils.convertType(value, type);
        }

        map.put(identifier, value);
    }

    protected final void arrayUpdate(Object array, int index, Object value) {
        Object converted = value;

        Class elementType = array.getClass().getComponentType();
        if (!elementType.isAssignableFrom(value.getClass())) {
            converted = ParseUtils.convertType(value, elementType);
        }

        try {
            Array.set(array, index, converted);
        } catch (Exception e) {
            String msg = "An error occurred setting a value at index \"" + index + "\" on an array with component types \"" +
                    elementType + "\".  Cause: " + e.toString();

            if (LOGGER.isErrorEnabled()) LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    protected final void listUpdate(List list, int index, Object value) {
        Object converted = value;

        if (list.size() > index) {
            Object o = list.get(index);
            // can only convert types when there is an item in the currently requested place
            if (o != null) {
                Class itemType = o.getClass();
                converted = ParseUtils.convertType(value, itemType);
            }

            list.set(index, value);
        } else {
            // @note: not sure that this is the right thing.  Question is whether or not to insert nulls here to fill list up to "index"
            // @update: List doesn't guarantee that implementations will accept nulls.  So, we can't rely on that as a solution.
            // @update: this is an unfortunate but necessary solution...unless the List has enough elements to 
            // accomodate the new item at a particular index, this must be an error case.  The reasons are this:
            // 1) can't fill the list with nulls, List implementations are allowed to disallow them
            // 2) can't just do an "add" to the list -- in processing [0] and [1] on an empty list, [1] may get processed first. 
            //    this will go into list slot [0].  then, [0] gets processed and simply overwrites the previous because it's 
            //    already in the list
            // 3) can't go to a mixed approach because there's no metadata about what has been done and no time to build
            //    something that is apt to be complicated and exposed to the user
            // so...
            // the ultimate 8.1sp2 functionality is to simply disallow updating a value in a list that doesn't exist.  that 
            // being said, it is still possible to simply add to the list.  if {actionForm.list[42]} inserts into the 42'nd 
            // item, {actionForm.list} will just do an append on POST since there is no index specified.  this fix does 
            // not break backwards compatability because it will work on full lists and is completely broken now on empty 
            // lists, so changing this just gives a better exception message that "ArrayIndexOutOfBounds".  :)
            // 
            // September 2, 2003
            // ekoneil@bea.com
            // 
            String msg = "An error occurred setting a value at index \"" + index + "\" because the list is " +
                    (list != null ? (" of size " + list.size()) : "null") + ".  " +
                    "Be sure to allocate enough items in the List to accomodate any updates which may occur against the list.";

            if (LOGGER.isErrorEnabled()) LOGGER.error(msg);

            throw new RuntimeException(msg);
        }
    }

    protected final void beanUpdate(Object bean, Object identifier, Object value) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Update \"" + bean + "\" type property \"" + identifier + "\"");

        String id = identifier.toString();
        Class beanType = bean.getClass();
        Class propType = PROPERTY_CACHE.getPropertyType(beanType, id);
        if (propType != null) {
            try {
                if (java.util.List.class.isAssignableFrom(propType)) {
                    Method lm = PROPERTY_CACHE.getPropertyGetter(beanType, id);
                    if (lm != null) {
                        List list = (List) lm.invoke(bean, (Object[]) null);
                        applyValuesToList(value, list);
                        return;
                    }
                } else {
                    Method m = PROPERTY_CACHE.getPropertySetter(beanType, id);

                    if (m != null) {
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("Apply value to property via method: " + m);

                        Class targetType = m.getParameterTypes()[0];
                        Object converted = ParseUtils.convertType(value, targetType);

                        m.invoke(bean, new Object[]{converted});
                        return;
                    }
                }
            } catch (Exception e) {
                String msg = "Could not update proprety named \"" + id + "\" on bean of type \"" + beanType + "\".  Cause: " + e;
                if (LOGGER.isErrorEnabled())
                    LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

        String msg = "Could not update expression because a public JavaBean setter for the property \"" + identifier + "\" could not be found.";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
    }

    protected final int parseIndex(String identifier) {
        try {
            return Integer.parseInt(identifier);
        } catch (Exception e) {
            String msg = "Error performing an array look-up with the index \"" + identifier + "\". Cause: " + e;

            if (LOGGER.isDebugEnabled())
                LOGGER.debug(msg, e);

            throw new RuntimeException(msg, e);
        }
    }

    private static final void applyValuesToList(Object value, List list) {
        if (list == null) {
            String msg = "Can not add a value to a null java.util.List";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        if (value instanceof String[]) {
            String[] ary = (String[]) value;
            for (int i = 0; i < ary.length; i++) {
                list.add(ary[i]);
            }
        } else if (value instanceof String) {
            list.add(value);
        }
        // types that are not String[] or String are just set on the object
        else
            list.add(value);
    }
}
