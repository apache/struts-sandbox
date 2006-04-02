/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.struts.flow.core.javascript;

import java.util.ArrayList;
import java.util.*;

import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.JavaScriptException;

/**
 * Aids in converting from Javascript objects to Java objects and vice versa.
 * @version $Id: LocationTrackingDebugger.java 280811 2005-09-14 09:53:32Z sylvain $
 */
public class ConversionHelper {

    /**
     *  Converts a JavaScript object to a HashMap
     *
     *@param  jsobject  The object to convert
     *@return           The Map
     */
    public static Map jsobjectToMap(Scriptable jsobject) {
        HashMap hash = new HashMap();
        Object[] ids = jsobject.getIds();
        for (int i = 0; i < ids.length; i++) {
            String key = ScriptRuntime.toString(ids[i]);
            Object value = jsobject.get(key, jsobject);
            if (value == Undefined.instance) {
                value = null;
            } else {
                value = jsobjectToObject(value);
            }
            hash.put(key, value);
        }
        return hash;
    }
    
    /**
     *  Converts a JavaScript object to a Java Object
     *
     *@param  obj  The JavaScript object
     *@return      The Java Object
     */
    public static Object jsobjectToObject(Object obj) {
        // unwrap Scriptable wrappers of real Java objects
        if (obj instanceof Wrapper) {
            obj = ((Wrapper) obj).unwrap();
        } else if (obj == Undefined.instance) {
            obj = null;
        }
        return obj;
    }
}

