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

import com.opensymphony.xwork.ActionInvocation;

import org.apache.ti.pageflow.FlowControllerException;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.NoCurrentPageFlowException;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.PreviousPageInfo;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.logging.Logger;

public class NavigateToPageResult
        extends NavigateToResult {
    private static final Logger _log = Logger.getInstance(NavigateToPageResult.class);

    /**
     * 0 is current page, 1 is previous page, etc.
     */
    private int _previousPageIndex = -1;

    public void execute(ActionInvocation invocation) throws Exception {
        //
        // We need access to previousPageInfo from the *current page flow*.  That is often the current FlowController
        // in the ActionConteext, but if it's a shared flow, then we don't want to use that.
        //
        PageFlowController curJpf = PageFlowUtils.getCurrentPageFlow();

        if (curJpf == null) {
            FlowControllerException ex = new NoCurrentPageFlowException(this);
            InternalUtils.throwPageFlowException(ex);
            assert false; // throwPageFlowException() must throw.
        }

        PreviousPageInfo prevPageInfo;

        switch (getPreviousPageIndex()) {
            case 0:
                prevPageInfo = curJpf.getCurrentPageInfo();

                break;

            case 1:
                prevPageInfo = curJpf.getPreviousPageInfo();

                break;

            default:
                assert false : getPreviousPageIndex() + " is not a valid previous-page index";

                // of course, in the future, we should support any index, up to an app-configured max
                prevPageInfo = curJpf.getCurrentPageInfo();
        }

        // The previous result has already been initialized from the previous Forward.
        //    1) Initialize from *this* forward, overwriting previously-initialized values.
        //    2) Apply the previous Forward (sets values in the request).
        //    3) Apply this Forward (sets values in the request, possibly overwriting those set above).
        //    4) Allow the previous result to finish execution.
        PageFlowActionContext actionContext = (PageFlowActionContext) invocation.getInvocationContext();
        Forward currentForward = actionContext.getForward();
        assert currentForward != null : "no forward found in context for Result \"" + getName() + '"';

        PageFlowResult prevResult = prevPageInfo.getResult();
        Forward previousForward = prevPageInfo.getForward();
        prevResult.initFrom(currentForward, actionContext, false);

        if (previousForward != null) {
            prevResult.applyForward(previousForward, actionContext);
        }

        prevResult.applyForward(currentForward, actionContext);
        actionContext.setPreviousPageInfo(prevPageInfo);
        prevResult.finishExecution(currentForward, actionContext);
    }

    /*
    // TODO: re-add logic for merging query strings (abstracted into a Servlet-specific version?)
    //
    // If there's a query string, override the previous query string.
    //
    String fwdPath = retFwd.getPath();
    String newQueryString = fwd.getQueryString();
    int existingQueryPos = fwdPath.indexOf( '?' );

    //
    // If the new forward (the one with ti.NavigateTo.currentPage/previousPage) has a query string, use that.
    // Otherwise, if the old forward has no query string, restore the one from the PreviousPageInfo if
    // appropriate.
    //
    if ( newQueryString != null )
    {
        // Chop off the old query string if necessary.
        if ( existingQueryPos != -1 ) fwdPath = fwdPath.substring( 0, existingQueryPos );
        retFwd.setPath( fwdPath + newQueryString );
    }
    else if ( existingQueryPos == -1 )
    {
        retFwd.setPath( fwdPath + getQueryString( fwd, prevPageInfo ) );
    }

    */
    /*
    protected Forward applyForward(Forward fwd, ModuleConfig altModuleConfig) {
        //
        // We need access to previousPageInfo from the *current page flow*.  That is often the current FlowController
        // in the ActionConteext, but if it's a shared flow, then we don't want to use that.
        //
        PageFlowController curJpf = PageFlowUtils.getCurrentPageFlow();

        if ( curJpf == null )
        {
            FlowControllerException ex = new NoCurrentPageFlowException( this );
            InternalUtils.throwPageFlowException( ex);
            assert false;   // throwPageFlowException() must throw.
        }

        PreviousPageInfo prevPageInfo;

        switch ( getPreviousPageIndex() )
        {
            case 0:
                prevPageInfo = curJpf.getCurrentPageInfo();
                break;

            case 1:
                prevPageInfo = curJpf.getPreviousPageInfo();
                break;

            default:
                assert false : getPreviousPageIndex() + " is not a valid previous-page index";
                    // of course, in the future, we should support any index, up to an app-configured max
                prevPageInfo = curJpf.getCurrentPageInfo();
        }

        Forward retFwd = doReturnToPage(fwd, prevPageInfo, curJpf);

        if ( prevPageInfo != null )
        {
            PageFlowActionContext actionContext = PageFlowActionContext.getContext();
            //mapping = prevPageInfo.getAction();
            //if ( form == null ) form = prevPageInfo.getFormBean();
        }

        if ( _log.isDebugEnabled() )
        {
            _log.debug( "navigate-to-page: " + ( fwd != null ? fwd.getPath() : "[null]" ) );
        }

        return retFwd;
    }

    protected Forward doReturnToPage( Forward fwd, PreviousPageInfo prevPageInfo, PageFlowController currentPageFlow)
    {
        if ( prevPageInfo == null )
        {
            if ( _log.isInfoEnabled() )
            {
                _log.info( "Attempted return-to-page, but previous page info was missing." );
            }

            FlowControllerException ex = new NoPreviousPageException( this, currentPageFlow );
            InternalUtils.throwPageFlowException( ex);
        }

        //
        // Figure out what URI to return to, and set the original form in the request or session.
        //
        Forward retFwd = prevPageInfo.getResult();
        PageFlowAction prevAction = prevPageInfo.getAction();
        PageFlowActionContext actionContext = PageFlowActionContext.getContext();

        //
        // Restore any forms that are specified by this forward (overwrite the original forms).
        //
        PageFlowUtils.setOutputForms( retFwd, false );
        InternalUtils.addActionOutputs( retFwd.getActionOutputs(), false );

        //
        // If the user hit the previous page directly (without going through an action), prevMapping will be null.
        //
        if ( prevAction != null )
        {
            //
            // If the currently-posted form is of the right type, initialize the page with that (but we don't overwrite
            // the form that was set above).
            //
            Object currentForm = actionContext.getAction().getFormBean();
            if ( currentForm != null ) PageFlowUtils.setOutputForm( currentForm, false );

            //
            // Initialize the page with the original form it got forwarded (but we don't overwrite the form that was
            // set above).
            //
            InternalUtils.setFormInScope( prevAction.getFormBeanAttribute(), prevPageInfo.getFormBean(), false );
        }

        //
        // If we're forwarding to a page in a different pageflow, we need to make sure the returned forward has
        // the right namespace, and that it has contextRelative=true.
        //
        FlowController flowController = actionContext.getFlowController();

        if ( ! retFwd.getPath().startsWith( "/" ) && flowController != currentPageFlow )
        {
            assert false : "NYI";
            retFwd = new forward( retFwd.getName(),
                                        '/' + currentPageFlow.getNamespace() + '/' + retFwd.getPath(),
                                        retFwd.isRedirect(),
                                        true );

        }

        if ( _log.isDebugEnabled() )
        {
            _log.debug( "Return-to-page in PageFlowController " + flowController.getClass().getName()
                       + ": original URI " + retFwd.getPath() );
        }

        if ( retFwd != null )
        {
            //
            // If the new (return-to) forward specifies a redirect value explicitly, use that; otherwise
            // use the redirect value from the original forward.
            //
            if ( ! hasExplicitRedirectValue() ) setRedirect( fwd.isRedirect() );

            //
            // If there's a query string, override the previous query string.
            //
            String fwdPath = retFwd.getPath();
            String newQueryString = fwd.getQueryString();
            int existingQueryPos = fwdPath.indexOf( '?' );

            //
            // If the new forward (the one with ti.NavigateTo.currentPage/previousPage) has a query string, use that.
            // Otherwise, if the old forward has no query string, restore the one from the PreviousPageInfo if
            // appropriate.
            //
            if ( newQueryString != null )
            {
                // Chop off the old query string if necessary.
                if ( existingQueryPos != -1 ) fwdPath = fwdPath.substring( 0, existingQueryPos );
                retFwd.setPath( fwdPath + newQueryString );
            }
            else if ( existingQueryPos == -1 )
            {
                retFwd.setPath( fwdPath + getQueryString( fwd, prevPageInfo ) );
            }
        }


        actionContext.setPreviousPageInfo( prevPageInfo );
        return retFwd;
    }
    */
    protected boolean shouldSavePreviousPageInfo() {
        return _previousPageIndex > 0;
    }

    public String getNavigateToAsString() {
        return (_previousPageIndex > 0) ? "ti.NavigateTo.previousPage" : "ti.NavigateTo.currentPage"; // TODO: constant
    }

    public boolean isPath() {
        return false;
    }

    public int getPreviousPageIndex() {
        return _previousPageIndex;
    }

    public void setPreviousPageIndex(int previousPageIndex) {
        _previousPageIndex = previousPageIndex;
    }
}
