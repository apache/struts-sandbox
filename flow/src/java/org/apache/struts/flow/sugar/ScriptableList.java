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

/**  Wrap a java.util.List for JavaScript.  */
public class ScriptableList extends JavaObjectWrapper implements Scriptable, Wrapper, Serializable {

    private List list;


    public ScriptableList() {
        this.list = list;
    }


    public ScriptableList(List list) {
        this.list = list;
        this.javaObject = javaObject;
    }


    public ScriptableList(Scriptable scope, Object javaObject, Class staticType, Map funcs) {
        super(scope, javaObject, staticType, funcs);
        if (javaObject instanceof List) {
            this.list = (List) javaObject;
        } else {
            throw new IllegalArgumentException("Passed object " + javaObject + " is not an instance of List");
        }
    }


    public String getClassName() {
        return staticType.toString();
    }


    public boolean has(int index, Scriptable start) {
        // We catch the ArrayIndexOutOfBoundsException because the parser is probably trying to retrieve
        // the value first(reading the statement left to right) then assign the value to that index... 
        // or something like that.
        try {
            return (list.get(index) != null);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }


    public Object get(int index, Scriptable start) {
        return list.get(index);
    }


    public void put(int index, Scriptable start, Object value) {
        int max = index + 1;
        if (max > list.size()) {
            for (int i = list.size(); i < index; i++) {
                list.add(i, null);
            }
            list.add(index, value);
        } else {
            list.set(index, value);
        }
    }


    public void delete(int index) {
        list.remove(index);
    }


    public Object[] getIds() {

        //TODO: optimize this :)
        Integer[] ids = new Integer[list.size()];
        for (int x = 0; x < ids.length; x++) {
            ids[x] = new Integer(x);
        }
        return ids;
    }


    public Object unwrap() {
        return this.list;
    }

}

