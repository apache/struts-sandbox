/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.processor.chain.pageflow;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.pageflow.FlowControllerFactory;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.logging.Logger;

import java.util.Map;

public class CreateSharedFlows implements Command {
    
    private static final Logger _log = Logger.getInstance(CreateSharedFlows.class);        

    public boolean execute(Context context) throws Exception {
        if ( _log.isDebugEnabled() )
        {
            String requestPath = PageFlowActionContext.get().getRequestPath();
            _log.debug( "Attempting to instantiate SharedFlowControllers for request " + requestPath );
        }
        
        Map/*< String, SharedFlowController >*/ sharedFlows = FlowControllerFactory.get().getSharedFlowsForRequest();
        ImplicitObjectUtil.loadSharedFlow( sharedFlows );
        return false;
    }
}
