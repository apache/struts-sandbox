/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.faces;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

/**
 * <p>Standard Chain of Responsibility command to invoke the remainder of the
 * web container's filter chain, using the currently specified request and
 * response objects.</p>
 *
 * $Id$
 */
public class InvokeCommand implements Command {
    
    
    /**
     * <p>Invoke the remainder of the filter chain.</p>
     *
     * @param context <code>ShaleWebContext</code> for this request
     */
    public boolean execute(Context context) throws Exception {

        ShaleWebContext webContext = (ShaleWebContext) context;
        webContext.getChain().doFilter(webContext.getRequest(),
                                       webContext.getResponse());
        return false;

    }


}
