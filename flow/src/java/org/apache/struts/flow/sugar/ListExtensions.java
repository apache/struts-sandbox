/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.struts.flow.sugar;

import org.mozilla.javascript.*;

import java.util.*;
import java.io.Serializable;

/**
 * Adds various function extensions to java.util.List implementations.
 * @targetClass java.util.List
 */
public class ListExtensions {

   
     /**
     *  Returns an immutable version of this list.
     *
     *  @funcParams 
     *  @funcReturn java.util.List
     *  @example frozenList = list.asImmutable()
     */
    public static ExtensionFunction asImmutable(final List list) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                return Collections.unmodifiableList(list);
            }
        };
    }
    
     /**
     *  Returns a synchronized version of this list.
     *
     *  @funcParams 
     *  @funcReturn java.util.List
     *  @example multiThreadList = list.asSynchronized()
     */
    public static ExtensionFunction asSynchronized(final List list) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                return Collections.synchronizedList(list);
            }
        };
    }
    
     /**
     *  Pops the last item off the list.  The last item will be returned and removed
     *  from the list.
     *
     *  @funcParams 
     *  @funcReturn Object
     *  @example lastItem = list.pop()
     */
    public static ExtensionFunction pop(final List list) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                Object o = null;
                if (list.size() > 0) {
                    o = list.get(list.size() - 1);
                    list.remove(list.size() - 1);
                }
                return o;
            }
        };
    }
    
     /**
     *  Sorts the list according to the natural order.
     *
     *  @funcParams 
     *  @funcReturn java.util.List
     *  @example sortedList = list.sort()
     */
    public static ExtensionFunction sort(final List list) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                Collections.sort(list);
                return null;
            }
        };
    }
    
     /**
     *  Sorts the list using the passed function to determine order.  The function will receive
     *  two parameters, and should return &gt; 0 if the first is greater, &lt; 0 if the first
     *  is less, and 0 if equal.
     *
     *  @funcParams Function func
     *  @funcReturn java.util.List
     *  @example sortedList = list.sort(function(val1, val2) { return val1.compareTo(val2) })
     */
    public static ExtensionFunction sortEach(final List list) {
        return new ExtensionFunction() {    
            public Object execute(final Context cx, final Scriptable scope, final Scriptable thisObj, java.lang.Object[] args) {
                final Object[] params = new Object[2];
                final Function func = (Function)args[0];
                Comparator comp = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        params[0] = o1;
                        params[1] = o2;
                        Object result = func.call(cx, scope, thisObj,params);
                        if (result instanceof Number) {
                            return ((Number)result).intValue();
                        } else {
                            throw new RuntimeException("Invalid sorting function - should return a number.  Returned "+result);
                        }
                    }
                    
                    public boolean equals(Object o) {return false;}
                };
                
                Collections.sort(list, comp);
                return list;
            }
        };
    }
}
