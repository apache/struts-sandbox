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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.flow.core.source.SourceValidity;

/**
 * The base class for the aggregation implementations
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:17 $
 */
public abstract class AbstractAggregatedValidity
    implements SourceValidity
{
    final ArrayList m_list = new ArrayList();

    public void add( final SourceValidity validity )
    {
        m_list.add( validity );
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer( "SourceValidity " );
        for( final Iterator i = m_list.iterator(); i.hasNext(); )
        {
            sb.append( i.next() );
            if( i.hasNext() ) sb.append( ':' );
        }
        return sb.toString();
    }
    
    public List getValidities() 
    {
        return Collections.unmodifiableList(m_list);
    }
    
    SourceValidity getValidity(final int index) 
    {
        return (SourceValidity) m_list.get(index);
    }
    
}
