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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

/**
 * <p>Mock implementation of <code>Lifecycle</code>.</p>
 *
 * $Id$
 */

public class MockLifecycle extends Lifecycle {


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // ------------------------------------------------------- Lifecycle Methods


    public void addPhaseListener(PhaseListener listener) {

        throw new UnsupportedOperationException();

    }


    public void execute(FacesContext context) throws FacesException {

        throw new UnsupportedOperationException();

    }


    public PhaseListener[] getPhaseListeners() {

        throw new UnsupportedOperationException();

    }


    public void removePhaseListener(PhaseListener listener) {

        throw new UnsupportedOperationException();

    }


    public void render(FacesContext context) throws FacesException {

        throw new UnsupportedOperationException();

    }


}
