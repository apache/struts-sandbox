/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.pageflow.internal;



/**
 * Specialization of the base ServletBeanContext that adds a Page Flow service provider to offer
 * initialization for PageFlowController and SharedFlowControler members in a Control.
 */
public class PageFlowBeanContext {

}

/* TODO: re-add this, under a controls-support module
        extends ServletBeanContext
        implements PageFlowServiceProvider.HasServletRequest
{
    public ServletRequest getServletRequest()
    {
        return super.getServletRequest();
    }

    /**
      * Called by BeanContextSupport superclass during construction and deserialization to
      * initialize subclass transient state
      *
     public void initialize()
     {
         super.initialize();
         addService( PageFlowController.class, PageFlowServiceProvider.getProvider() );
     }
}
*/
