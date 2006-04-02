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
package org.apache.struts.flow.core.source.impl.validity;

import java.io.File;

import org.apache.struts.flow.core.source.SourceValidity;

/**
 * A validation object for time-stamps.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $
 */
public final class FileTimeStampValidity
    implements SourceValidity
{
    private long m_timeStamp;
    private File m_file;

    public FileTimeStampValidity( final String filename )
    {
        this( new File( filename ) );
    }

    public FileTimeStampValidity( final File file )
    {
        this( file, file.lastModified() );
    }

    public FileTimeStampValidity( final File file,
                                  final long timeStamp )
    {
        m_file = file;
        m_timeStamp = timeStamp;
    }

    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        return ( m_file.lastModified() == m_timeStamp ? 1 : -1 );
    }

    public int isValid( final SourceValidity newValidity )
    {
        if( newValidity instanceof FileTimeStampValidity )
        {
            final long timeStamp =
                ( (FileTimeStampValidity)newValidity ).getTimeStamp();
            return ( m_timeStamp == timeStamp ? 1 : -1);
        }
        return -1;
    }

    public File getFile()
    {
        return this.m_file;
    }

    public long getTimeStamp()
    {
        return this.m_timeStamp;
    }

    public String toString()
    {
        return "FileTimeStampValidity: " + m_file.getPath() + ": " + this.m_timeStamp;
    }
}
