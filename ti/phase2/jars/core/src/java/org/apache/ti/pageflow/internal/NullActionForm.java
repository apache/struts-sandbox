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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


/**
 * ActionForm/Map that stores no properties and emits no errors.
 */
public class NullActionForm implements Map {

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean containsKey(Object key) {
        return false;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Object get(Object key) {
        return null;
    }

    public Object put(Object o, Object o1) {
        return null;
    }

    public Object remove(Object key) {
        return null;
    }

    public void putAll(Map map) {
    }

    public void clear() {
    }

    public Set keySet() {
        return Collections.EMPTY_SET;
    }

    public Collection values() {
        return Collections.EMPTY_LIST;
    }

    public Set entrySet() {
        return Collections.EMPTY_SET;
    }
}


