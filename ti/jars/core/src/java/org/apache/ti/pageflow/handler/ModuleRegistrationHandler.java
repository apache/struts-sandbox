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
package org.apache.ti.pageflow.handler;

import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.ModuleConfigLocator;

import java.net.URL;

public interface ModuleRegistrationHandler extends Handler {

    /**
     * Get the ModuleConfig for the given namespace (registering the module dynamically if necessary).
     *
     * @param namespace the namespace of the desired module.
     * @return the ModuleConfig that corresponds with <code>namespace</code>.
     */
    public ModuleConfig getModuleConfig(String namespace);

    /**
     * Clear all registered modules.
     */
    public void clearRegisteredModules();

    /**
     * Get the current list of registered ModuleConfigLocators.
     *
     * @return an array of registered ModuleConfigLocators.
     */
    public ModuleConfigLocator[] getModuleConfigLocators();

    /**
     * Get the resource URL the Struts module configration file for a given namespace.
     * based on registered ModuleConfigLocators.
     *
     * @param namespace the namespace of the module.
     * @return a String that is the path to the Struts configuration file, relative to the web application root,
     *         or <code>null</code> if no appropriate configuration file is found.
     */
    public URL getModuleConfURL(String namespace);
}
