/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow;

/**
 *  <p>
 *
 *  Global constants for the Chain of Responsibility Library.</p>
 */
public final class Constants {

    // -------------------------------------------------- Context Attribute Keys

    /**
     *  <p>
     *
     *  The default context attribute under which the forward name for
     *  the current request will be stored.</p>
     */
    public final static String FORWARD_KEY = "forward";
    

    
    // ----------------------- Context Attribute Keys borrowed from Struts-Chain
    
    /**
     *  <p>
     *
     *  The default context attribute under which the <code>Action</code> for
     *  the current request will be stored.</p>
     */
    public final static String ACTION_KEY = "action";

    /**
     *  <p>
     *
     *  The default context attribute under which the <code>ActionConfig</code>
     *  for the current request will be stored.</p>
     */
    public final static String ACTION_CONFIG_KEY = "actionConfig";

    /**
     *  <p>
     *
     *  The default context attribute under which the <code>ActionForm</code>
     *  for the current request will be stored.</p>
     */
    public final static String ACTION_FORM_KEY = "actionForm";

    /**
     *  <p>
     *
     *  The default context attribute under which the <code>ActionServet</code>
     *  for the current application will be stored.</p>
     */
    public final static String ACTION_SERVLET_KEY = "actionServlet";

    /**
     *  <p>
     *
     *  The default context attribute under which the <code>MessageResources</code>
     *  for the current request will be stored.</p>
     */
    public final static String MESSAGE_RESOURCES_KEY = "messageResources";

}

