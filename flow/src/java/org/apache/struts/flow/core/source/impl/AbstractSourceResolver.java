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

public abstract class AbstractSourceResolver implements SourceResolver {

    protected String method;
    protected Map uriParameters;
    protected String uriEncoding;

    public void setMethod(String m) {
        this.method = m;
    }
    
    public void setUriParameters(Map params) {
        this.uriParameters = params;
    }
    
    public void setUriEncoding(String enc) {
        this.uriEncoding = enc;
    }
        
	
    /**
     * Get a {@link Source} object. This is a shortcut for {@link #resolveURI
     * (String, String, Map)}.
     * 
     * @return the resolved source object.
     * @throws MalformedURLException if <code>location</code> is malformed.
     * @throws IOException if the source couldn't be created for some other reason.
     */
    public abstract Source resolveURI( String location )
        throws MalformedURLException, IOException;

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
    public abstract Source resolveURI( String location,
                       String base,
                       Map parameters )
        throws MalformedURLException, IOException;

    /**
     * Releases a resolved resource.
     * 
     * @param source the source to release.
     */
     public void release( Source source ) {}
}
