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
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * <p>Mock implementation of <code>RenderKitFactory</code>.</p>
 *
 * $Id$
 */

public class MockRenderKitFactory extends RenderKitFactory {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Return a default instance.</p>
     */
    public MockRenderKitFactory() {
    
        renderKits = new HashMap();
        renderKits.put(RenderKitFactory.HTML_BASIC_RENDER_KIT,
                       new MockRenderKit());

    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    private Map renderKits = new HashMap();


    // ------------------------------------------------ RenderKitFactory Methods


    public void addRenderKit(String renderKitId, RenderKit renderKit) {

        if ((renderKitId == null) || (renderKit == null)) {
            throw new NullPointerException();
        }
        if (renderKits.containsKey(renderKitId)) {
            throw new IllegalArgumentException(renderKitId);
        }
        renderKits.put(renderKitId, renderKit);

    }


    public RenderKit getRenderKit(FacesContext context, String renderKitId) {

        if (renderKitId == null) {
            throw new NullPointerException();
        }
        RenderKit renderKit = (RenderKit) renderKits.get(renderKitId);
        if (renderKit == null) {
            throw new IllegalArgumentException(renderKitId);
        }
        return renderKit;

    }


    public Iterator getRenderKitIds() {

        return renderKits.keySet().iterator();

    }


}
