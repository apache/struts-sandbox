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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <code>ArrayIterator</code> provides an <code>Iterator</code> over a Java
 * array. <code>ArrayIterator</code> will return each element in the array
 * in the order they are stored in the array. Multidimensional arrays are
 * handled by returning a sequence of sub-arrays. So a three dimensional array
 * of integers will return a sequence of two dimensional arrays of integers.
 * This <code>Iterator</code> does not support the <code>remove()</code>
 * method.
 */
public class ArrayIterator
        implements Iterator {

    /**
     * The array object supplied to the iterator
     */
    private Object _array;

    /**
     * The total number of elements in the array
     */
    private int _totalElements;

    /**
     * The current element being accessed
     */
    private int _currentElement;

    public ArrayIterator(Object array) {
        if (array == null)
            return;

        if (!array.getClass().isArray())
            throw new IllegalStateException(Bundle.getErrorString("ArrayIterator_notAnArray"));

        _array = array;
        _totalElements = Array.getLength(_array);
    }

    public boolean hasNext() {
        if (_array == null)
            return false;
        else {
            if (_currentElement < _totalElements)
                return true;
            else
                return false;
        }
    }

    public Object next() {
        if (_currentElement >= _totalElements)
            throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));

        Object nextElement = null;
        try {
            nextElement = Array.get(_array, _currentElement);
        } catch (Exception e) {
            throw new NoSuchElementException(Bundle.getErrorString("ArrayIterator_arrayError", new Object[]{_array.getClass().getName()}));
        }

        _currentElement++;

        return nextElement;
    }

    public void remove() {
        throw new UnsupportedOperationException(Bundle.getErrorString("IteratorFactory_Iterator_removeUnsupported", new Object[]{this.getClass().getName()}));
    }
}
