/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.test.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseStream;
import javax.faces.render.Renderer;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;
import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;

/**
 * <p>Mock implementation of <code>RenderKit</code>.</p>
 *
 * $Id$
 */

public class MockRenderKit extends RenderKit {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Return a default instance.</p>
     */
    public MockRenderKit() {
    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    private Map renderers = new HashMap();


    // ------------------------------------------------------- RenderKit Methods


    public void addRenderer(String family, String rendererType,
                            Renderer renderer) {

        if ((family == null) || (rendererType == null) || (renderer == null)) {
            throw new NullPointerException();
        }
        renderers.put(family + "|" + rendererType, renderer);

    }


    public Renderer getRenderer(String family, String rendererType) {

        if ((family == null) || (rendererType == null)) {
            throw new NullPointerException();
        }
        return (Renderer) renderers.get(family + "|" + rendererType);

    }


    public ResponseWriter createResponseWriter(Writer writer,
					       String contentTypeList,
					       String characterEncoding) {

       throw new UnsupportedOperationException();

    }


    public ResponseStream createResponseStream(OutputStream out) {

        throw new UnsupportedOperationException();

    }


    public ResponseStateManager getResponseStateManager() {

        throw new UnsupportedOperationException();

    }


}
