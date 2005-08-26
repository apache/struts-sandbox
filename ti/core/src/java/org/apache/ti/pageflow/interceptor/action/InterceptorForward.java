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
package org.apache.ti.pageflow.interceptor.action;

import org.apache.ti.pageflow.Forward;

import java.net.URI;


/**
 * forward returned from an {@link ActionInterceptor} to alter the destination URI of a page flow action.
 */
public class InterceptorForward extends Forward {

    protected InterceptorForward(Forward originalForward) {
        super(originalForward);
    }

    protected InterceptorForward() {
        super();
    }

    /**
     * Construct from a webapp-relative path.
     *
     * @param path the webapp-relative destination path.
     */
    public InterceptorForward(String path) {
        super(path);
    }

    /**
     * Construct from a URI.
     *
     * @param path     the webapp-relative destination path.
     * @param redirect if <code>true</code>, the controller will redirect to the given path; otherwise, a server forward
     *                 will be performed.
     */
    /* TODO: re-add this
    public InterceptorForward(String path, boolean redirect) {
        super(path, redirect);
    }
    */

    /**
     * Construct from a URI.
     *
     * @param uri the URI pointed to by this forward.  This is assumed to be <strong>webapp-relative</strong>
     *            (i.e., it does not include the webapp context path), unless {@link #setExternalRedirect} is called.
     */
    public InterceptorForward(URI uri) {
        super(null, uri);
    }
    
    /**
     * Construct from a URI.
     * 
     * @param uri the URI pointed to by this forward.  This is assumed to be <strong>webapp-relative</strong>
     *            (i.e., it does not include the webapp context path), unless {@link #setExternalRedirect} is called.
     * @param redirect if <code>true</code>, the controller will redirect to the given URI; otherwise, a server forward
     *            will be performed.
     */ 
    // TODO: re-add the ability to set redirect programmatically on a Forward; also setExternalRedirect
    /*
    public InterceptorForward( URI uri, boolean redirect )
    {
        super( uri, redirect );
    }
    */
    
    /**
     * @exclude
     */
    public void rehydrateRequest() {
    }
}
