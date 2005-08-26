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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link java.util.Iterator} interface for an {@link java.util.Enumeration}.
 */
public class EnumerationIterator
        implements Iterator {

    private Enumeration _enum = null;

    public EnumerationIterator(Enumeration e) {
        _enum = e;
    }

    public boolean hasNext() {
        if (_enum == null)
            return false;
        else
            return _enum.hasMoreElements();
    }

    public Object next() {
        if (_enum == null || hasNext() == false)
            throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));
        else
            return _enum.nextElement();
    }

    public void remove() {
        throw new UnsupportedOperationException(Bundle.getErrorString("IteratorFactory_Iterator_removeUnsupported",
                new Object[]{this.getClass().getName()}));
    }
}
