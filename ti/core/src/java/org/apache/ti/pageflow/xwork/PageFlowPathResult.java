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
package org.apache.ti.pageflow.xwork;

import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.util.logging.Logger;

public class PageFlowPathResult extends PageFlowResult {

    private static final Logger _log = Logger.getInstance(PageFlowPathResult.class);

    protected Forward applyForward(Forward fwd, ModuleConfig altModuleConfig) {

        //
        // It's a normal path.  Mark a particular request attribute if it's an inherited local path, which
        // shouldn't be treated as a transfer to an external page flow (the base class page flow).
        //
        if (isInheritedPath()) {
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            actionContext.setStayInCurrentModule(true);
        }

        return fwd;
    }

    protected boolean shouldSavePreviousPageInfo() {
        return true;
    }

    public boolean isPath() {
        return true;
    }
    
    /* TODO: re-add this sort of support for self-nesting a page flow
    forward forwardTo( forward fwd, String actionName, ModuleConfig altModuleConfig)
    {
        //
        // Special case: the *only* way for a nested pageflow to nest itself is for it
        // to forward to itself as a .jpf.  Simply executing an action in the .jpf isn't
        // enough, obviously, since it's impossible to tell whether it should be executed
        // in the current pageflow or a new nested one.
        //
        if ( fwd != null && getModuleConfig().isNestedFlow())
        {
            boolean selfNesting = false;
            String superFwdPath = superFwd.getPath();
            
            if ( superFwdPath.startsWith("/") )
            {
                if ( superFwdPath.equals( getPath() ) ) selfNesting = true;
            }
            else
            {
                String className = getClass().getName();
                int lastDot = className.lastIndexOf( '.' );
                String thisPageFlowLocalURI = className.substring( lastDot + 1 ) + PAGEFLOW_EXTENSION;
                if ( superFwdPath.equals( thisPageFlowLocalURI ) ) selfNesting = true;
            }
            
            if ( selfNesting )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Self-nesting page flow " + getPath() );
                }
                
                try
                {
                    // This will cause the right pageflow stack stuff to happen.
                    FlowControllerFactory.get().createPageFlow( getClass() );
                }
                catch ( IllegalAccessException e )
                {
                    // This should never happen -- if we successfully created this page flow once, we can do it again.
                    assert false : e;
                    _log.error( e );
                }
                catch ( InstantiationException e )
                {
                    _log.error( "Could not create PageFlowController instance of type " + getClass().getName(), e );
                }
            }
        }
        
        return superFwd;
    }
    
    */

}
