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
package org.apache.ti.pageflow;

import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.util.logging.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * The Page Flow extension of the Struts RequestProcessor, which contains callbacks that are invoked
 * during processing of a request to the Struts action servlet.  This class is registered as the
 * <strong>controller</strong> for all Struts modules derived from page flows.
 */
public class PageFlowRequestProcessor
        implements Serializable, InternalConstants, PageFlowConstants {

    private static int requestNumber = 0;

    private static final Logger _log = Logger.getInstance(PageFlowRequestProcessor.class);

    private static final String ACTION_OVERRIDE_PARAM_PREFIX = "actionOverride:";
    private static final int ACTION_OVERRIDE_PARAM_PREFIX_LEN = ACTION_OVERRIDE_PARAM_PREFIX.length();
    private static final String SCHEME_UNSECURE = "http";
    private static final String SCHEME_SECURE = "https";
    private static final String REDIRECT_REQUEST_ATTRS_PREFIX = InternalConstants.ATTR_PREFIX + "requestAttrs:";
    private static final String REDIRECT_REQUEST_ATTRS_PARAM = "forceRedirect";


    private Map/*< String, Class >*/ _formBeanClasses = new HashMap/*< String, Class >*/();
    // private Map/*< String, List< PageFlowAction > >*/ _overloadedActions = new HashMap/*< String, List< PageFlowAction > >*/();
    private ContainerAdapter _containerAdapter;
    private Handlers _handlers;
    private FlowControllerFactory _flowControllerFactory;
    //private InternalConcurrentHashMap/*< String, Class >*/ _pageServletClasses = new InternalConcurrentHashMap/*< String, Class >*/();
    //private PageFlowPageFilter _pageServletFilter;
    
    /*
    protected void processPopulate( HttpServletRequest request, HttpServletResponse response, Object form,
                                    PageFlowAction mapping )
        throws PageFlowException
    {
        //
        // If a previous action forwarded us a form, use that -- don't populate it from request parameters.
        //
        Object previousForm = InternalUtils.getForwardedFormBean( request, true );

        if ( previousForm != null )
        {
            return;
        }

        if ( _log.isDebugEnabled() )
        {
            _log.debug( "Populating bean properties from this request" );
        }

        // struts does this
        if ( form != null )
        {
            form.setServlet( servlet );
            form.reset( mapping, request );
        }

        if ( mapping.getMultipartClass() != null )
        {
            request.setAttribute( Globals.MULTIPART_KEY, mapping.getMultipartClass() );
        }

        PageFlowActionContext requestWrapper = getContext();
        boolean alreadyCalledInRequest = requestWrapper.isProcessPopulateAlreadyCalled();
        if ( ! alreadyCalledInRequest ) requestWrapper.setProcessPopulateAlreadyCalled( true );
        
        //
        // If this is a forwarded request and the form-bean is null, don't call to ProcessPopulate.
        // We don't want to expose errors due to parameters from the original request, which won't
        // apply to a forwarded action that doesn't take a form.
        //
        if ( !alreadyCalledInRequest || form != null )
        {
            //
            // If this request was forwarded by a button-override of the main form action, then ensure that there are
            // no databinding errors when the override action does not use a form bean.
            //
            if ( form == null && requestWrapper.isForwardedByButton() ) form = NULL_ACTION_FORM;
            
            ProcessPopulate.populate( request, response, form, alreadyCalledInRequest );
        }
    }
    */

    /**
     * The requested action can be overridden by a request parameter.  In this case, we parse the action from
     * the request parameter and forward to a URI constructed from it.
     * 
     * @return <code>true</code> if the action was overridden by a request parameter, in which case the request
     *         was forwarded.
     * @throws IOException
     * @throws PageFlowException    
     */ 
    /* TODO: re-add this -- it should be in Chain
    protected boolean processActionOverride()
        throws IOException, PageFlowException
    {
        // Only make this check if this is an initial (non-forwarded) request.
        //
        // TODO: performance?
        //
        PageFlowActionContext wrapper = getContext();
        if ( ! wrapper.isForwardedByButton() && ! wrapper.isForwardedRequest() )
        {
            //
            // First, since we need access to request parameters here, process a multipart request
            // if that's what we have.  This puts the parameters (each in a MIME part) behind an
            // interface that makes them look like normal request parameters.
            //
            // TODO: re-add multipart support
            // HttpServletRequest multipartAwareRequest = processMultipart( request );
            
            
            for ( Iterator i = getContext().getWebContext().getRequestScope().keySet().iterator(); i.hasNext(); )
            {
                String paramName = ( String ) i.next();

                if ( paramName.startsWith( ACTION_OVERRIDE_PARAM_PREFIX ) )
                {
                    String actionPath = paramName.substring( ACTION_OVERRIDE_PARAM_PREFIX_LEN );

                    String qualifiedAction = InternalUtils.qualifyAction( actionPath );
                    actionPath = InternalUtils.createActionPath(qualifiedAction );

                    if ( _log.isDebugEnabled() )
                    {
                        _log.debug( "A request parameter overrode the action.  Forwarding to: " + actionPath );
                    }

                    wrapper.setForwardedByButton( true );
                    doForward( actionPath );
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void processSecurity()
    {
        PageFlowActionContext context = PageFlowActionContext.getContext();        
        Map requestScope = context.getWebContext().getRequestScope();
        String uri = context.getRequestPath();
        
        //
        // Allow the container to do a security check on forwarded requests, if that feature is enabled.
        //
        PageflowConfig pageflowConfig = ConfigUtil.getConfig().getPageflowConfig();
        if ( pageflowConfig != null && pageflowConfig.getEnsureSecureForwards() && getContext().isForwardedRequest() )
        {
            //
            // In some situations (namely, in scoped requests under portal), the initial
            // security check may not have been done for the request URI.  In this case, a redirect
            // to https may happen during checkSecurity().
            //
            if ( _containerAdapter.doSecurityRedirect( uri) )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "checkSecurity() caused a redirect.  Ending processing for this request "
                            + '(' + uri + ')' );
                }
                
                return;
            }
        }
        
        //
        // If we've come in on a forced redirect due to security constraints, look for request attrs
        // that we put into the session.
        //
        String hash = ( String ) requestScope.get( REDIRECT_REQUEST_ATTRS_PARAM );
        if ( hash != null )
        {
            Map sessionScope = getContext().getSession();
            
            if ( sessionScope != null )
            {
                String carryoverAttrName = makeRedirectedRequestAttrsKey( uri, hash );
                Map attrs = ( Map ) sessionScope.get( carryoverAttrName );
                sessionScope.remove( carryoverAttrName );
                
                if ( attrs != null )
                {
                    for ( Iterator i = attrs.entrySet().iterator(); i.hasNext(); )
                    {
                        Map.Entry entry = ( Map.Entry ) i.next();
                        
                        String attrName = ( String ) entry.getKey();
                        if ( requestScope.get( attrName ) == null )
                        {
                            requestScope.put( attrName, entry.getValue() );
                        }
                    }
                }
            }
        }
    }
    */
    
    /**
     * Process any direct request for a page flow by forwarding to its "begin" action.
     * 
     * @param request the current HttpServletRequest
     * @param response the current HttpServletResponse
     * @param uri the decoded request URI
     * @return <code>true</code> if the request was for a page flow, in which case it was forwarded.
     * @throws IOException
     * @throws PageFlowException
     */ 
    /* TODO: re-add
    protected boolean processPageFlowRequest( HttpServletRequest request, HttpServletResponse response, String uri )
        throws IOException, PageFlowException
    {
        //
        // forward requests for *.jpf to the "begin" action within the appropriate Struts module.
        //
        if ( FileUtils.osSensitiveEndsWith( uri, PageFlowConstants.PAGEFLOW_EXTENSION ) )
        {
            //
            // Make sure the current module config matches the request URI.  If not, this could be an
            // EAR where the jpf-struts-config.xml wasn't included because of a compilation error.
            //
            String modulePath = PageFlowUtils.getModulePath( request );
            if ( ! moduleConfig.getPrefix().equals( modulePath ) )
            {
                if ( _log.isErrorEnabled() )
                {
                    InternalStringBuilder msg = new InternalStringBuilder( "No module configuration registered for " );
                    msg.append( uri ).append( " (namespace " ).append( namespace ).append( ")." );
                    _log.error( msg.toString() );
                }

                if ( modulePath.length() == 0 ) modulePath = "/";
                InternalUtils.sendDevTimeError( "PageFlow_NoModuleConf", null,
                                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, request, response,
                                                getServletContext(), new Object[]{ uri, modulePath } );
                return true;
            }

            //
            // Make sure that the requested pageflow matches the pageflow for the directory.
            //
            PageFlowAction beginMapping = getBeginMapping();
            if ( beginMapping != null )
            {
                String desiredType = beginMapping.getParameter();
                desiredType = desiredType.substring( desiredType.lastIndexOf( '.' ) + 1 ) + JPF_EXTENSION;
                String requestedType = InternalUtils.getRequestPath( request );
                requestedType = requestedType.substring( requestedType.lastIndexOf( '/' ) + 1 );

                if ( ! requestedType.equals( desiredType ) )
                {
                    if ( _log.isDebugEnabled() )
                    {
                        _log.debug( "Wrong .jpf requested for this directory: got " + requestedType
                                   + ", expected " + desiredType );
                    }

                    if ( _log.isErrorEnabled() )
                    {
                        InternalStringBuilder msg = new InternalStringBuilder( "Wrong .jpf requested for this directory: got " );
                        msg.append( requestedType ).append( ", expected " ).append( desiredType ).append( '.' );
                        _log.error( msg.toString() );
                    }

                    InternalUtils.sendDevTimeError( "PageFlow_WrongPath", null,
                                                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, request, response,
                                                    getServletContext(), new Object[]{ requestedType, desiredType } );

                    return true;
                }
            }

            uri = PageFlowUtils.getBeginActionURI( uri );

            if ( _log.isDebugEnabled() )
            {
                _log.debug( "Got request for " + request.getRequestURI() + ", forwarding to " + uri );
            }

            doForward( uri, request, response );
            return true;
        } 
        
        return false;
    }
    
    protected PageFlowAction getBeginMapping()
    {
    return ( PageFlowAction ) moduleConfig.findActionConfig( BEGIN_ACTION_PATH );
    }
    
    */
    
    /**
     * A MultipartRequestWrapper that we cache in the outer request once we've handled the multipart request once.
     * It extends the base Struts MultipartRequestWrapper by being aware of ScopedRequests; for ScopedRequests, it
     * filters the parameter names accordingly.
     */ 
    /* TODO: re-add
    private static class RehydratedMultipartRequestWrapper extends MultipartRequestWrapper
    {
        public RehydratedMultipartRequestWrapper( HttpServletRequest req )
        {
            super( req );
             
            MultipartRequestHandler handler = MultipartRequestUtils.getCachedMultipartHandler( req );
             
            if ( handler != null )
            {
                ScopedRequest scopedRequest = ScopedUtils.unwrapRequest( req );
                Map textElements = handler.getTextElements();
                parameters = scopedRequest != null ? scopedRequest.filterParameterMap( textElements ) : textElements;
            }
        }
    }
    */
    
    /**
     * If this is a multipart request, wrap it with a special wrapper.  Otherwise, return the request unchanged.
     */
    /* TODO: re-add multipart support
    protected HttpServletRequest processMultipart( HttpServletRequest request )
    {
        if ( ! "POST".equalsIgnoreCase( request.getMethod() ) ) return request;

        String contentType = request.getContentType();
        if ( contentType != null && contentType.startsWith( "multipart/form-data" ) )
        {
            PageFlowActionContext pageFlowRequestWrapper = getContext();
            
            //
            // We may have already gotten a multipart wrapper during process().  If so, use that.
            //
            MultipartRequestWrapper cachedWrapper = pageFlowRequestWrapper.getMultipartRequestWrapper();
            
            if ( cachedWrapper != null && cachedWrapper.getRequest() == request ) return cachedWrapper;
            
            try
            {
                //
                // First, pre-handle the multipart request.  This parses the stream and caches a single
                // MultipartRequestHandler in the outer request, so we can create new wrappers around it at will.
                //
                MultipartRequestUtils.preHandleMultipartRequest( request );
            }
            catch ( PageFlowException e )
            {
                _log.error( "Could not parse multipart request.", e.getRootCause() );
                return request;
            }
            
            MultipartRequestWrapper ret = new RehydratedMultipartRequestWrapper( request );
            pageFlowRequestWrapper.setMultipartRequestWrapper( ret );
            return ret;
        }
        else
        {
            return request;
        }

    }
    */

    /*
    private boolean isCorrectFormType( Class formBeanClass, PageFlowAction mapping )
    {
        assert mapping.getName() != null : "cannot pass an PageFlowAction that has no form bean";
        Class cachedFormBeanClass = ( Class ) _formBeanClasses.get( mapping.getName() );
        return isCorrectFormType( formBeanClass, cachedFormBeanClass, mapping );
    }
    
    private boolean isCorrectFormType( Class formBeanClass, Class actionMappingFormBeanClass, PageFlowAction mapping )
    {
        if ( actionMappingFormBeanClass != null )
        {
            return actionMappingFormBeanClass .isAssignableFrom( formBeanClass );
        }
        else
        {
            //
            // The form bean class couldn't be loaded at init time -- just check against the class name.
            //
            FormBeanConfig mappingFormBean = moduleConfig.findFormBeanConfig( mapping.getName() );
            String formClassName = formBeanClass.getName();
            
            if ( mappingFormBean != null && mappingFormBean.getType().equals( formClassName ) ) return true;
            
            if ( mapping instanceof PageFlowAction )
            {
                String desiredType = ( ( PageFlowAction ) mapping ).getFormBeanClass();
                if ( formClassName.equals( desiredType ) ) return true;
            }
        }
        
        return false;
    }
    */

    /* TODO: re-add tokens, or something like it.
    private PageFlowAction checkTransaction( HttpServletRequest request, HttpServletResponse response,
                                            PageFlowAction mapping, String actionPath )
        throws IOException
    {
        if ( mapping instanceof PageFlowAction && ( ( PageFlowAction ) mapping ).isPreventDoubleSubmit() )
        {
            if ( ! TokenProcessor.getInstance().isTokenValid( request, true ) )
            {
                FlowController currentFC = getContext().getCurrentFlowController();
                String actionName = InternalUtils.getActionName( mapping );
                DoubleSubmitException ex = new DoubleSubmitException( actionName, currentFC );
                
                if ( currentFC != null )
                {
                    try
                    {
                        forward fwd = currentFC.handleException( ex, mapping, null, request, response );
                        return new ExceptionHandledPageFlowAction( actionPath, fwd );
                    }
                    catch ( PageFlowException servletException)
                    {
                        _log.error( "Exception occurred while handling " + ex.getClass().getName(), servletException );
                    }
                }
                
                ex.sendResponseErrorCode( response );
                return null;
            }
        }
        
        return mapping;
    }
    */
    public void init()
            throws PageFlowException {
        //
        // Cache a list of overloaded actions for each overloaded action path (actions are overloaded by form bean type).
        //
        // TODO: re-add overloaded action support
//        cacheOverloadedPageFlowActions();
        
        //
        // Cache the form bean Classes by form bean name.
        //
        // TODO: re-add class caching?
//        cacheFormClasses();
    }
    
    /* TODO: re-add overloaded action support
    private void cacheOverloadedPageFlowActions()
    {
        ActionConfig[] actionConfigs = moduleConfig.findActionConfigs();
        
        for ( int i = 0; i < actionConfigs.length; i++ )
        {
            ActionConfig actionConfig = actionConfigs[i];
            
            if ( actionConfig instanceof PageFlowAction )
            {
                PageFlowAction mapping = ( PageFlowAction ) actionConfig;
                String unqualifiedActionPath = ( ( PageFlowAction ) actionConfig ).getUnqualifiedActionPath();
                
                if ( unqualifiedActionPath != null )
                {
                    List overloaded = ( List ) _overloadedActions.get( unqualifiedActionPath );
                    
                    if ( overloaded == null )
                    {
                        overloaded = new ArrayList();
                        _overloadedActions.put( unqualifiedActionPath, overloaded );
                    }
                    
                    overloaded.add( mapping );
                }
            }
        }
    }
    */
    
    /*
    private void cacheFormClasses()
    {
        FormBeanConfig[] formBeans = moduleConfig.findFormBeanConfigs();
        ReloadableClassHandler rch = _handlers.getReloadableClassHandler();
        
        for ( int i = 0; i < formBeans.length; i++ )
        {
            FormBeanConfig formBeanConfig = formBeans[i];
            String formType = InternalUtils.getFormBeanType( formBeanConfig );
            
            try
            {
                Class formBeanClass = rch.loadClass( formType );
                _formBeanClasses.put( formBeanConfig.getName(), formBeanClass );
            }
            catch ( ClassNotFoundException e )
            {
                _log.error( "Could not load class " + formType + " referenced from form bean config "
                            + formBeanConfig.getName() + " in Struts module " + moduleConfig );
            }
        }
    }
    */

    /**
     * Read component instance mapping configuration file.
     * This is where we read files properties.
     */
    
    /* TODO: re-add Tiles support
    protected void initDefinitionsMapping() throws PageFlowException
    {
        definitionsFactory = null;
        TilesUtilImpl tilesUtil = TilesUtil.getTilesUtil();

        if ( tilesUtil instanceof TilesUtilStrutsImpl )
        {
            // Retrieve and set factory for this modules
            definitionsFactory =
                    ( ( TilesUtilStrutsImpl ) tilesUtil ).getDefinitionsFactory( getServletContext(), moduleConfig );

            if ( definitionsFactory == null && log.isDebugEnabled() )
            {
                log.debug( "Definition Factory not found for module: '"
                           + moduleConfig.getPrefix() );
            }
        }
    }
    */

    /* TODO: re-add this.  It's code to customize XWork -- to handle shared flow and form-bean-specific actions.
    public PageFlowAction processMapping( HttpServletRequest request, HttpServletResponse response, String path )
        throws IOException
    {
        FlowController fc = getContext().getFlowController();
        Object forwardedForm = InternalUtils.getForwardedFormBean( false );
        
        //
        // First, see if this is a request for a shared flow action.  The shared flow's name (as declared by the
        // current page flow) will precede the dot.
        //
        if ( fc != null && ! processSharedFlowMapping( request, response, path, fc ) ) return null;
        
        //
        // Look for a form-specific action path.  This is used when there are two actions with the same
        // name, but different forms (in nesting).
        //
        Class forwardedFormClass = null;
        
        if ( forwardedForm != null )
        {
            forwardedFormClass = forwardedForm.getClass();
            List possibleMatches = ( List ) _overloadedActions.get( path );
            PageFlowAction bestMatch = null;
            
            //
            // Troll through the overloaded actions for the given path.  Look for the one whose form bean class is
            // exactly the class of the forwarded form; failing that, look for one that's assignable from the class
            // of the forwarded form.
            //
            for ( int i = 0; possibleMatches != null && i < possibleMatches.size(); ++i )
            {
                PageFlowAction possibleMatch = ( PageFlowAction ) possibleMatches.get( i );
                Class cachedFormBeanClass = ( Class ) _formBeanClasses.get( possibleMatch.getName() );
                
                if ( forwardedFormClass.equals( cachedFormBeanClass ) )
                {
                    bestMatch = possibleMatch;
                    break;
                }
                if ( bestMatch == null && isCorrectFormType( forwardedFormClass, possibleMatch ) )
                {
                    bestMatch = possibleMatch;
                }
            }
            
            if ( bestMatch != null )
            {
                request.setAttribute( Globals.MAPPING_KEY, bestMatch );
                
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Found form-specific action mapping " + bestMatch.getPath() + " for " + path
                                + ", form " + forwardedFormClass.getName() );
                }
                
                return checkTransaction(bestMatch);
            }
        }
        
        //
        // Look for a directly-defined mapping for this path.
        //
        PageFlowAction mapping = ( PageFlowAction ) moduleConfig.findActionConfig( path );
        
        if ( mapping != null )
        {
            boolean wrongForm = false;
            
            //
            // We're going to bail out if there is a forwarded form and this mapping requires a different form type.
            //
            if ( forwardedForm != null )
            {
                boolean mappingHasNoFormBean = mapping.getName() == null;
                wrongForm = mappingHasNoFormBean || ! isCorrectFormType( forwardedFormClass, mapping );
            }
            
            if ( ! wrongForm )
            {
                request.setAttribute( Globals.MAPPING_KEY, mapping );
                return checkTransaction( request, response, mapping, path );
            }
        }

        //
        // getContext().getOriginalServletPath returns the request URI we had before trying to forward to an action
        // in a shared flow.
        //
        String errorServletPath = getContext().getOriginalServletPath();
        
        //
        // If the error action path has a slash in it, then it's not local to the current page flow.  Replace
        // it with the original servlet path.
        //
        if ( errorServletPath != null && path.indexOf( '/' ) > 0 ) path = errorServletPath;
        return processUnresolvedAction( path, request, response, forwardedForm );
    }
    
    protected boolean processSharedFlowMapping( HttpServletRequest request, HttpServletResponse response,
                                                String actionPath, FlowController currentFlowController )
            throws IOException
    {
        if ( currentFlowController.isPageFlow() )
        {
            int dot = actionPath.indexOf( '.' );
            
            if ( dot != -1 )
            {
                Map/*< String, SharedFlowController >* sharedFlows = PageFlowUtils.getSharedFlows( request );
                if ( sharedFlows == null ) return true;
                if ( dot == actionPath.length() - 1 ) return true;     // empty action name
                assert actionPath.length() > 0 && actionPath.charAt( 0 ) == '/' : actionPath;
                String sharedFlowName = actionPath.substring( 1, dot );
                SharedFlowController sf = ( SharedFlowController ) sharedFlows.get( sharedFlowName );
                
                if ( sf != null )
                {
                    if ( _log.isDebugEnabled() )
                    {
                        _log.debug( "Forwarding to shared flow " + sf.getDisplayName() + " to handle action \""
                                    + actionPath + "\"." );
                    }
                    
                    //
                    // Save the original request URI, so if the action fails on the shared flow, too, then we can
                    // give an error message that includes *this* URI, not the shared flow URI.
                    //
                    getContext().setOriginalServletPath( InternalUtils.getRequestPath( request ) );
                    
                    //
                    // Construct a URI that is [shared flow namespace] + [base action path] + [action-extension (.do)]
                    //
                    int lastSlash = actionPath.lastIndexOf( '/' );
                    assert lastSlash != -1 : actionPath;
                    InternalStringBuilder uri = new InternalStringBuilder( sf.getModulePath() );
                    uri.append( '/' );
                    uri.append( actionPath.substring( dot + 1 ) );
                    uri.append( ACTION_EXTENSION );
                    
                    try
                    {
                        doForward( uri.toString(), request, response );
                        return false;
                    }
                    catch ( PageFlowException e )
                    {
                        _log.error( "Could not forward to shared flow URI " + uri, e );
                    }
                }
            }
        }
        
        return true;
    }
    
    protected PageFlowAction processUnresolvedAction( String actionPath, HttpServletRequest request,
                                                     HttpServletResponse response, Object returningForm )
        throws IOException
    {
                if ( _log.isInfoEnabled() )
        {
            InternalStringBuilder msg = new InternalStringBuilder( "action \"" ).append( actionPath );
            _log.info( msg.append( "\" was also unhandled by Global.app." ).toString() );
        }
        
        //
        // If there's a PageFlowController for this request, try and let it handle an
        // action-not-found exception.  Otherwise, let Struts print out its "invalid path"
        // message.
        //
        FlowController fc = PageFlowUtils.getCurrentPageFlow( request );
        
        try
        {
            if ( fc != null )
            {
                Exception ex = new ActionNotFoundException( actionPath, fc, returningForm );
                InternalUtils.setCurrentModule( fc.getModuleConfig(), request );
                forward result = fc.handleException( ex, null, null, request, response );
                return new ExceptionHandledPageFlowAction( actionPath, result );
            }
        }
        catch ( PageFlowException e )
        {
            // ignore this -- just let Struts do its thing.
            
            if ( _log.isDebugEnabled() )
            {
                _log.debug( e );
            }
        }
                    
        if ( _log.isDebugEnabled() )
        {
            _log.debug( "Couldn't handle an ActionNotFoundException -- delegating to Struts" ); 
        }
        
        return super.processMapping( request, response, actionPath );
    }
    */
    
    /*
    protected boolean processRoles( HttpServletRequest request, HttpServletResponse response, PageFlowAction mapping )
        throws IOException, PageFlowException 
    {
        //
        // If there are no required roles for this action, just return.
        //
        String roles[] = mapping.getRoleNames();
        if ( roles == null || roles.length < 1 )
        {
            return true;
        }

        // Check the current user against the list of required roles
        FlowController fc = getContext().getCurrentFlowController();
        FlowControllerHandlerContext context = new FlowControllerHandlerContext( request, response, fc );
        
        for ( int i = 0; i < roles.length; i++ )
        {
            if ( _handlers.getLoginHandler().isUserInRole( context, roles[i] ) )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( " User " + request.getRemoteUser() + " has role '" + roles[i] + "', granting access" );
                }
                
                return true;
            }
        }

        // The current user is not authorized for this action
        if ( _log.isDebugEnabled() )
        {
            _log.debug( " User '" + request.getRemoteUser() + "' does not have any required role, denying access" );
        }
                
        //
        // Here, Struts sends an HTTP error.  We try to let the current page flow handle a relevant exception.
        //
        LoginHandler loginHandler = _handlers.getLoginHandler();
        String actionName = InternalUtils.getActionName( mapping );
        FlowController currentFC = getContext().getCurrentFlowController();
        FlowControllerException ex;
        
        if ( loginHandler.getUserPrincipal( context ) == null )
        {
            ex = currentFC.createNotLoggedInException( actionName, request );
        }
        else
        {
            ex = new UnfulfilledRolesException( mapping.getRoleNames(), mapping.getRoles(), actionName, currentFC );
        }
        
        if ( currentFC != null )
        {
            forward fwd = currentFC.handleException( ex, mapping, null, request, response );
            processForwardConfig( request, response, fwd );
        }
        else
        {
            ( ( ResponseErrorCodeSender ) ex ).sendResponseErrorCode( response );
        }
        
        return false;
    }
    
    private static String addScopeParams( String url, HttpServletRequest request )
    {
        //
        // If the current request is scoped, add the right request parameter to the URL.
        //
        String scopeID = request.getParameter( ScopedUtils.SCOPE_ID_PARAM );
        if ( scopeID != null )
        {
            return InternalUtils.addParam( url, ScopedUtils.SCOPE_ID_PARAM, scopeID );
        }
        else
        {
            return url;
        }
    }
    
    */


    /**
     * Set the no-cache headers.  This overrides the base Struts behavior to prevent caching even for the pages.
     */
    /*
    protected void processNoCache( HttpServletRequest request, HttpServletResponse response )
    {
        //
        // Set the no-cache headers if:
        //    1) the module is configured for it, or
        //    2) netui-config.xml has an "always" value for <pageflow-config><prevent-cache>, or
        //    3) netui-config.xml has an "inDevMode" value for <pageflow-config><prevent-cache>, and we're not in
        //       production mode.
        //
        boolean noCache = moduleConfig.getControllerConfig().getNocache();
        
        if ( ! noCache )
        {
            PageflowConfig pfConfig = ConfigUtil.getConfig().getPageflowConfig();
            
            if ( pfConfig != null )
            {
                PageflowConfig.PreventCache.Enum preventCache = pfConfig.getPreventCache();
                
                if ( preventCache != null )
                {
                    switch ( preventCache.intValue() )
                    {
                        case PageflowConfig.PreventCache.INT_ALWAYS:
                            noCache = true;
                            break;
                        case PageflowConfig.PreventCache.INT_IN_DEV_MODE:
                            noCache = ! _containerAdapter.isInProductionMode();
                            break;
                    }
                }
            }
        }
        
        if ( noCache )
        {
            //
            // The call to PageFlowPageFilter.preventCache() will cause caching to be prevented
            // even when we end up forwarding to a pagee.  Normally, no-cache headers are lost
            // when a server forward occurs.
            //
            ServletUtils.preventCache( response );
            PageFlowPageFilter.preventCache( request );
        }
    }
    */

}
