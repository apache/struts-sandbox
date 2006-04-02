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

import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceException;
import org.apache.struts.flow.core.source.SourceValidity;

/**
 * Abstract base class for a source implementation.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/28 11:47:24 $
 */

public abstract class AbstractSource
    implements Source
{
    private boolean m_gotInfos;
    private long m_lastModificationDate;
    private long m_contentLength;
    private String m_systemId;

    private String m_scheme;

    /**
     * Get the last modification date and content length of the source.
     * Any exceptions are ignored.
     * Override this to get the real information
     */
    protected void getInfos()
    {
        this.m_contentLength = -1;
        this.m_lastModificationDate = 0;
    }

    /**
     * Call {@link #getInfos()} if it hasn't already been called since the last
     * call to {@link #refresh()}.
     */
    protected void checkInfos()
    {
        if( !m_gotInfos )
        {
            getInfos();
            m_gotInfos = true;
        }
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * The returned stream must be closed by the calling code.
     *
     * @throws SourceException if file not found or
     *         HTTP location does not exist.
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream()
        throws IOException, SourceException
    {
        return null;
    }

    /**
     * Return the unique identifer for this source
     */
    public String getURI()
    {
        return m_systemId;
    }

    /**
     * Return the protocol identifier.
     */
    public String getScheme() 
    {
        return this.m_scheme;
    }

    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    public SourceValidity getValidity()
    {
        return null;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void refresh()
    {
        m_gotInfos = false;
    }

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        return null;
    }

    /**
     * Return the content length of the content or -1 if the length is
     * unknown
     */
    public long getContentLength()
    {
        checkInfos();
        return this.m_contentLength;
    }

    /**
     * Get the last modification date of the source or 0 if it
     * is not possible to determine the date.
     */
    public long getLastModified()
    {
        checkInfos();
        return this.m_lastModificationDate;
    }
    /**
     * Sets the contentLength.
     * @param contentLength The contentLength to set
     */
    protected void setContentLength(long contentLength)
    {
        m_contentLength = contentLength;
    }

    /**
     * Sets the lastModificationDate.
     * @param lastModificationDate The lastModificationDate to set
     */
    protected void setLastModified(long lastModificationDate)
    {
        m_lastModificationDate = lastModificationDate;
    }

    /**
     * Sets the scheme.
     * @param scheme The scheme to set
     */
    protected void setScheme(String scheme)
    {
        m_scheme = scheme;
    }

    /**
     * Sets the systemId.
     * @param systemId The systemId to set
     */
    protected void setSystemId(String systemId)
    {
        m_systemId = systemId;
    }

}
