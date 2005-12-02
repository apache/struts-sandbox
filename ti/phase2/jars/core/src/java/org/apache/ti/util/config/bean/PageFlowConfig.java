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
public class PageFlowConfig {
    private static final boolean DEFAULT_ENABLE_SELF_NESTING = false;
    private static final int DEFAULT_MAX_FORWARDS_PER_REQUEST = 25;
    private static final int DEFAULT_MAX_NESTING_STACK_DEPTH = 10;
    private static final boolean DEFAULT_ENSURE_SECURE_FORWARDS = false;
    private static final boolean DEFAULT_THROW_SESSION_EXPIRED_EXCEPTION = true;
    private static final MultipartHandler DEFAULT_MULTIPART_HANDLER = MultipartHandler.DISABLED;
    private static final PreventCache DEFAULT_PREVENT_CACHE = PreventCache.DEFAULT;
    private boolean _enableSelfNesting;
    private boolean _ensureSecureForwards;
    private boolean _throwSessionExpiredException;
    private int _maxForwardsPerRequest;
    private int _maxNestingStackDepth;
    private MultipartHandler _multipartHandler;
    private PreventCache _preventCache;
    private ModuleConfigLocatorConfig[] _moduleConfigLocators;

    public PageFlowConfig() {
        _enableSelfNesting = DEFAULT_ENABLE_SELF_NESTING;
        _ensureSecureForwards = DEFAULT_ENSURE_SECURE_FORWARDS;
        _throwSessionExpiredException = DEFAULT_THROW_SESSION_EXPIRED_EXCEPTION;
        _maxForwardsPerRequest = DEFAULT_MAX_FORWARDS_PER_REQUEST;
        _maxNestingStackDepth = DEFAULT_MAX_NESTING_STACK_DEPTH;
        _multipartHandler = DEFAULT_MULTIPART_HANDLER;
        _preventCache = DEFAULT_PREVENT_CACHE;
    }

    public PageFlowConfig(Boolean enableSelfNesting, Boolean ensureSecureForwards, Boolean throwSessionExpiredException,
                          Integer maxForwardsPerRequest, Integer maxNestingStackDepth, MultipartHandler multipartHandler,
                          PreventCache preventCache, ModuleConfigLocatorConfig[] moduleConfigLocators) {
        /* initialize the defaults */
        this();

        if (enableSelfNesting != null) {
            _enableSelfNesting = enableSelfNesting.booleanValue();
        }

        if (ensureSecureForwards != null) {
            _ensureSecureForwards = ensureSecureForwards.booleanValue();
        }

        if (throwSessionExpiredException != null) {
            _throwSessionExpiredException = throwSessionExpiredException.booleanValue();
        }

        if (maxForwardsPerRequest != null) {
            _maxForwardsPerRequest = maxForwardsPerRequest.intValue();
        }

        if (maxNestingStackDepth != null) {
            _maxNestingStackDepth = maxNestingStackDepth.intValue();
        }

        if (multipartHandler != null) {
            _multipartHandler = multipartHandler;
        }

        if (preventCache != null) {
            _preventCache = preventCache;
        }

        _moduleConfigLocators = moduleConfigLocators;
    }

    public boolean isEnableSelfNesting() {
        return _enableSelfNesting;
    }

    public boolean isEnsureSecureForwards() {
        return _ensureSecureForwards;
    }

    public boolean isThrowSessionExpiredException() {
        return _throwSessionExpiredException;
    }

    public int getMaxForwardsPerRequest() {
        return _maxForwardsPerRequest;
    }

    public int getMaxNestingStackDepth() {
        return _maxNestingStackDepth;
    }

    public MultipartHandler getMultipartHandler() {
        return _multipartHandler;
    }

    public PreventCache getPreventCache() {
        return _preventCache;
    }

    public ModuleConfigLocatorConfig[] getModuleConfigLocators() {
        return _moduleConfigLocators;
    }
}
