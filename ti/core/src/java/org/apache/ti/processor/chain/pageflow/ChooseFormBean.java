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
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.util.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class ChooseFormBean implements Command {
    
    private static final Logger _log = Logger.getInstance(ChooseFormBean.class);

    public boolean execute(Context context) throws Exception {
        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        Object formBean = getFormBean(actionContext.getAction());
        actionContext.setFormBean(formBean);
        return false;
    }
    
    private Object getFormBean(PageFlowAction action) {
        //
        // See if we're using a pageflow-scoped form (a member variable in the current pageflow).
        //
        Field formMemberField = getPageFlowScopedFormMember(action);
        
        //
        // First look to see whether the input form was overridden in the request.
        // This happens when a pageflow action forwards to another pageflow,
        // whose begin action expects a form.  In this case, the form is already
        // constructed, and shouldn't be instantiated anew or populated from request
        // parameters.
        //
        Object previousForm = InternalUtils.getForwardedFormBean( false );
        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        
        if ( previousForm != null )
        {
            //
            // If there was a forwarded form, and if this action specifies a pageflow-scoped form member,
            // set the member with this form.
            //
            if ( formMemberField != null )
            {
                try
                {
                    FlowController fc = actionContext.getFlowController();
                    assert fc != null : "no FlowController in request " + actionContext.getRequestPath();
                    formMemberField.set( fc, previousForm );
                }
                catch ( IllegalAccessException e )
                {
                    _log.error( "Could not access page flow member " + formMemberField.getName()
                            + " as the form bean.", e );
                }
            }
            
            //
            // Return the forwarded form.
            //
            return previousForm;
        }
        
        //
        // First see if the previous action put a pageflow-scoped form in the request.  If so, remove it;
        // we don't want a normal request-scoped action to use this pageflow-scoped form.
        //
        String pageFlowScopedFormName = actionContext.getPageFlowScopedFormName();
        Map requestScope = actionContext.getWebContext().getRequestScope();
        if ( pageFlowScopedFormName != null )
        {
            requestScope.remove( pageFlowScopedFormName );
            actionContext.setPageFlowScopedFormName( null );
        }
        
        //
        // If this action specifies a pageflow-scoped form member variable, use it.
        //
        if ( formMemberField != null )
        {
            try
            {
                FlowController fc = actionContext.getFlowController();
                Object form = formMemberField.get( fc );
                
                if ( form == null ) // the pageflow hasn't filled the value yet
                {
                    form = createActionForm(action);
                    
                    // TODO: make an interface for reset(), and call reset.
                    // form.reset( mapping, request );
                    formMemberField.set( fc, form );
                }
                
                //
                // Store the form in the right place in the request, so Struts can see it.
                // But, mark a flag so we know to remove this on the next action request -- we don't
                // want this form used by another action unless that action uses the pageflow-scoped
                // form.
                //
                String formAttrName = action.getFormBeanAttribute();
                requestScope.put( formAttrName, form );
                actionContext.setPageFlowScopedFormName( formAttrName );
                return form;
            }
            catch ( IllegalAccessException e )
            {
                _log.error( "Could not access page flow member " + formMemberField.getName() + " as the form bean.", e );
            }
        }
        
        Object bean = createActionForm(action);
        return bean;
    }
    
    private static Object createActionForm(PageFlowAction action)
    {
        String formBeanType = action.getFormBeanType();
        if (formBeanType != null) {
            try {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Creating new ActionForm instance of type " + formBeanType );
                }
               
                return Handlers.get().getReloadableClassHandler().newInstance(formBeanType);
            }
            catch ( Exception e )
            {
                // Can be any exception -- not just the reflection-related exceptions...
                // because the exception could be thrown from the bean's constructor.
                if ( _log.isErrorEnabled() )
                {
                    _log.error( "Error while creating form-bean object of type " + formBeanType, e );
                }
            }
        }
        
        return null;
    }
        
    private Field getPageFlowScopedFormMember(PageFlowAction action)
    {
        String formMember = action.getFormMember();
        
        try
        {
            if ( formMember != null )
            {
                FlowController fc = PageFlowActionContext.get().getFlowController();
                Field field = fc.getClass().getDeclaredField( formMember );
                if ( ! Modifier.isPublic( field.getModifiers() ) ) field.setAccessible( true );
                return field;
            }
        }
        catch ( Exception e )
        {
            _log.error( "Could not use page flow member " + formMember + " as the form bean.", e );
        }
        
        return null;
    }
    
     
}
