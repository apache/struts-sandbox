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
import org.apache.ti.core.urls.URLRewriterService;
import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.FlowControllerFactory;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowEventReporter;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.DefaultURLRewriter;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.internal.JavaControlUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.logging.Logger;

import java.util.Map;

import com.opensymphony.xwork.ActionContext;

public class ShowView implements Command {

    private static final Logger _log = Logger.getInstance(ShowView.class);

    public boolean execute(Context context) throws Exception {
        // Callback to the container adapter.
        ContainerAdapter containerAdapter = AdapterManager.getContainerAdapter();
        PageFlowEventReporter er = containerAdapter.getEventReporter();
        containerAdapter.beginRequest();
        er.beginPageRequest();
        long startTime = System.currentTimeMillis();
        
        //
        // Initialize the ControlBeanContext in the session.
        //
        JavaControlUtils.initializeControlContext();

        //
        // Register the default URLRewriter
        //
        URLRewriterService.registerURLRewriter( 0, new DefaultURLRewriter() );

        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        boolean isForwardedRequest = actionContext.isNestedRequest();
        
        try
        {
            if ( ! actionContext.isStayInCurrentModule() ) {
                // Ensure that the right module is selected, for use by the tags.
                InternalUtils.selectModule( actionContext.getModuleConfig());
            }
            
            try
            {
                //
                // Initialize shared flows for the current request.
                //
                FlowControllerFactory flowControllerFactory = FlowControllerFactory.get();
                Map/*< String, SharedFlowController >*/ sharedFlows = flowControllerFactory.getSharedFlowsForRequest();
                ImplicitObjectUtil.loadSharedFlow( sharedFlows );
                
                //
                // Make sure that the current PageFlowController is set up for this request.
                //
                PageFlowController curJpf;
                if ( actionContext.isStayInCurrentModule() )
                {
                    actionContext.setStayInCurrentModule( false );
                    curJpf = PageFlowUtils.getCurrentPageFlow();
                }
                else
                {
                    curJpf = flowControllerFactory.getPageFlowForRequest();
                }
                
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Current PageFlowController is: " + curJpf );
                    _log.debug( "Continuing with filter chain..." );
                }
                
                runPage( curJpf);
            }
            catch ( ClassNotFoundException e )
            {
                throw new PageFlowException( e );
            }
            catch ( InstantiationException e )
            {
                throw new PageFlowException( e );
            }
            catch ( IllegalAccessException e )
            {
                throw new PageFlowException( e );
            }
            catch ( PageFlowException e )
            {
                throw new PageFlowException( e );
            }
        }
        finally
        {
            //
            // Clean up the ControlBeanContext in the session.
            //
            JavaControlUtils.uninitializeControlContext();
            
            //
            // Callback to the server adapter.
            //
            containerAdapter.endRequest();
            long timeTaken = System.currentTimeMillis() - startTime;
            er.endPageRequest(timeTaken );
            
            //
            // If this is not a forwarded request, then commit any session-scoped changes that were stored in the
            // request.
            //
            if ( ! isForwardedRequest )
            {
                Handlers.get().getStorageHandler().applyChanges();
            }
        }
        
        return false;
    }
    
    private void runPage( PageFlowController curJpf)
        throws PageFlowException, PageFlowException
    {
        //
        // Make sure that the pageflow's getRequest() and getResponse() will work while the page
        // is being rendered, since methods on the pageflow may be called (through databinding
        // or tags, or through direct reference).
        //
        if ( curJpf != null )
        {
            //
            // We're going to bail out if there are too many concurrent requests for the same JPF.
            // This prevents an attack that takes advantage of the fact that we synchronize requests
            // to the same pageflow.
            //
            if ( curJpf.incrementRequestCount() )
            {
                try
                {
                    //
                    // Any databinding calls, indirect calls to getRequest(), etc. must be protected
                    // against conflicts from running action methods at the same time as rendering 
                    // the page here.  Synchronize on the JPF.
                    //
                    synchronized ( curJpf )
                    {
                        ImplicitObjectUtil.loadImplicitObjects( curJpf );
                                
                        //
                        // Tell the page flow that we're about to display a page so it can manage settings,
                        // such as previous page information, if needed in advance.
                        //
                        curJpf.beforePage();
                                
                        try
                        {
                            showView();
                        }
                        catch ( PageFlowException ex )
                        {
                            //
                            // If a PageFlowException escapes out of the page, let the current FlowController handle it.
                            //
                            if ( ! handleException( ex, curJpf) ) throw ex;
                        }
                        catch ( Throwable th )
                        {
                            //
                            // If a Throwable escapes out of the page, let the current FlowController handle it.
                            //
                            if ( ! handleException( th, curJpf) )
                            {
                                if ( th instanceof Error ) throw ( Error ) th;
                                throw new PageFlowException( th );
                            }
                        }
                    }
                }
                finally
                {
                    curJpf.decrementRequestCount();
                }
            }
        }
        else
        {
            ImplicitObjectUtil.loadImplicitObjects(null );
            showView();
        }
    }
    
    public static abstract class ViewRunner {
        
        public abstract void runView() throws PageFlowException;
        
        public void activate() {
            ActionContext actionContext = ActionContext.getContext();
            actionContext.put("viewRunner", this);
        }
    }
    
    protected void showView() throws PageFlowException {
        ActionContext actionContext = ActionContext.getContext();
        ViewRunner viewRunner = (ViewRunner) actionContext.get("viewRunner");
        assert viewRunner != null : "ViewRunner not in action context under key \"viewRunner\"";
        viewRunner.runView();
    }
    
    private boolean handleException( Throwable th, FlowController fc) {
        try
        {
            fc.handleException( th );
            return true;
        }
        catch ( PageFlowException t )
        {
            _log.error( "Exception while handling exception " + th.getClass().getName()
                        + ".  The original exception will be thrown.", th );
            return false;
        }
    } 
    
    public void destroy()
    {
    }    
}
