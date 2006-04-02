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
package org.apache.struts.flow.core.source;

import java.io.IOException;

/**
 * This Exception is thrown every time there is a problem in processing
 * a source.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:26 $
 */
public class SourceException
    extends IOException 
{
    /**
     * The Throwable that caused this exception to be thrown.
     */
    private final Throwable m_throwable;

    /**
     * Construct a new <code>SourceException</code> instance.
     *
     * @param message the detail message for this exception.
     */
    public SourceException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>SourceException</code> instance.
     *
     * @param message the detail message for this exception.
     * @param throwable the root cause of the exception.
     */
    public SourceException( final String message, final Throwable throwable )
    {
        super( message  );
        m_throwable = throwable;
    }
    
    /**
     * Retrieve the cause of the exception.
     *
     * @return the cause.
     */
    public final Throwable getCause()
    {
        return m_throwable;
    }
}
