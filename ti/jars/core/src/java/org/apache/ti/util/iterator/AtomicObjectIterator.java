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
import java.util.NoSuchElementException;

/**
 */
public class AtomicObjectIterator
        implements Iterator {

    /**
     * The object that should be wrapped in the iterator
     */
    private Object _object;

    /**
     * A boolean to track if the single object in this iterator has been returned
     */
    private boolean _nextCalled = false;

    AtomicObjectIterator(Object object) {
        _object = object;
    }

    public boolean hasNext() {
        if (_nextCalled || _object == null)
            return false;
        else
            return true;
    }

    public Object next() {
        if (!_nextCalled && _object != null) {
            _nextCalled = true;
            return _object;
        } else
            throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));
    }

    public void remove() {
        throw new UnsupportedOperationException(Bundle.getErrorString("IteratorFactory_Iterator_removeUnsupported", new Object[]{this.getClass().getName()}));
    }
}
