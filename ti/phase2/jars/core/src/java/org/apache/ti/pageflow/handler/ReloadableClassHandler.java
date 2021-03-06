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
package org.apache.ti.pageflow.handler;


/**
 * Handler for loading and reloading classes.
 */
public interface ReloadableClassHandler
        extends Handler {

    Object newInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException;

    Class loadClass(String className)
            throws ClassNotFoundException;

    Class loadCachedClass(String className);

    void reloadClasses();

    ClassLoader getClassLoader();

    boolean isReloadEnabled();
}
