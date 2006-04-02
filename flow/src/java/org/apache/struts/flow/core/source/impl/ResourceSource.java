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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceException;
import org.apache.struts.flow.core.source.SourceNotFoundException;
import org.apache.struts.flow.core.source.SourceUtil;
import org.apache.struts.flow.core.source.SourceValidity;
import org.apache.struts.flow.core.source.impl.validity.TimeStampValidity;

/**
 * Description of a source which is described by the resource protocol
 * which gets a resource from the classloader.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:24 $
 */
public final class ResourceSource
    extends AbstractSource
    implements Source
{
    /** Location of the resource */
    private URL m_location;
    private String m_mimeType;

    public ResourceSource( final String systemId ) throws MalformedURLException
    {
        final int pos = SourceUtil.indexOfSchemeColon(systemId);
        if (pos == -1 || ! systemId.startsWith("://", pos))
        {
            throw new MalformedURLException("Invalid format for ResourceSource : " + systemId);
        }
        
        setSystemId(systemId);
        m_location = getClassLoader().getResource(systemId.substring( pos + 3 ));
        setScheme(systemId.substring(0, pos));
    }
    
    public boolean exists()
    {
        return m_location != null;
    }
    
    protected void getInfos()
    {
        // Reset infos
        super.getInfos();
        m_mimeType = null;
        
        if (m_location == null) {
            // Does not exist
            return;
        }
        
        URLConnection connection;
        try
        {
            connection = m_location.openConnection();
        }
        catch(IOException ioe)
        {
            // Exists but unable to open it??
            return;
        }

        setLastModified(connection.getLastModified());
        setContentLength(connection.getContentLength());
        m_mimeType = connection.getContentType();
    }
    
    public String getMimeType()
    {
        return m_mimeType;
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * The returned stream must be closed by the calling code.
     */
    public InputStream getInputStream()
        throws IOException, SourceException
    {
        if (!exists())
        {
            throw new SourceNotFoundException(getURI());
        }
        
        return m_location.openStream();
    }

    /**
     * Returns {@link TimeStampValidity} as resources may change in a directory-based classloader.
     */
    public SourceValidity getValidity()
    {
        return new TimeStampValidity(getLastModified());
    }
    
    protected ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = getClass().getClassLoader();
        }
        
        return loader;
    }
}
