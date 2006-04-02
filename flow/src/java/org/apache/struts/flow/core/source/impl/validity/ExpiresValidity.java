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

import org.apache.struts.flow.core.source.SourceValidity;

/**
 * A validation object that holds an expiration date.
 * When the defined time/date has arrived, this validity object is 
 * not valid any more.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:17 $
 */
public final class ExpiresValidity
    implements SourceValidity
{
    private long expires;

    /**
     * Constructor
     * @param expires The delta from now when this validity object gets invalid.
     */
    public ExpiresValidity( long expires ) 
    {
        this.expires = System.currentTimeMillis() + expires;
    }

    /**
     * Checks if the expires date is already reached.
     * 
     * @see org.apache.struts.flow.core.source.SourceValidity#isValid()
     */
    public int isValid() 
    {
        final long currentTime = System.currentTimeMillis();
        return (currentTime <= this.expires ? SourceValidity.VALID : SourceValidity.INVALID);
    }

    /**
     * This method is never invoked as {@link #isValid()} can always perform
     * the complete check.
     * 
     * @see org.apache.struts.flow.core.source.SourceValidity#isValid(SourceValidity)
     */
    public int isValid( SourceValidity newValidity ) 
    {
        return SourceValidity.INVALID;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() 
    {
        return "ExpiresValidity: " + expires;
    }
}
