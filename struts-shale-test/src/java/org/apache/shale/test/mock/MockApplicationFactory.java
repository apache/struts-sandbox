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

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * <p>Mock implementation of <code>ApplicationFactory</code>.</p>
 *
 * $Id$
 */

public class MockApplicationFactory extends ApplicationFactory {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a default instance.</p>
     */
    public MockApplicationFactory() {

        this.application = new MockApplication();

    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    private Application application = null;


    // --------------------------------------------- AppolicationFactory Methods


    public Application getApplication() {

        return this.application;

    }


    public void setApplication(Application application) {

        this.application = application;

    }


}
