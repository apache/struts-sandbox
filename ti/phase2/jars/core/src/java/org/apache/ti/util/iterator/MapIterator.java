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
package org.apache.ti.util.iterator;

import org.apache.ti.util.Bundle;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link Iterator} interface for accessing the set of
 * values stored in a {@link Map}.
 */
public class MapIterator
        implements Iterator {

    /**
     * An iterator for the map's values.
     */
    private Iterator _mapIterator = null;

    /**
     * Create the {@link Iterator} for the given {@link Map}
     *
     * @param map
     */
    public MapIterator(Map map) {
        if (map == null)
            return;

        _mapIterator = map.values().iterator();
    }

    /**
     * Advance to the next value in the {@link Map}.
     *
     * @return <code>true</code> if there is a next item; <code>false</code> otherwise
     */
    public boolean hasNext() {
        if (_mapIterator == null)
            return false;
        else
            return _mapIterator.hasNext();
    }

    /**
     * Advance to the next item in the {@link Map}
     *
     * @return the next item
     * @throws NoSuchElementException if the map has no more elements
     */
    public Object next() {
        if (_mapIterator == null)
            throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));
        else
            return _mapIterator.next();
    }

    /**
     * Remove the current item in the iterator.
     */
    public void remove() {
        if (_mapIterator == null)
            throw new UnsupportedOperationException(Bundle.getErrorString("IteratorFactory_Iterator_removeUnsupported", new Object[]{this.getClass().getName()}));
        else
            _mapIterator.remove();
    }
}
