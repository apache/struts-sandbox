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
import org.apache.ti.pageflow.internal.ProcessPopulate;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.internal.NullActionForm;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.logging.Logger;

import java.util.Map;

public class PopulateData implements Command {

    private static final Logger _log = Logger.getInstance(PopulateData.class);
    private static final NullActionForm NULL_ACTION_FORM = new NullActionForm();
    
    public boolean execute(Context context) throws Exception {
        //
        // If a previous action forwarded us a form, use that -- don't populate it from request parameters.
        //
        Object previousForm = InternalUtils.getForwardedFormBean( true );

        if ( previousForm != null ) {
            return false;
        }

        if ( _log.isDebugEnabled() )
        {
            _log.debug( "Populating bean properties from this request" );
        }

        /* TODO: re-add multipart support
        if ( mapping.getMultipartClass() != null )
        {
            request.setAttribute( Globals.MULTIPART_KEY, mapping.getMultipartClass() );
        }
        */

        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        boolean alreadyCalledInRequest = actionContext.isProcessPopulateAlreadyCalled();
        if ( ! alreadyCalledInRequest ) actionContext.setProcessPopulateAlreadyCalled( true );
        
        //
        // If this is a forwarded request and the form-bean is null, don't call to ProcessPopulate.
        // We don't want to expose errors due to parameters from the original request, which won't
        // apply to a forwarded action that doesn't take a form.
        //
        Object formBean = actionContext.getFormBean();
        if ( ! alreadyCalledInRequest || formBean != null )
        {
            //
            // If this request was forwarded by a button-override of the main form action, then ensure that there are
            // no databinding errors when the override action does not use a form bean.
            //
            if (formBean == null) {
                Map mappingParams = actionContext.getActionMapping().getParams();
                if (mappingParams != null && mappingParams.get(InternalConstants.ORIGINAL_ACTION_KEY) != null) {
                    formBean = NULL_ACTION_FORM;
                }
            }
            
            ProcessPopulate.populate( formBean, alreadyCalledInRequest );
        }    
        
        return false;
    }
}
