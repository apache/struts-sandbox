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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.ModuleRegistrationHandler;
import org.apache.ti.pageflow.handler.ReloadableClassHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.BouncyClassLoader;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.internal.cache.ClassLevelCache;
import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;
import org.apache.ti.util.logging.Logger;

import java.util.Iterator;
import java.util.Map;


public class DefaultReloadableClassHandler
        extends DefaultHandler
        implements ReloadableClassHandler {

    private static final Logger _log = Logger.getInstance(DefaultReloadableClassHandler.class);

    private BouncyClassLoader _pageFlowClassLoader = null;

    public DefaultReloadableClassHandler() {
        // This feature is disabled for now.
        /* TODO: re-add this in the future
        {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ContainerAdapter containerAdapter = AdapterManager.getContainerAdapter();
            File[] classDirs = null;
            
            // TODO: make this configurable in netui-config.xml.  You should be able to specify absolute files
            // and also context-relative paths.
            {
                String path = servletContext.getRealPath( "/WEB-INF/classes" );
                
                if ( path != null )
                {
                    File file = new File( path );
                    if ( file.isDirectory() ) classDirs = new File[]{ file };
                }
            }
            
            if ( classDirs != null && ! containerAdapter.isInProductionMode() )
            {
                _pageFlowClassLoader = new BouncyClassLoader( classDirs, contextClassLoader );
            }
        }
        */
    }

    public Object newInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return getRegisteredReloadableClassHandler().loadClass(className).newInstance();
    }

    private static Map/*< String, Class >*/ _loadedClasses = new InternalConcurrentHashMap/*< String, Class >*/();

    private static class Null {

    }

    private static Class NULL_CLASS = Null.class;

    public Class loadCachedClass(String className) {
        Class clazz = (Class) _loadedClasses.get(className);

        if (clazz != null) {
            return clazz != NULL_CLASS ? clazz : null;
        } else {
            try {
                clazz = getRegisteredReloadableClassHandler().loadClass(className);
                _loadedClasses.put(className, clazz);
                return clazz;
            } catch (ClassNotFoundException e) {
                _loadedClasses.put(className, NULL_CLASS);
                return null;
            }
        }
    }

    public Class loadClass(String className)
            throws ClassNotFoundException {
        if (_pageFlowClassLoader != null) {
            synchronized (this) {
                return _pageFlowClassLoader.loadClass(className);
            }
        } else {
            return DiscoveryUtils.getClassLoader().loadClass(className);
        }
    }

    public void reloadClasses() {
        if (_pageFlowClassLoader == null) {
            return;
        }

        synchronized (this) {
            if (_pageFlowClassLoader.isStale()) {
                _log.debug("Classes modified; bouncing classloader.");
                
                //
                // First go through the session and remove any attributes whose classes were loaded by the stale
                // classloader.
                //
                Map sessionScope = PageFlowActionContext.get().getSession();

                if (sessionScope != null) {
                    for (Iterator i = sessionScope.entrySet().iterator(); i.hasNext();) {
                        Map.Entry entry = (Map.Entry) i.next();
                        String attrName = (String) entry.getKey();
                        Object attr = entry.getValue();
                        if (attr.getClass().getClassLoader() == _pageFlowClassLoader) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("Removing session attribute " + attrName + " (" + attr
                                        + ") because its ClassLoader is being bounced.");
                            }

                            sessionScope.remove(attrName);
                        }
                    }
                }
                
                //
                // Clear all caches of methods, etc.
                //
                ClassLevelCache.clearAll();
                
                //
                // Clear out all registered modules from the ActionServlet.
                //
                ModuleRegistrationHandler mrh = Handlers.get().getModuleRegistrationHandler();
                mrh.clearRegisteredModules();
                
                //
                // Bounce the classloader.
                //
                init(getConfig(), getPreviousHandler());
            }
        }
    }

    public ClassLoader getClassLoader() {
        if (_pageFlowClassLoader != null) {
            synchronized (this) {
                return _pageFlowClassLoader;
            }
        }

        return _pageFlowClassLoader;
    }

    public boolean isReloadEnabled() {
        return _pageFlowClassLoader != null;
    }

    public ReloadableClassHandler getRegisteredReloadableClassHandler() {
        return (ReloadableClassHandler) super.getRegisteredHandler();
    }
}
