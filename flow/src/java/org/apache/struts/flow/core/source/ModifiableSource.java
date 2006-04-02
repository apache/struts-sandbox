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
import java.io.OutputStream;

/**
 * A {@link Source} that can be written to.
 * <p>
 * As far a possible, implementations should provide a kind of transaction or
 * buffering of data written to the source. This is especially important in
 * stream-based systems such as Cocoon where an error that occurs during the
 * processing should lead to cancelling data written to the source.
 * <p>
 * This is the role of the {@link #canCancel(OutputStream)} and
 * {@link #cancel(OutputStream)} methods.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:26 $
 */
public interface ModifiableSource
    extends Source
{
    /**
     * Return an {@link OutputStream} to write to.
     *
     * The returned stream must be closed or cancelled by the calling code.
     */
    OutputStream getOutputStream() throws IOException;
    
    /**
     * Delete the source 
     */
    void delete() throws SourceException;

    /**
     * Can the data sent to an <code>OutputStream</code> returned by
     * {@link #getOutputStream()} be cancelled ?
     *
     * @return true if the stream can be cancelled
     */
    boolean canCancel(OutputStream stream);

    /**
     * Cancel the data sent to an <code>OutputStream</code> returned by
     * {@link #getOutputStream()}.  Cancelling the stream will also close it.
     *
     * <p>After cancelling, the stream should no longer be used.</p>
     */
    void cancel(OutputStream stream) throws IOException;
    
}
