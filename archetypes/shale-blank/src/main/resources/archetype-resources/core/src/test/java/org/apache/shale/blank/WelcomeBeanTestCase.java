/*
 * Copyright 2006 The Apache Software Foundation.
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
 *
 */

package org.apache.shale.blank;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.shale.test.base.AbstractViewControllerTestCase;

/**
 * <p>Test case for the {@link WelcomeBean} ViewController implementation.</p>
 *
 */
public class WelcomeBeanTestCase extends AbstractViewControllerTestCase {


    // ------------------------------------------------------------ Constructors


    // Construct a new instance of this test case.
    public WelcomeBeanTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {

        // Perform superclass setup tasks
        super.setUp();

        // Construct a new instance to be tested
        bean = new WelcomeBean();

        // Insert any additional setup for your unit test methods here

    }

    // Return the tests included in this test case.
    public static Test suite() {

        return (new TestSuite(WelcomeBeanTestCase.class));

    }


    // Tear down instance variables required by this test case.
    public void tearDown() {

        // Insert any additional teardown for your unit test methods here

        // Remove the instance we just tested
        bean = null;

        // Perform superclass teardown tasks
        super.tearDown();

    }


    // ------------------------------------------------------ Instance Variables


    // The instance to be tested
    WelcomeBean bean = null;


    // ------------------------------------------------------------ Test Methods


    // Test behavior of prerender() method
    public void testPrerender() {

        bean.prerender();
        assertNotNull(bean.getTimestamp());

    }


    // Test a pristine instance of {@link WelcomeBean}
    public void testPristine() {

        assertNull(bean.getTimestamp());

    }


}
