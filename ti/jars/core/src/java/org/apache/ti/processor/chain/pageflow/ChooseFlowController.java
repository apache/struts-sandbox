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
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.FlowControllerFactory;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.SharedFlowController;
import org.apache.ti.util.logging.Logger;

public class ChooseFlowController implements Command {

    private static final Logger _log = Logger.getInstance(ChooseFlowController.class);
    
    public boolean execute(Context context) throws Exception {
        //
        // Get the FlowController for this request (page flow or shared flow), and cache it in the request.
        //
        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        ModuleConfig moduleConfig = actionContext.getModuleConfig();
        assert moduleConfig != null;
        
        String flowControllerClassName = moduleConfig.getControllerClassName();
        FlowController currentFlowController = null;
        
        if ( flowControllerClassName != null )
        {
            currentFlowController = getFlowController( flowControllerClassName );
            actionContext.setCurrentFlowController( currentFlowController );
        }
        else
        {
            actionContext.setCurrentFlowController( null );
        }
        
        return false;
    }
    
    private FlowController getFlowController( String fcClassName )
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        FlowControllerFactory fcFactory = FlowControllerFactory.get();
        Class fcClass = fcFactory.getFlowControllerClass( fcClassName );
        
        if ( PageFlowController.class.isAssignableFrom( fcClass ) )
        {
            PageFlowController current = PageFlowUtils.getCurrentPageFlow();
            
            if ( current != null && current.getClass().equals( fcClass ) )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Using current page flow: " + current );
                }
                
                //
                // Reinitialize transient data that may have been lost on session failover.
                //
                current.reinitialize();
                return current;
            }
            
            return fcFactory.createPageFlow( fcClass );
        }
        else
        {
            assert SharedFlowController.class.isAssignableFrom( fcClass ) : fcClass.getName();
            
            SharedFlowController current = PageFlowUtils.getSharedFlow( fcClass.getName());
            
            if ( current != null )
            {
                current.reinitialize();
                return current;
            }
            
            return fcFactory.createSharedFlow( fcClass );
        }
    }
}
