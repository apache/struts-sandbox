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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.struts.flow.core.source.SourceValidity;

/**
 * A validation object using a List.
 * This validity object does the same as the {@link AggregatedValidity}
 * object, but the contained validity objects are only fetched when
 * required.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:17 $
 */
public final class DeferredAggregatedValidity
        extends AbstractAggregatedValidity
    implements SourceValidity
{

    public void add( final DeferredValidity validity )
    {
        m_list.add( validity );
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
        for( final ListIterator i = m_list.listIterator(); i.hasNext(); )
        {
            final Object o = i.next();
            final SourceValidity validity;
            if (o instanceof SourceValidity) {
                validity = (SourceValidity)o;
            } else {
                validity = ((DeferredValidity)o).getValidity();
                i.set(validity);
            }
            final int v = validity.isValid();
            if( v < 1 )
            {
                return v;
            }
        }
        return 1;
    }

    public int isValid( final SourceValidity validity )
    {
        AbstractAggregatedValidity aggregatedValidity = null;
        
        if (validity instanceof AbstractAggregatedValidity) 
        {
            aggregatedValidity = (AbstractAggregatedValidity)validity;
        }
        
        if ( null != aggregatedValidity) 
        {
            ArrayList otherList = aggregatedValidity.m_list;
            if( m_list.size() != otherList.size() )
            {
                return -1;
            }

            for(int i=0; i < m_list.size(); i++) {
                final SourceValidity srcA = this.getValidity(i);
                int result = srcA.isValid();
                if ( result == -1) 
                {
                    return -1;
                }
                if ( result == 0 )
                {
                    final SourceValidity srcB = aggregatedValidity.getValidity(i);
                    result = srcA.isValid( srcB );
                    if ( result < 1)
                    {
                        return result;
                    }
                }
            }
            return 1;
        }
        return -1;
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
    
    SourceValidity getValidity(final int index) 
    {
        final Object o = m_list.get(index);
        final SourceValidity validity;
        if (o instanceof SourceValidity) {
            validity = (SourceValidity)o;
        } else {
            validity = ((DeferredValidity)o).getValidity();
            m_list.set(index, validity);
        }
        return validity;
    }
    
    private void writeObject(java.io.ObjectOutputStream out)
         throws IOException
    {
        // resolve all deferred source validities first
        for(int i=0; i<m_list.size();i++) {
            this.getValidity(i);
        }
        out.defaultWriteObject();
    }

}

