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

import java.util.Locale;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * <p>Mock implementation of <code>ViewHandler</code>.</p>
 *
 * $Id$
 */

public class MockViewHandler extends ViewHandler {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a default instance.</p>
     */
    public MockViewHandler() {
    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // ----------------------------------------------------- ViewHandler Methods


    public Locale calculateLocale(FacesContext context) {

        return context.getViewRoot().getLocale();

    }


    public String calculateRenderKitId(FacesContext context) {

        return context.getViewRoot().getRenderKitId();

    }


    public UIViewRoot createView(FacesContext context, String viewId) {

        throw new UnsupportedOperationException();

    }


    public String getActionURL(FacesContext context, String viewId) {

        throw new UnsupportedOperationException();

    }


    public String getResourceURL(FacesContext context, String path) {

        throw new UnsupportedOperationException();

    }


    public void renderView(FacesContext context, UIViewRoot view) {

        throw new UnsupportedOperationException();

    }


    public UIViewRoot restoreView(FacesContext context, String viewId) {

        throw new UnsupportedOperationException();

    }


    public void writeState(FacesContext context) {

        throw new UnsupportedOperationException();

    }


}
