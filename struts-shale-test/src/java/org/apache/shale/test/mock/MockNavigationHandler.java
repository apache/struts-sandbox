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

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * <p>Mock implementation of <code>NavigationHandler</code>.</p>
 *
 * $Id$
 */

public class MockNavigationHandler extends NavigationHandler {
    

    // ------------------------------------------------------------ Constructors

    /**
     * <p>Construct a default instance.</p>
     */
    public MockNavigationHandler() {
    }
    

    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // ----------------------------------------------- NavigationHandler Methods
    

    /**
     * <p>Process the specified navigation request.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param action Action method being executed
     * @param outcome Logical outcome from this action method
     */
    public void handleNavigation(FacesContext context,
                                 String action, String outcome) {

        ; // FIXME - provide default implementation

    }


}
