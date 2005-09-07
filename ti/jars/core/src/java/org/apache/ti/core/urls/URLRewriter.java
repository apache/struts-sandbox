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
package org.apache.ti.core.urls;


/**
 * Offers methods for rewriting URLs/query parameters.
 */
public abstract class URLRewriter {

    /**
     * Flag indicating that other rewriters are allowed to be used in the chain.
     * The URLRewriterService API will not allow more than one URLRewriter in
     * the chain with this property equal to false.
     */
    private boolean _allowOtherRewriters = true;

    /**
     * Set the switch to allow other rewriters
     * @param allowOtherRewriters
     */
    public void setAllowOtherRewriters(boolean allowOtherRewriters) {
        _allowOtherRewriters = allowOtherRewriters;
    }

    /**
     * Get the state of the allow other rewriters switch.
     * @return true if allowed, false if not
     */
    public boolean allowOtherRewriters() {
        return _allowOtherRewriters;
    }

    /**
     * Get the prefix to use when rewriting a query parameter name.
     * Loops through the list of registered URLRewriters to build up a the prefix.
     *
     * @param name the name of the query parameter.
     * @return a prefix to use to rewrite a query parameter name.
     */
    public abstract String getNamePrefix(String name);

    /**
     * Rewrite the given URL.
     *
     * @param url             the MutableURI to be rewritten.
     * @param type            the type of URL to be rewritten.  This is one of the following values:
     *                        <ul>
     *                        <li><code>action</code>: a standard (non-resource) URL
     *                        <li><code>resource</code>: a resource (e.g., image) URL
     *                        </ul>
     * @param needsToBeSecure a flag indicating whether the URL should be secure (SSL required) or not
     */
    public abstract void rewriteURL(MutableURI url, URLType type, boolean needsToBeSecure);

    /**
	 * @todo Finish documenting me!
	 * 
     * Tell whether rewritten form actions should be allowed to have query parameters.  If this returns
     * <code>false</code>, then a form-tag implementation should render query parameters into hidden
     * fields on the form instead of allowing them to remain in the URL.
     * 
     * @return Return true if allowed, false if not
     */
    public boolean allowParamsOnFormAction() {
        return false;
    }
}
