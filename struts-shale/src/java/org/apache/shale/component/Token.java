/*
 * Copyright 2004-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.component;

import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.apache.shale.faces.ShaleConstants;
import org.apache.shale.util.Messages;
import org.apache.shale.util.TokenProcessor;

/**
 * <p>Component that renders a transaction token input field, and then
 * validates it on a subsequent form submit.</p>
 *
 * $Id$
 */
public class Token extends UIInput {
    

    // -------------------------------------------------------- Static Variables


    /**
     * <p>Message resources for this class
     */
    private static Messages messages =
      new Messages("org.apache.shale.Bundle",
                   Token.class.getClassLoader());


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Create a default instance of this component.</p>
     */
    public Token() {
        setRendererType("org.apache.shale.Token");
    }


    // -------------------------------------------------------------- Properties


    /**
     * <p>Return the component family for this component.</p>
     */
    public String getFamiy() {
        return "org.apache.shale.Token";
    }


    // --------------------------------------------------------- UIInput Methods


    /**
     * <p>Perform superclass validations, then ensure that the specified input
     * value is acceptable at this point in time.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     */
    public void validate(FacesContext context) {

        super.validate(context);
        String token = (String) getSubmittedValue();
        TokenProcessor tp = getTokenProcessor(context);
        if (!tp.verify(context, token)) {
            setValid(false);
            String summary = messages.getMessage("token.invalid");
            FacesMessage message = new FacesMessage(summary);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
        }

    }


    /**
     * <p>Return the transaction token value to be rendered for this occcurrence
     * of this component.  As a side effect, the transaction token value will
     * be saved for verification on a subsequent submit.</p>
     */
    public Object getValue() {

        FacesContext context = FacesContext.getCurrentInstance();
        TokenProcessor tp = getTokenProcessor(context);
        return tp.generate(context);

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Retrieve the {@link TokenProcessor} instance for this application,
     * creating and caching a new one if necessary.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     */
     private TokenProcessor getTokenProcessor(FacesContext context) {

         TokenProcessor tp = (TokenProcessor) context.getExternalContext().
           getApplicationMap().get(ShaleConstants.TOKEN_PROCESSOR);
         if (tp == null) {
             tp = new TokenProcessor();
             context.getExternalContext().
               getApplicationMap().put(ShaleConstants.TOKEN_PROCESSOR, tp);
         }
         return tp;

     }


}
