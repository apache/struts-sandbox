/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.pageflow.requeststate;

import javax.servlet.http.HttpSession;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 *
 */
public class NameService {

    private static final String NAME_SERVICE = "netui.nameService";

    private HashMap/*<String,WeakReference>*/ _nameMap;
    private int _nextValue;

    /**
     * private constructor allowing for a factory method to access NameService objects.
     */
    private NameService() {
        _nameMap = new HashMap/*<String,WeakReference>*/();
        _nextValue = 0;
    }

    /**
     * This will return the session specific instance of a NameService.  There
     * will only be a single NameService per session.
     *
     * @param session the HttpSession that contains the NameService
     * @return the NameService associated with the session.
     */
    public static NameService instance(HttpSession session) {
        // synchronize on the session so we only create a single NameService
        // within the session.
        synchronized (session) {
            NameService nameService = (NameService) session.getAttribute(NAME_SERVICE);
            if (nameService == null) {
                nameService = new NameService();
                session.setAttribute(NAME_SERVICE, nameService);
            }
            assert(nameService != null) : "Found invalid null name service";
            return nameService;
        }
    }

    /**
     * This method will create a unique name for an INameable object.  The name
     * will be unque within the session.  This will throw an IllegalStateException
     * if INameable.setObjectName has previously been called on object.
     *
     * @param namePrefix The prefix of the generated name.
     * @param object     the INameable object.
     * @throws IllegalStateException if this method is called more than once for an object
     */
    public synchronized void nameObject(String namePrefix, INameable object) {
        String name = namePrefix + Integer.toString(_nextValue++);
        object.setObjectName(name);
    }

    /**
     * This is a debug method that will set the next integer value.  This is used
     * so tests can force the name.
     *
     * @param val
     */
    public void debugSetNameIntValue(int val) {
        _nextValue = val;
    }

    /**
     * @param object
     */
    public void put(INameable object) {
        if (object == null)
            throw new IllegalStateException("object must not be null");
        String name = object.getObjectName();
        if (name == null)
            throw new IllegalStateException("object has not been named");

        _nameMap.put(name, new WeakReference(object));
    }

    /**
     * @param name
     * @return INameable
     */
    public INameable get(String name) {
        if (name == null)
            throw new IllegalStateException("name must not be null");
        WeakReference wr = (WeakReference) _nameMap.get(name);
        if (wr == null)
            return null;
        INameable object = (INameable) wr.get();
        if (object == null) {
            _nameMap.remove(name);
        }
        return object;
    }
}
