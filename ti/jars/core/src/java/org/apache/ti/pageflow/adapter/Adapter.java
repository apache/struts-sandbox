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
package org.apache.ti.pageflow.adapter;

import org.apache.commons.chain.web.WebContext;

/**
 * Base interface for all discoverable adapters.
 */
public interface Adapter {

    /**
     * Called upon the initialization, to decide whether this Adapter instance should become active.
     *
     * @param context the context being initialized.
     * @return <code>true</code> if the environment is appropriate for this Adapter, and it should become active.
     */
    public boolean accept(WebContext context);

    /**
     * Set the generic AdapterContext.
     */
    public void initialize(WebContext context);
}
