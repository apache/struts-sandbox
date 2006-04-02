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
 *
 * @targetClass java.util.Collection
 */
public class CollectionExtensions {

    /**
     *  Iterates through the collection and for each element,
     *  calls the passed function with the element as the 
     *  parameter.
     *
     *  @funcParams Function func
     *  @funcReturn void
     *  @example list.each(function(item) { print(item) })
     */
    public static ExtensionFunction each(final Collection col) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                Function func = (Function)args[0];
                Object[] param = new Object[1];
                
                for (Iterator i = col.iterator(); i.hasNext(); ) {
                    param[0] = i.next();
                    func.call(cx, scope, thisObj, param);
                }
                return null;
            }
        };
    }
 
    /**
     *  Finds the first item selected by the passed function.  The function
     *  will receive the item and should return true or false for the 
     *  result.
     *
     *  @funcParams Function func
     *  @funcReturn Object
     *  @example item = list.find(function(item) { return item.matches(/foo[0-9]/) })
     */
    public static ExtensionFunction find(final Collection col) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                Function func = (Function)args[0];
                Object[] param = new Object[1];
                Object match = null;
                for (Iterator i = col.iterator(); i.hasNext(); ) {
                    param[0] = i.next();
                    match = func.call(cx, scope, thisObj, param);
                    if (match != null) {
                        return match;
                    }
                }
                return null;
            }
        };
    }
    
    /**
     *  Finds all items selected by the passed function.  The function
     *  will receive the item and should return true or false for the 
     *  result.  A list of all matches will be returned.
     *
     *  @funcParams Function func
     *  @funcReturn java.util.List
     *  @example matches = list.findAll(function(item) { return item.matches(/foo[0-9]/) })
     */
    public static ExtensionFunction findAll(final Collection col) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
                Function func = (Function)args[0];
                Object[] param = new Object[1];
                ArrayList found = new ArrayList();
                Object match = null;
                for (Iterator i = col.iterator(); i.hasNext(); ) {
                    param[0] = i.next();
                    match = func.call(cx, scope, thisObj, param);
                    if (match != null) {
                        found.add(match);
                    }
                }
                return found;
            }
        };
    }
    
    /**
     *  Provides the current size of the collection.  Alternative to
     *  the size() method to be more consistent with Javascript
     *  arrays.
     *
     *  @propReturn int
     *  @example list.length
     */
    public static int length(Collection col, Scriptable scope) {
        return col.size();   
    }
}
