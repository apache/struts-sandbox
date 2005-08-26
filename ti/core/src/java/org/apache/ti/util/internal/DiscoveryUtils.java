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
package org.apache.ti.util.internal;

import org.apache.ti.util.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * Utility methods for discovering implementor classes on the classpath.  An implementor class is declared in the
 * standard way, in a file within META-INF/services where the file name is the class name of the desired interface, and
 * the file contains the class name of the implementation.  For example, to declare test.MyServiceImpl as a
 * test.MyService implementation, the file META-INF/services/test.MyService is put on classpath (e.g., in a JAR),
 * with contents "test.MyServiceImpl".
 */
public class DiscoveryUtils {

    private static final Logger _log = Logger.getInstance(DiscoveryUtils.class);


    /**
     * Get the ClassLoader from which implementor classes will be discovered and loaded.
     */
    public static ClassLoader getClassLoader() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) return cl;
        } catch (SecurityException e) {
            if (_log.isDebugEnabled()) {
                _log.debug("Could not get thread context classloader.", e);
            }
        }

        if (_log.isTraceEnabled()) {
            _log.trace("Can't use thread context classloader; using classloader for " + DiscoveryUtils.class.getName());
        }

        return DiscoveryUtils.class.getClassLoader();
    }

    /**
     * Get all implementor classes (on the context classpath) that implement a given interface.
     *
     * @param interfaceType the Class that represents the interface.
     * @return an array of Classes that are implementations of <code>interfaceType</code>.
     */
    public static Class[] getImplementorClasses(Class interfaceType) {
        String interfaceName = interfaceType.getName();
        ArrayList/*< Class >*/ classes = new ArrayList/*< Class >*/();
        ClassLoader classLoader = getClassLoader();

        try {
            Enumeration e = classLoader.getResources("META-INF/services/" + interfaceName);

            while (e.hasMoreElements()) {
                URL url = (URL) e.nextElement();

                if (_log.isTraceEnabled()) {
                    _log.trace("Found implementor entry for interface " + interfaceName + " at " + url);
                }

                InputStream is = null;
                String className = null;

                try {
                    is = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    className = reader.readLine().trim();
                    Class implementorClass = loadImplementorClass(className, interfaceType, classLoader);
                    if (implementorClass != null) classes.add(implementorClass);
                } catch (IOException ioe) {
                    _log.error("Could not read implementor class entry at + " + url);
                } finally {
                    if (is != null) is.close();
                }
            }
        } catch (IOException e) {
            _log.error("Could not discover implementors for " + interfaceName, e);
        }

        return (Class[]) classes.toArray(new Class[0]);
    }

    public static Object newImplementorInstance(String className, Class interfaceType) {
        Class implementorClass = loadImplementorClass(className, interfaceType);

        if (implementorClass != null) {
            try {
                return implementorClass.newInstance();
            } catch (IllegalAccessException e) {
                _log.error("Could not instantiate " + className + " for interface " + interfaceType.getName(), e);
            } catch (InstantiationException e) {
                _log.error("Could not instantiate " + className + " for interface " + interfaceType.getName(), e);
            }
        }

        return null;
    }

    /**
     * Load an implementor class from the context classloader.
     *
     * @param className     the name of the implementor class.
     * @param interfaceType the interface that the given class should implement.
     * @return the implementor Class, or <code>null</code> if an error occurred (the error will be logged).
     */
    public static Class loadImplementorClass(String className, Class interfaceType) {
        return loadImplementorClass(className, interfaceType, getClassLoader());
    }

    /**
     * Load an implementor class from the context classloader.
     *
     * @param className     the name of the implementor class.
     * @param interfaceType the interface that the given class should implement.
     * @param classLoader   the ClassLoader from which to load the implementor class.
     * @return the implementor Class, or <code>null</code> if an error occurred (the error will be logged).
     */
    private static Class loadImplementorClass(String className, Class interfaceType, ClassLoader classLoader) {
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("Trying to load implementor class for interface " + interfaceType.getName()
                        + ": " + className);
            }

            Class implementorClass = classLoader.loadClass(className);

            if (interfaceType.isAssignableFrom(implementorClass)) {
                return implementorClass;
            } else {
                _log.error("Implementor class " + className + " does not implement interface "
                        + interfaceType.getName());
            }
        } catch (ClassNotFoundException cnfe) {
            //
            // This will happen when the user class was built against an out-of-date interface.
            //
            _log.error("Could not find implementor class " + className + " for interface " + interfaceType.getName(),
                    cnfe);
        } catch (LinkageError le) {
            _log.error("Linkage error when loading implementor class " + className + " for interface "
                    + interfaceType.getName(), le);
        }

        return null;
    }
}
