/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.flow.core.source.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.chain.web.servlet.ServletWebContext;
import javax.servlet.ServletContext;
import org.apache.commons.chain.web.portlet.PortletWebContext;
import javax.portlet.PortletContext;
import org.apache.commons.chain.Context;

import java.net.URL;

import org.apache.struts.flow.core.source.*;


/**
 * Base interface for resolving a source by system identifiers.
 * Instead of using the java.net.URL classes which prevent you
 * from adding your own custom protocols in a server environment,
 * you should use this resolver for all URLs.
 *
 * The resolver creates for each source a <code>Source</code>
 * object, which could then be asked for an <code>InputStream</code>
 * etc.
 *
 * When the <code>Source</code> object is no longer needed
 * it must be released using the resolver. This is very similar like
 * looking up components from a <code>ComponentLocator</code>.
 * In fact a source object can implement most lifecycle interfaces
 * like Composable, Initializable, Disposable etc.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.7 $ $Date: 2004/02/28 11:47:26 $
 */

public class ChainSourceResolver extends AbstractSourceResolver implements SourceResolver {

	private Context ctx;
    
    public ChainSourceResolver(Context ctx) {
       this.ctx = ctx;
    }
    
    /**
     *  Determines an absolute path to the path name. If the chain context is an
     *  instance of <code>ServletWebContext</code>, the path is first resolved
     *  against the <code>ServletContext</code>. If not, the path is also tested
     *  for being an absolute path already, and if not, tested to see if it is
     *  in the classpath. If either the path is in the <code>ServletContext</code>
     *  or in the classpath, a temp file is created and the path to it is
     *  returned.
     *
     *@param  ctx              The chain context
     *@param  pathname         The script path
     *@return                  The absolute path to the script file
     *@exception  IOException  If anything goes wrong
     */
    public Source resolveURI( String location ) throws MalformedURLException, IOException {
        
        return resolveURI(location, null, null);
    }
        
        /*
        InputStream is = null;
        File tmpdir = null;

        if (ctx instanceof ServletWebContext) {
            ServletContext context = ((ServletWebContext) ctx).getContext();

            // Can we access the file in the servlet directory?
            String path = context.getRealPath(pathname);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    return (file.toURI());
                }
            }

            // Determine the servlet temp directory;
            tmpdir = (File) context.getAttribute
                    ("javax.servlet.context.tempdir");

            // Try to locate the file in the servlet context
            is = context.getResourceAsStream(pathname);
        } else if (ctx instanceof PortletWebContext) {
            PortletContext context = ((PortletWebContext) ctx).getContext();

            // Can we access the file in the portlet directory?
            String path = context.getRealPath(pathname);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    return (file.toURI());
                }
            }

            // Try to locate the file in the servlet context
            is = context.getResourceAsStream(pathname);
        } 
        
        if (tmpdir == null) {
            // Set the temp directory
            tmpdir = new File(System.getProperty("java.io.tmpdir"));
        }

        // Check to see if the tmp file exists
        File tmpfile = new File(tmpdir, pathname);
        if (tmpfile.exists()) {
            return (tmpfile.toURI());
        }

        // Check to see if the path name is absolute
        File file = new File(pathname);
        if (is != null && file.exists()) {
            return (file.toURI());
        }

        // Try to locate the file on the classpath
        if (is == null) {
            is = getClass().getResourceAsStream(pathname);
        }

        // If no source stream is able to be located
        if (is == null) {
            throw new IOException("Unable to load resource:" + pathname);
        }

        // Read from the source stream and create a tmp file
        BufferedInputStream bis = new BufferedInputStream(is, 1024);
        FileOutputStream os =
                new FileOutputStream(tmpfile);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        byte buffer[] = new byte[1024];
        while (true) {
            int n = bis.read(buffer);
            if (n <= 0) {
                break;
            }
            bos.write(buffer, 0, n);
        }
        bos.close();
        bis.close();
        return (tmpfile.toURI());
        */
    
    /**
     * Get a {@link Source} object.
     * @param location - the URI to resolve. If this is relative it is either
     *                   resolved relative to the base parameter (if not null)
     *                   or relative to a base setting of the source resolver
     *                   itself.
     * @param base - a base URI for resolving relative locations. This
     *               is optional and can be <code>null</code>.
     * @param parameters - Additional parameters for the URI. The parameters
     *                     are specific to the used scheme.
     * @return the resolved source object.
     * @throws MalformedURLException if <code>location</code> is malformed.
     * @throws IOException if the source couldn't be created for some other reason.
     */
    public Source resolveURI( String location,
                       String base,
                       Map parameters )
                       throws MalformedURLException, IOException {
                         
        URL url = null;                   
        if (ctx instanceof ServletWebContext) {
            ServletContext context = ((ServletWebContext) ctx).getContext();

            // Can we access the file in the servlet directory?
            url = context.getResource(location);
        } else if (ctx instanceof PortletWebContext) {
            PortletContext context = ((PortletWebContext) ctx).getContext();

            // Can we access the file in the servlet directory?
            url = context.getResource(location);
        } 
        if (url != null) {
            URLSource source = new URLSource();
            source.init(url, null);
            return source;
        } else {
            return new ResourceSource("resource:/"+location);
        }
   }

    /**
     * Releases a resolved resource.
     * 
     * @param source the source to release.
     */
     public void release( Source source ) {}
}
