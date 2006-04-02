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

/**
 * This Exception should be thrown if the source could not be found.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:26 $
 */
public class SourceNotFoundException
    extends SourceException
{
    /**
     * Construct a new <code>SourceNotFoundException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public SourceNotFoundException( final String message )
    {
        super( message, null );
    }

    /**
     * Construct a new <code>SourceNotFoundException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public SourceNotFoundException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
