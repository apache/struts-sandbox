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
package org.apache.ti.processor.chain.pageflow;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.config.mapper.ActionMapping;

/**
 * Set up the Page Flow ModuleConfig.  This step needs to happen early because it also adds a configuration provider
 * to XWork, which needs to have this configuration available when it is creating the ActionProxy.
 */
public class SetupPageFlowModule implements Command {

    public boolean execute(Context context) throws Exception {

        ActionMapping mapping = (ActionMapping) context.get("actionMapping");
        String namespace = mapping.getNamespace();
        ModuleConfig config = Handlers.get().getModuleRegistrationHandler().getModuleConfig(namespace);
        PageFlowActionContext.get().setModuleConfig(config);
        return false;
    }
}
