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
package org.apache.ti.util.internal.cache;

import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;
import org.apache.ti.util.logging.Logger;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The PropertyCache is used to track the JavaBean properties and public
 * fields of a set of classes that are stored in the cache.  This implementation
 * provides a significant speed-up when looking-up reflected metadata
 * of Java classes.  It is primarily used in the NetUI expression engine
 * to provide fast access to the properties and fields of classes
 * against which expressions are being evaluated.
 */
public final class PropertyCache {

    private static final Logger LOGGER = Logger.getInstance(PropertyCache.class);

    private final InternalConcurrentHashMap _classCache;

    public PropertyCache() {
        _classCache = new InternalConcurrentHashMap();
    }

    /**
     * Get an array of {@link java.beans.PropertyDescriptor} objects that
     * describe JavaBean properties of the given <code>_type</code>.  This
     * array <b>should not</b> be modified.
     *
     * @param type the {@link java.lang.Class} whose JavaBean properties to find
     * @return an array of {@link java.beans.PropertyDescriptor} objects that describe the JavaBean properties
     */
    public final PropertyDescriptor[] getPropertyDescriptors(Class type) {
        CachedClass cc = getCachedClass(type);
        return (cc != null ? cc.getPropertyDescriptors() : null);
    }

    public final Method getPropertyGetter(Class type, String property) {
        CachedClass cc = getCachedClass(type);
        if (cc == null)
            return null;
        CachedProperty cp = cc.getProperty(property);
        return (cp != null ? cp.getReadMethod() : null);
    }

    public final Method getPropertySetter(Class type, String property) {
        CachedClass cc = getCachedClass(type);
        if (cc == null)
            return null;
        CachedProperty cp = cc.getProperty(property);
        return (cp != null ? cp.getWriteMethod() : null);
    }

    public final Class getPropertyType(Class type, String property) {
        CachedClass cc = getCachedClass(type);
        if (cc == null)
            return null;
        CachedProperty cp = cc.getProperty(property);
        return (cp != null ? cp.getType() : null);
    }

    private final CachedClass getCachedClass(Class type) {
        Object obj = _classCache.get(type);
        if (obj == null) {
            try {
                obj = new CachedClass(type);
                _classCache.put(type, obj);
            } catch (Exception e) {
                LOGGER.error("Error introspecting a class of _type \"" + type + "\" when determining its JavaBean property info", e);
                return null;
            }
        }

        return (CachedClass) obj;
    }

    /**
     *
     */
    private class CachedClass {

        private Class _type = null;
        private HashMap _properties = null;
        private PropertyDescriptor[] _propertyDescriptors = null;

        CachedClass(Class type)
                throws IntrospectionException {
            this._type = type;
            init(type);
        }

        private void init(Class type)
                throws IntrospectionException {
            _properties = new HashMap();

            if (Modifier.isPublic(type.getModifiers())) {
                PropertyDescriptor[] pds = Introspector.getBeanInfo(type).getPropertyDescriptors();
                for (int i = 0; i < pds.length; i++) {
                    _properties.put(pds[i].getName(), new CachedProperty(pds[i]));
                }
            }
            // not looking at a public class, get all of the JavaBean PDs off of its interfaces
            else {
                // look on the public interfaces on this class and all superclasses
                for (Class c = type; c != null; c = c.getSuperclass()) {
                    Class[] interfaces = c.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        Class iface = interfaces[i];
                        if (Modifier.isPublic(iface.getModifiers())) {
                            PropertyDescriptor[] pds = Introspector.getBeanInfo(iface).getPropertyDescriptors();
                            for (int j = 0; j < pds.length; j++) {
                                if (!_properties.containsKey(pds[j].getName()))
                                    _properties.put(pds[j].getName(), new CachedProperty(pds[j]));
                            }
                        }
                    }
                }

                // look on the nearest public base class
                Class baseClass = type.getSuperclass();
                while (!Modifier.isPublic(baseClass.getModifiers())) {
                    baseClass = baseClass.getSuperclass();
                }

                PropertyDescriptor[] pds = Introspector.getBeanInfo(baseClass).getPropertyDescriptors();
                for (int j = 0; j < pds.length; j++) {
                    if (!_properties.containsKey(pds[j].getName()))
                        _properties.put(pds[j].getName(), new CachedProperty(pds[j]));
                }
            }

            if (_properties.size() > 0) {
                _propertyDescriptors = new PropertyDescriptor[_properties.size()];
                Iterator iterator = _properties.values().iterator();
                for (int i = 0; iterator.hasNext(); i++) {
                    _propertyDescriptors[i] = ((CachedProperty) iterator.next()).getPropertyDescriptor();
                }
            }
        }

        PropertyDescriptor[] getPropertyDescriptors() {
            return _propertyDescriptors;
        }

        CachedProperty getProperty(String name) {
            return (CachedProperty) _properties.get(name);
        }
    }

    /**
     *
     */
    private class CachedProperty {

        private Method _readMethod = null;
        private Method _writeMethod = null;
        private String _name = null;
        private PropertyDescriptor _pd = null;
        private Class _type = null;

        CachedProperty(PropertyDescriptor pd) {
            _pd = pd;
            _name = pd.getName();
            _readMethod = pd.getReadMethod();
            _writeMethod = pd.getWriteMethod();
            _type = pd.getPropertyType();
        }

        PropertyDescriptor getPropertyDescriptor() {
            return _pd;
        }

        Method getReadMethod() {
            return _readMethod;
        }

        Method getWriteMethod() {
            return _writeMethod;
        }

        String getName() {
            return _name;
        }

        Class getType() {
            return _type;
        }
    }
}
