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

import com.opensymphony.xwork.ActionContext;
import org.apache.commons.chain.web.WebContext;
import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.DefaultContainerAdapter;
import org.apache.ti.pageflow.adapter.Adapter;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.logging.Logger;


/**
 * @exclude
 */
public class AdapterManager {

    private static final Logger _log = Logger.getInstance(AdapterManager.class);

    private static final String SERVLET_CONTAINER_ADAPTER_ATTR = InternalConstants.ATTR_PREFIX + "servletAdapter";
    private static final String SERVLET_CONTAINER_ADAPTER_PROP = "beehive.servletcontaineradapter";

    public static ContainerAdapter getContainerAdapter() {
        ActionContext actionContext = ActionContext.getContext();
        ContainerAdapter adapter = (ContainerAdapter) actionContext.getApplication().get(SERVLET_CONTAINER_ADAPTER_ATTR);
        assert adapter != null : "ContainerAdapter manager not initialized correctly.";
        return adapter;
    }

    public static ContainerAdapter init(WebContext webContext) {
        ContainerAdapter containerAdapter = createServletContainerAdapter(webContext);
        webContext.getApplicationScope().put(SERVLET_CONTAINER_ADAPTER_ATTR, containerAdapter);
        return containerAdapter;
    }

    // TODO: this method could move to a more general place.
    private static Adapter tryAdapter(WebContext webContext, Class adapterClass) {
        try {
            Adapter sa = (Adapter) adapterClass.newInstance();

            try {
                if (sa.accept(webContext)) {
                    _log.info("Adapter " + adapterClass.getName() + " accepted.");
                    sa.initialize(webContext);
                    return sa;
                } else {
                    _log.info("Adapter " + adapterClass.getName() + " is present but did not accept.");
                }
            } catch (Exception e) {
                _log.error(adapterClass.getName() + ".accept() threw an exception.", e);
            } catch (LinkageError e) {
                _log.error(adapterClass.getName() + ".accept() caused a linkage error and may be out of date.", e);
            }
        } catch (InstantiationException e) {
            _log.error("Could not create instance of Adapter class " + adapterClass.getName(), e);
        } catch (IllegalAccessException e) {
            _log.error("Could not create instance of Adapter class " + adapterClass.getName(), e);
        } catch (Exception e) {
            _log.error("Error creating instance of Adapter class " + adapterClass.getName(), e);
        }

        return null;
    }

    /*
    private static Class loadClass( ClassLoaders classLoaders, String className, Class spiClass )
    {
        for ( int i = 0; i < classLoaders.size(); ++i )
        {
            try
            {
                return classLoaders.get( i ).loadClass( className );
            }
            catch ( ClassNotFoundException e )
            {
                // ignore
            }
        }
        
        _log.error( "Could not load class " + className + " to implement " + spiClass.getName() );
        return null;
    }
    */
    
    private static ContainerAdapter createServletContainerAdapter(WebContext webContext) {
        String adapterClassName = System.getProperty(SERVLET_CONTAINER_ADAPTER_PROP);

        if (adapterClassName != null) {
            Class adapterClass =
                    DiscoveryUtils.loadImplementorClass(adapterClassName, ContainerAdapter.class);

            if (adapterClass != null) {
                ContainerAdapter sa = (ContainerAdapter) tryAdapter(webContext, adapterClass);
                if (sa != null) return sa;
            }
        }

        /*
        ClassLoaders loaders = ClassLoaders.getAppLoaders( ContainerAdapter.class, AdapterManager.class, true );
        DiscoverServiceNames dsn = new DiscoverServiceNames( loaders );
        ResourceNameIterator i = dsn.findResourceNames( ContainerAdapter.class.getName() );
        
        while ( i.hasNext() )
        {
            Class adapterClass = loadClass( loaders, i.nextResourceName(), ContainerAdapter.class );
            
            if ( adapterClass != null )
            {
                ContainerAdapter sa =
                        ( ContainerAdapter ) tryAdapter( adapterClass, servletContext );
                if ( sa != null ) return sa;
            }
        }
        
        */
        
        Class[] classes = DiscoveryUtils.getImplementorClasses(ContainerAdapter.class);

        for (int i = 0; i < classes.length; i++) {
            ContainerAdapter sa = (ContainerAdapter) tryAdapter(webContext, classes[i]);
            if (sa != null) return sa;
        }

        _log.info("No ContainerAdapter specified or discovered; using " + DefaultContainerAdapter.class);
        ContainerAdapter sa =
                new DefaultContainerAdapter() {
                    public boolean accept(WebContext context) {
                        return true;
                    }
                };
        sa.initialize(webContext);
        return sa;
    }
}
