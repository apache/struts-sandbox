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
package org.apache.ti.pageflow.interceptor;

import java.io.Serializable;

public abstract class AbstractInterceptor
        implements Interceptor, Serializable {

    private InterceptorConfig _config;

    /**
     * Called when this interceptor is being initialized.
     *
     * @param config the configuration object associated with this interceptor.
     */
    public void init(InterceptorConfig config) {
        _config = config;
    }

    /**
     * Get the configuration object associated with this interceptor.
     *
     * @return the configuration object associated with this interceptor.
     */
    public InterceptorConfig getConfig() {
        return _config;
    }

}
