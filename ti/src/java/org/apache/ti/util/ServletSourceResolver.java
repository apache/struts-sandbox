/*
 * $Id$
 *
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
package org.apache.ti.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *  Source resolver that uses the servlet resource locator
 */
public class ServletSourceResolver implements SourceResolver {

    /**
     * <p>Commons Logging instance.</p>
     */
    protected static Log log = LogFactory.getLog(ServletSourceResolver.class);
    
    public URL resolve(String path, WebContext context) 
            throws IOException, MalformedURLException {
                
        List list = resolveList(path, context);
        if (list.size() > 0) {
            return (URL) list.get(0);
        } else {
            return null;
        }
    }
    
    public List resolveList(String path, WebContext context) 
            throws IOException, MalformedURLException {
        
        ServletContext servletContext = ((ServletWebContext)context).getContext();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        ArrayList resolvedUrls = new ArrayList();

        URL resource = null;
        if (path != null && path.length() > 0) {

            if (path.charAt(0) == '/') {
                resource = servletContext.getResource(path);
            }

            if (resource == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to locate " + path
                            + " in the servlet context, "
                            + "trying classloader.");
                }
                Enumeration e = loader.getResources(path);
                if (!e.hasMoreElements()) {
                    String msg = "Resource not found: "+path;
                    log.error(msg);
                    throw new IOException(msg);
                } else {
                    while (e.hasMoreElements()) {
                        resolvedUrls.add(e.nextElement());
                    }
                }
            } else {
                resolvedUrls.add(resource);
            }
        }
        return resolvedUrls;  
    }
    
    public List resolveFromList(String paths, WebContext context) 
            throws IOException, MalformedURLException {
        
        ServletContext servletContext = ((ServletWebContext)context).getContext();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        ArrayList resolvedUrls = new ArrayList();

        URL resource = null;
        String path = null;
        // Process each specified resource path
        while (paths.length() > 0) {
            int comma = paths.indexOf(',');
            if (comma >= 0) {
                path = paths.substring(0, comma).trim();
                paths = paths.substring(comma + 1);
            } else {
                path = paths.trim();
                paths = "";
            }

            if (path.length() < 1) {
                break;
            }

            if (path.charAt(0) == '/') {
                resource = servletContext.getResource(path);
            }

            if (resource == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to locate " + path
                            + " in the servlet context, "
                            + "trying classloader.");
                }
                Enumeration e = loader.getResources(path);
                if (!e.hasMoreElements()) {
                    String msg = "Resource not found: "+path;
                    log.error(msg);
                    throw new IOException(msg);
                } else {
                    while (e.hasMoreElements()) {
                        resolvedUrls.add(e.nextElement());
                    }
                }
            } else {
                resolvedUrls.add(resource);
            }
        }
        return resolvedUrls;   
    }

}
