/*
 * $Id$ 
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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

package org.apache.struts.plugins.resources;

import javax.servlet.ServletContext;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;


/**
 * Factory for <code>CommonsResources</code> instances.  The
 * configuration paramter for such instances is the base Java package
 * name of the resources entries from which our keys and values will be
 * loaded.
 *
 * @version $Rev$ $Date$
 */

public class CommonsResourcesFactory extends MessageResourcesFactory {


   // --------------------------------------------------------- Public Methods


   /**
    * Create and return a newly instansiated <code>MessageResources</code>.
    * This method must be implemented by concrete subclasses.
    *
    * @param config Configuration parameter(s) for the requested bundle
    */
   public MessageResources createResources(String config) {

       return new CommonsResources(this, config, this.returnNull);

   }

   public MessageResources createResources(ServletContext servletContext, 
           String implFactoryClass, String resourcesImpl, String config) throws Exception {

       return new CommonsResources(this, servletContext, implFactoryClass, resourcesImpl, 
               config, this.returnNull);

   }


}
