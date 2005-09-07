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
package org.apache.ti.core.factory;

import org.apache.ti.util.logging.Logger;

import java.io.Serializable;

/**
 * Base class for factories.
 */
public abstract class Factory
        implements Serializable {

    private static final Logger log = Logger.getInstance(Factory.class);

    private FactoryConfig _config;

    /**
     * Called after this factory has been created and initialized.
     */
    protected void onCreate() {
    	log.debug("Factory#create()");
    }

    void init(FactoryConfig config) {
        _config = config;
    }

    /**
     * Called to reinitialize this instance, most importantly after it has been serialized/deserialized.
     */
    protected void reinit() {
    	log.debug("Factory#reinit()");
    }

    /**
	 * @todo Finish documenting me!
	 * 
     * Get the configuration object (containing custom properties) that is associated with this factory.
     * @return A {@link FactoryConfig} or null
     */
    protected FactoryConfig getConfig() {
    	log.debug("Factory#getConfig()");
        return _config;
    }
}
