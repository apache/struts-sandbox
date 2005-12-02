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
package org.apache.ti.script.common;

import org.apache.ti.util.iterator.IteratorFactory;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Base {@link java.util.Map} implementation that can be used by
 * clients that need to expose implicit objects to an expression
 * language through a Map.  This Map implementation is read-only.
 */
public abstract class AbstractScriptableMap
        extends AbstractMap {

    /**
     * Default implementation of a {@link java.util.Set} that can be returned by the
     * entrySet method of {@link java.util.Map}.  This implementation simply takes an
     * array of entries and implements iterator() and size().
     */
    class EntrySet
            extends AbstractSet {

        private Entry[] _entries = null;

        public EntrySet(Entry[] entries) {
            _entries = entries;
        }

        public Iterator iterator() {
            return IteratorFactory.createIterator(_entries);
        }

        public int size() {
            return _entries != null ? _entries.length : 0;
        }

        public boolean add(Object object) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection coll) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection coll) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection coll) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Default implementation of {@link java.util.Map.Entry} that handles
     * key / value pairs in a very basic way.  This is meant as a convenience
     * to subclasses that need to provide an entrySet() to satisfy the
     * {@link java.util.AbstractMap} contract.
     */
    class Entry
            implements Map.Entry {

        private final Object _key;
        private final Object _value;

        Entry(Object key, Object value) {
            _key = key;
            _value = value;
        }

        public Object getKey() {
            return _key;
        }

        public Object getValue() {
            return _value;
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            return ((_key == null ? 0 : _key.hashCode()) ^ (_value == null ? 0 : _value.hashCode()));
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Map.Entry))
                return false;

            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if ((key == null || (key != null && key.equals(_key))) &&
                    (value == null || (value != null && value.equals(_value))))
                return true;

            return false;
        }
    }
}
