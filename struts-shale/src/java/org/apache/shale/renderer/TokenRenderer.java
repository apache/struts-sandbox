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

package org.apache.shale.renderer;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.apache.shale.component.Token;

/**
 * <p>Renderer for a {@link Token} component, dealing with a transaction
 * token used to catch duplicate form submits.</p>
 *
 * $Id$
 */
public class TokenRenderer extends Renderer {
    
    
    // -------------------------------------------------------- Renderer Methods


    /**
     * <p>Save the submitted value if this component was actually rendered.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component <code>UIComponent</code> to be decoded
     */
    public void decode(FacesContext context, UIComponent component) {

        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        Token token = (Token) component;
        if (token.isRendered()) {
            String clientId = token.getClientId(context);
            Map map = context.getExternalContext().getRequestParameterMap();
            token.setSubmittedValue(map.get(clientId));
        }

    }


    /**
     * <p>Render the start of a hidden input element containing the transaction
     * token value for this component.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component <code>UIComponent</code> to be decoded
     *
     * @exception IOException if an input/output error occurs
     */
    public void encodeBegin(FacesContext context, UIComponent component)
      throws IOException {

        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        Token token = (Token) component;
        if (token.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("input", token);
            String clientId = token.getClientId(context);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("name", clientId, "id");
            writer.writeAttribute("type", "hidden", null);
            writer.writeAttribute("value", token.getToken(), null);
        }

    }


    /**
     * <p>Render the end of a hidden input element containing the transaction
     * token value for this component.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component <code>UIComponent</code> to be decoded
     *
     * @exception IOException if an input/output error occurs
     */
    public void encodeEnd(FacesContext context, UIComponent component)
      throws IOException {

        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        Token token = (Token) component;
        if (token.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();
            writer.endElement("input");
        }

    }


}
