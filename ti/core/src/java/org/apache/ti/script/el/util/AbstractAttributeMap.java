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
package org.apache.ti.script.el.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public abstract class AbstractAttributeMap
        implements Map {

    private Map _map = null;

    protected abstract Object getValue(Object key);

    protected abstract Object putValue(Object key, Object value);

    protected abstract Enumeration getKeysEnumeration();

    public void clear() {
    }

    public boolean containsKey(Object key) {
        return (getValue(key) != null);
    }

    public boolean containsValue(Object key) {
        return getMap().containsValue(key);
    }

    public Set entrySet() {
        return getMap().entrySet();
    }

    public Object get(Object key) {
        return getValue(key);
    }

    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    public Set keySet() {
        return getMap().keySet();
    }

    public Object put(Object key, Object value) {
        return putValue(key, value);
    }

    public void putAll(Map t) {
        getMap().putAll(t);
    }

    public Object remove(Object key) {
        return getMap().remove(key);
    }

    public int size() {
        return getMap().size();
    }

    public Collection values() {
        return getMap().values();
    }

    private Map getMap() {
        if (_map == null)
            _map = convertToMap();

        return _map;
    }

    private Map convertToMap() {
        if (_map == null)
            _map = new HashMap();

        Enumeration keys = getKeysEnumeration();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            _map.put(key, getValue(key));
        }

        return _map;
    }
}
