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

import java.io.Serializable;


/**
 * Default implementation of the base Handler interface.  Simply stores a reference to the ServletContext.
 */
public abstract class BaseHandler
        implements Handler, Serializable {

    private HandlerConfig _config;
    private Handler _previousHandler;

    protected BaseHandler() {
    }

    /**
     * Initialize.
     *
     * @param handlerConfig   the configuration object for this Handler.
     * @param previousHandler the previously-registered Handler, which this one can adapt.
     */
    public void init(HandlerConfig handlerConfig, Handler previousHandler) {
        _previousHandler = previousHandler;
        _config = handlerConfig;
    }

    protected Handler getPreviousHandler() {
        return _previousHandler;
    }

    protected HandlerConfig getConfig() {
        return _config;
    }
}
