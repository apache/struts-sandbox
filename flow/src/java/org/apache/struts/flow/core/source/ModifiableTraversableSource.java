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
 * A modifiable traversable source. This adds to {@link ModifiableSource} the
 * ability to create a directory.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:26 $
 */
public interface ModifiableTraversableSource extends ModifiableSource, TraversableSource
{
    /**
     * If it doesn't already exist, ensure this source is traversable
     * (equivalent to <code>File.mkdirs()</code>)
     * <p>
     * If the source already exists, this method does nothing if it's already
     * traversable, and fails otherwise.
     */
    public void makeCollection() throws SourceException;

}


