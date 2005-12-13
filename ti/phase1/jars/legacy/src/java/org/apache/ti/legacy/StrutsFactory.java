/*
 * $Id$
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
 */

package org.apache.ti.legacy;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.apache.struts.action.*;
import org.apache.struts.config.*;
import java.util.*;


/**
 *  Provides conversion methods between the Struts Action 1.x and XWork
 *  classes.
 */
public class StrutsFactory {
    private static StrutsFactory FACTORY = new StrutsFactory();

    protected StrutsFactory() {
    }

    public static void setStrutsFactory(StrutsFactory factory) {
        FACTORY = factory;
    }

    public static StrutsFactory getStrutsFactory() {
        return FACTORY;
    }

    public ModuleConfig createModuleConfig() {
        return null;
    }

    public ActionMapping createActionMapping(ActionConfig cfg) {
        return null;
    }

    public void convertErrors(ActionErrors errors, Object action) {
        ValidationAware vaction = null;
        TextProvider text = null;
        
        if (action instanceof ValidationAware) {
            vaction = (ValidationAware)action;
        }
        if (action instanceof TextProvider) {
            text = (TextProvider)action;
        }

        ActionMessage error = null;
        String field = null;
        String msg = null;
        Object[] values = null;
        for (Iterator i = errors.properties(); i.hasNext(); ) {
            field = (String) i.next();
            for (Iterator it = errors.get(field); it.hasNext(); ) {
                error = (ActionMessage) it.next();
                msg = error.getKey();
                if (error.isResource() && text != null) {
                    values = error.getValues();
                    if (values != null) {
                        msg = text.getText(error.getKey(), Arrays.asList(values));
                    } else {
                        msg = text.getText(error.getKey());
                    }    
                } 
                if (vaction != null) {
                    if (field == errors.GLOBAL_MESSAGE) {
                        vaction.addActionError(msg);
                    } else {
                        vaction.addFieldError(field, msg);
                    }
                } else {
                    // should do something here
                }
            }
        }
    }    
}
