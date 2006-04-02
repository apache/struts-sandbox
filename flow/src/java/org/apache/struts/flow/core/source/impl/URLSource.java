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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceException;
import org.apache.struts.flow.core.source.SourceParameters;
import org.apache.struts.flow.core.source.SourceResolver;
import org.apache.struts.flow.core.source.SourceUtil;
import org.apache.struts.flow.core.source.SourceValidity;
import org.apache.struts.flow.core.source.impl.validity.TimeStampValidity;

/**
 * Description of a source which is described by an URL.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/28 11:47:24 $
 */
public class URLSource extends AbstractSource implements Source
{

    /** The URL of the source */
    protected URL m_url;

    /** The connection for a real URL */
    protected URLConnection m_connection;

    /** The <code>SourceParameters</code> used for a post*/
    protected SourceParameters m_parameters;

    /** The encoding of the <code>SourceParameters</code>*/
    protected String m_encoding;

    /** Is this a post? */
    protected boolean m_isPost = false;

    /** Does this source exist ? */
    protected boolean m_exists = false;

    /** the prev returned SourceValidity */
    protected SourceValidity m_cachedValidity;

    protected long m_cachedLastModificationDate;

    /** The content type (if known) */
    protected String m_mimeType;

    /**
     * Constructor
     */
    public URLSource()
    {
    }

    /**
     * Initialize a new object from a <code>URL</code>.
     * @param parameters This is optional
     */
    public void init(URL url, Map parameters) throws IOException
    {
        String systemId = url.toExternalForm();
        setSystemId(systemId);
        setScheme(SourceUtil.getScheme(systemId));

        m_url = url;
        m_isPost = false;
        // get the default system encoding in case no encoding is specified
        try {
            m_encoding = System.getProperty("file.property", "ISO-8859-1");
        } catch (SecurityException e) {
            m_encoding = "ISO-8859-1"; 
        }

        if (null != parameters)
        {
            m_parameters = (SourceParameters) parameters.get(SourceResolver.URI_PARAMETERS);
            final String method = (String) parameters.get(SourceResolver.METHOD);

            if ("POST".equalsIgnoreCase(method))
                m_isPost = true;

            final String encoding = (String) parameters.get(SourceResolver.URI_ENCODING);
            if (encoding != null && !"".equals(encoding))
                m_encoding = encoding;
        }

        if (null != m_parameters && m_parameters.hasParameters() && !m_isPost)
        {
            StringBuffer urlBuffer = new StringBuffer(systemId);
            String key;
            final Iterator i = m_parameters.getParameterNames();
            Iterator values;
            String value;
            boolean first = (systemId.indexOf('?') == -1);
            if (first == true)
                urlBuffer.append('?');
            while (i.hasNext())
            {
                key = (String) i.next();
                values = m_parameters.getParameterValues(key);
                while (values.hasNext() == true)
                {
                    value = SourceUtil.encode((String) values.next(), m_encoding);
                    if (first == false)
                        urlBuffer.append('&');
                    first = false;
                    urlBuffer.append(key);
                    urlBuffer.append('=');
                    urlBuffer.append(value);
                }
            }

            m_url = new URL(urlBuffer.toString());
            m_parameters = null;
        }
    }

    /**
     * Get the last modification date and content length of the source.
     * Any exceptions are ignored.
     * Override this to get the real information
     */
    protected void getInfos()
    {
        // exists will be set below depending on the m_url type
        m_exists = false;

        if (!m_isPost)
        {
            try
            {
                if (null == m_connection)
                {
                    m_connection = m_url.openConnection();
                    String userInfo = getUserInfo();
                    if (m_url.getProtocol().startsWith("http") && userInfo != null){
                        m_connection.setRequestProperty("Authorization", "Basic " + SourceUtil.encodeBASE64(userInfo));
                    }
                }
                setLastModified(m_connection.getLastModified());
                m_mimeType = m_connection.getContentType();
                int contentLength = m_connection.getContentLength();
                setContentLength(contentLength);
                if ( m_connection instanceof HttpURLConnection )
                {
                    // check the status code for exists
                    // if the url does not exists we might also get an IOException here!
                    try 
                    {
                        final int statusCode = ((HttpURLConnection)m_connection).getResponseCode();
                        if ( statusCode == 200 || statusCode == 304 )
                        {
                            m_exists = true;
                        } 
                        else
                        {
                            m_exists = false;
                        }                        
                    }
                    catch (IOException ignore)
                    {
                        m_exists = false;
                    }
                        
                } 
                else 
                {
                    m_exists = contentLength > 0;
                }
            }
            catch (IOException ignore)
            {
                super.getInfos();
            }
        }
        else
        {
            // do not open m_connection when using post!
            super.getInfos();
        }
    }

    /**
     * Does this source exist ?
     */
    public boolean exists()
    {
        checkInfos();
        return m_exists;
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
    public InputStream getInputStream() throws IOException, SourceException
    {
        checkInfos();
        InputStream input = null;
        if (m_connection == null)
        {
            m_connection = m_url.openConnection();

            String userInfo = getUserInfo();
            if (m_url.getProtocol().startsWith("http") && userInfo != null)
            {
                m_connection.setRequestProperty("Authorization", "Basic " + SourceUtil.encodeBASE64(userInfo));
            }

            // do a post operation
            if (m_connection instanceof HttpURLConnection && m_isPost)
            {
                StringBuffer buffer = new StringBuffer(2000);
                String key;
                Iterator i = m_parameters.getParameterNames();
                Iterator values;
                String value;
                boolean first = true;
                while (i.hasNext())
                {
                    key = (String) i.next();
                    values = m_parameters.getParameterValues(key);
                    while (values.hasNext() == true)
                    {
                        value = SourceUtil.encode((String) values.next(), m_encoding);
                        if (first == false)
                            buffer.append('&');
                        first = false;
                        buffer.append(key.toString());
                        buffer.append('=');
                        buffer.append(value);
                    }
                }
                HttpURLConnection httpCon = (HttpURLConnection) m_connection;
                httpCon.setDoInput(true);

                if (buffer.length() > 1)
                { // only post if we have parameters
                    String postString = buffer.toString();
                    httpCon.setRequestMethod("POST"); // this is POST
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    // A content-length header must be contained in a POST request
                    httpCon.setRequestProperty("Content-length", Integer.toString(postString.length()));
                    java.io.OutputStream out = new java.io.BufferedOutputStream(httpCon.getOutputStream());
                    out.write(postString.getBytes());
                    out.close();
                }
                input = httpCon.getInputStream();
                m_connection = null; // make sure a new m_connection is created next time
                return input;
            }
        }
        input = m_connection.getInputStream();
        m_connection = null; // make sure a new m_connection is created next time
        return input;
    }

    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    public SourceValidity getValidity()
    {
        final long lm = getLastModified();
        if (lm > 0)
        {
            if (lm == m_cachedLastModificationDate)
                return m_cachedValidity;

            m_cachedLastModificationDate = lm;
            m_cachedValidity = new TimeStampValidity(lm);
            return m_cachedValidity;
        }
        return null;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void refresh()
    {
        // reset m_connection
        m_connection = null;
        super.refresh();
    }

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        return m_mimeType;
    }

    /**
     * The decoded userinfo for this source.
     * null, if no userinfo exists
     */
    protected String getUserInfo()
    {
        if (m_url == null) return null;
        String ui = m_url.getUserInfo();
        if (ui == null) return null;

        try
        {
         ui = URLDecoder.decode(ui,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
         // Platform does not support UTF-8. This should never happen.
         // e.printStackTrace();
        }
        return ui;
    }
}
