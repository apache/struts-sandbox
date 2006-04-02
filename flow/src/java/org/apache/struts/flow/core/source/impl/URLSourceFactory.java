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
import java.net.URL;
import java.util.Map;

import org.apache.struts.flow.core.Factory;
import org.apache.struts.flow.core.Logger;

import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceFactory;

/**
 * A factory for a {@link URL} wrapper
 * 
 * @avalon.component
 * @avalon.service type=SourceFactory
 * @x-avalon.info name=url-source
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: URLSourceFactory.java,v 1.4 2004/02/28 11:47:24 cziegeler Exp $
 */
public class URLSourceFactory implements SourceFactory
{

    /**
     * Create an URL-based source. This class actually creates an {@link URLSource}, but if another
     * implementation is needed, subclasses can override this method.
     */
    protected Source createURLSource(URL url, Map parameters) throws MalformedURLException, IOException
    {
        URLSource result = new URLSource();
        result.init(url, parameters);
        return result;
    }

    /**
     * Create an file-based source. This class actually creates an {@link FileSource}, but if another
     * implementation is needed, subclasses can override this method.
     */
    protected Source createFileSource(String uri) throws MalformedURLException, IOException
    {
        return new FileSource(uri);
    }

    /**
     * @see org.apache.struts.flow.core.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String uri, Map parameters) throws MalformedURLException, IOException
    {
        if (getLogger().isDebugEnabled())
        {
            final String message = "Creating source object for " + uri;
            getLogger().debug(message);
        }

        // First check if it's a file
        if (uri.startsWith("file:"))
        {
            // Yes : return a file source
            return createFileSource(uri);
        }
        else
        {
            // Not a "file:" : create an URLSource
            // First try to create the URL
            URL url;
            try
            {
                url = new URL(uri);
            }
            catch (MalformedURLException mue)
            {
                // Maybe a file name containing a ':' ?
                if (getLogger().isDebugEnabled())
                {
                    this.getLogger().debug("URL " + uri + " is malformed. Assuming it's a file path.", mue);
                }
                return createFileSource(uri);
            }

            return createURLSource(url, parameters);
        }
    }

    /**
     * @see org.apache.struts.flow.core.source.SourceFactory#release(org.apache.struts.flow.core.source.Source)
     */
    public void release(Source source)
    {
        // do nothing here
    }
    
    public Logger getLogger() {
        return Factory.getLogger();
    }
}
