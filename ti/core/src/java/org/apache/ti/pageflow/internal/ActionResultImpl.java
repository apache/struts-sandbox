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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.ActionResult;

import java.io.PrintWriter;

/**
 * Encapsulation of the results returned by {@link org.apache.ti.pageflow.PageFlowUtils#strutsLookup}.
 */
public class ActionResultImpl implements ActionResult {

    private String _uri = null;
    private boolean _isRedirect = false;
    private int _statusCode = 0;
    private String _statusMessage = null;
    private boolean _isError = false;


    protected ActionResultImpl() {
    }

    public ActionResultImpl(String uri, boolean redirect, int statusCode, String statusMessage, boolean isError) {
        _uri = uri;
        _isRedirect = redirect;
        _statusCode = statusCode;
        _statusMessage = statusMessage;
        _isError = isError;
    }

    public String getURI() {
        return _uri;
    }

    public void setURI(String uri) {
        _uri = uri;
    }

    public boolean isRedirect() {
        return _isRedirect;
    }

    public void setRedirect(boolean redirect) {
        _isRedirect = redirect;
    }

    public boolean isError() {
        return _isError;
    }

    public void setError(boolean error) {
        _isError = error;
    }

    public int getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(int statusCode) {
        _statusCode = statusCode;
    }

    public String getStatusMessage() {
        return _statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        _statusMessage = statusMessage;
    }

    public boolean hadCompileErrors() {
        return false;
    }

    public void printCompileErrors(PrintWriter writer) {
    }
}
