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
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

/**
 * <p>Mock implementation of <code>FacesContextFactory</code>.</p>
 *
 * $Id$
 */

public class MockFacesContextFactory extends FacesContextFactory {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Return a default instance.</p>
     */
    public MockFacesContextFactory() {}
    

    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // --------------------------------------------- FacesContextFactory Methods


    public FacesContext getFacesContext(Object context, Object request,
					Object response, 
					Lifecycle lifecycle) throws FacesException {

        return new MockFacesContext();

    }


}
