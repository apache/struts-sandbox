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
package org.apache.ti.pageflow;

/**
 * Interface for specifying alternate locations for auto-registered Struts modules.
 *
 * @see org.apache.ti.pageflow.internal.DefaultModuleRegistrationHandler#getDefaultModuleConfigLocators
 */
public interface ModuleConfigLocator {

    /**
     * Get the resource path to a module config file, based on the module name.
     *
     * @param namespace the namespace of the module, e.g., "someModule" or "some/other/module".
     * @return the webapp-relative path the the Struts module config file.
     */
    public String getModuleResourcePath(String namespace);
}
