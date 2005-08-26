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




/**
 * Base class for form beans associated with action methods in {@link PageFlowController}s.  Note that Page Flow actions
 * may take form beans of any type.
 */
public class FormData {

    /* TODO: re-add the functionality in this class
    
    private static final Logger _log = Logger.getInstance( FormData.class );
    
    //
    // This is used to allow us to run against Validator 1.0 or 1.1.  The reflective Method is only used when running
    // against Validator 1.0 (legacy).
    //
    private static Method _legacyInitValidatorMethod = null;
    
    static
    {
        try
        {
            _legacyInitValidatorMethod = 
                    Resources.class.getMethod( "initValidator",
                                               new Class[]{
                                                   String.class,
                                                   Object.class,
                                                   ServletContext.class,
                                                   HttpServletRequest.class,
                                                   ActionErrors.class,
                                                   int.class
                                               } );
        }
        catch ( NoSuchMethodException e )
        {
            // ignore -- we're in Validator 1.1 or later.
        }
    }
    
    public FormData()
    {
        super();
    }

    public ActionErrors validate( ActionMapping mapping, HttpServletRequest request )
    {
        return validateBean( this, mapping.getAttribute(), mapping, request );
    }

    //MessageResources that conglomerates a primary and backup MessageResources.
    private static class MergedMessageResources
            extends MessageResources
    {
        private MessageResources _primary;
        private MessageResources _backup;

        public MergedMessageResources( MessageResources primary, MessageResources backup )
        {
            super( primary.getFactory(), primary.getConfig(), primary.getReturnNull() );
            _primary = primary;
            _backup = backup;
        }

        public String getMessage( Locale locale, String key )
        {
            String message = _primary.getMessage( locale, key );
            if ( message == null ) message = _backup.getMessage( locale, key );
            return message;
        }
    }
    
    /**
     * Run all validation (declarative validation from annotations and the result of {@link Validatable#validate}) on
     * a given bean.
     * 
     * @param bean the bean to validate.
     * @param beanName the name of the bean, to be passed to Validator to look up declarative validation rules.
     * @param mapping the current ActionMapping.
     * @param request the current HttpServletRequest.
     * @return an ActionErrors object containing errors that occurred during bean validation.
     * 
    protected ActionErrors validateBean( Object bean, String beanName, ActionMapping mapping, HttpServletRequest request )
    {
        MessageResources messageResources = ( MessageResources ) request.getAttribute( Globals.MESSAGES_KEY );
        ExpressionAwareMessageResources.update( messageResources, bean );
        
        //
        // See if this action uses a form that defines its own message resources.  If so, use those, or combine them
        // with the message resources from the current module.
        //
        if ( mapping instanceof PageFlowAction )
        {
            PageFlowAction pfam = ( PageFlowAction ) mapping;
            String bundle = pfam.getFormBeanMessageResourcesKey();
            
            if ( bundle != null )
            {
                MessageResources formBeanResources = ( MessageResources ) request.getAttribute( bundle );
                ExpressionAwareMessageResources.update( formBeanResources, bean );
                
                if ( formBeanResources != null )
                {
                    if ( messageResources != null )
                    {
                        formBeanResources = new MergedMessageResources( messageResources, formBeanResources );
                    }
                    
                    request.setAttribute( Globals.MESSAGES_KEY, formBeanResources );
                    messageResources = formBeanResources;
                }
            }
        }
        
        //
        // If there's no MessageResources for this request, create one that can evaluate expressions.
        //
        if ( messageResources == null )
        {
            messageResources = new ExpressionAwareMessageResources( bean, request, getServlet().getServletContext() );
            request.setAttribute( Globals.MESSAGES_KEY, messageResources );
        }
        
        
        ServletContext servletContext = getServlet().getServletContext();
        ActionErrors errors = new ActionErrors();

        //
        // If the ValidatorPlugIn was initialized for this module, run it.
        //
        if ( Resources.getValidatorResources( servletContext, request ) != null )
        {
            try
            {
                //
                // Run validations associated with the bean.
                //
                Validator beanV = initValidator( beanName, bean, servletContext, request, errors, page );
                validatorResults = beanV.validate();
                
                //
                // Run validations associated with the action.
                //
                Validator actionV = initValidator( mapping.getPath(), bean, servletContext, request, errors, page );
                validatorResults.merge( actionV.validate() );
            }
            catch ( ValidatorException e )
            {
                _log.error( e.getMessage(), e );
            }
        }

        //
        // If this bean implements our Validatable interface, run its validate method.
        //
        if ( bean instanceof Validatable )
        {
            ( ( Validatable ) bean ).validate( mapping, request, errors );
        }
        
        return errors;
    }
    
    private static Validator initValidator( String beanName, Object bean, ServletContext context, 
                                            HttpServletRequest request, ActionErrors errors, int page )
    {
        if ( _legacyInitValidatorMethod != null )
        {
            try
            {
                Object[] args = new Object[]{ beanName, bean, context, request, errors, new Integer( page ) };
                Validator validator = ( Validator ) _legacyInitValidatorMethod.invoke( Resources.class, args );
                
                //
                // The NetUI validator rules work on both 1.1 and 1.2.  They take ActionMessages instead of ActionErrors.
                //
                validator.addResource( "org.apache.struts.action.ActionMessages", errors );
                return validator;
            }
            catch ( IllegalAccessException e )
            {
                assert false : e.getMessage();
                throw new RuntimeException( e );
            }
            catch ( InvocationTargetException e )
            {
                assert false : e.getMessage();
                throw new RuntimeException( e );
            }
        }
        else
        {
            return Resources.initValidator( beanName, bean, context, request, errors, page );
        }
    }
    */
}
