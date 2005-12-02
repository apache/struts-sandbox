/**
 Copyright 2004 The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Header:$
 */
package org.apache.ti.util.config.bean;


/**
 *
 */
public class PageFlowHandlersConfig {
    private HandlerConfig[] _actionForwardHandlers;
    private HandlerConfig[] _exceptionsHandler;
    private HandlerConfig[] _forwardRedirectHandler;
    private HandlerConfig[] _loginHandler;
    private HandlerConfig[] _storageHandler;
    private HandlerConfig[] _reloadableClassHandler;
    private HandlerConfig[] _moduleRegistrationHandler;
    private HandlerConfig[] _annotationHandler;

    public PageFlowHandlersConfig(HandlerConfig[] actionForwardHandlers, HandlerConfig[] exceptionsHandler,
                                  HandlerConfig[] forwardRedirectHandler, HandlerConfig[] loginHandler,
                                  HandlerConfig[] storageHandler, HandlerConfig[] reloadableClassHandler,
                                  HandlerConfig[] moduleRegistrationHandler, HandlerConfig[] annotationHandler) {
        _actionForwardHandlers = actionForwardHandlers;
        _exceptionsHandler = exceptionsHandler;
        _forwardRedirectHandler = forwardRedirectHandler;
        _loginHandler = loginHandler;
        _storageHandler = storageHandler;
        _reloadableClassHandler = reloadableClassHandler;
        _moduleRegistrationHandler = moduleRegistrationHandler;
        _annotationHandler = annotationHandler;
    }

    public HandlerConfig[] getActionForwardHandlers() {
        return _actionForwardHandlers;
    }

    public HandlerConfig[] getExceptionsHandlers() {
        return _exceptionsHandler;
    }

    public HandlerConfig[] getForwardRedirectHandlers() {
        return _forwardRedirectHandler;
    }

    public HandlerConfig[] getLoginHandlers() {
        return _loginHandler;
    }

    public HandlerConfig[] getStorageHandlers() {
        return _storageHandler;
    }

    public HandlerConfig[] getReloadableClassHandlers() {
        return _reloadableClassHandler;
    }

    public HandlerConfig[] getModuleRegistrationHandlers() {
        return _moduleRegistrationHandler;
    }

    public HandlerConfig[] getAnnotationHandlers() {
        return _annotationHandler;
    }
}
