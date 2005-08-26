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

import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.core.urls.URLRewriter;
import org.apache.ti.core.urls.URLType;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;


public class DefaultURLRewriter extends URLRewriter {

    public String getNamePrefix(String name) {
        return "";
    }

    public void rewriteURL(MutableURI url, URLType type, boolean needsToBeSecure) {
        //
        // If the current request has a special parameter that addresses a named 'scope',
        // add the parameter to the URL.
        //
        String scopeID = (String) PageFlowActionContext.get().getParameters().get(InternalConstants.SCOPE_ID_PARAM);
        if (scopeID != null) {
            // check to see if the param is already there.
            if (url.getParameter(InternalConstants.SCOPE_ID_PARAM) == null) {
                url.addParameter(InternalConstants.SCOPE_ID_PARAM, scopeID, true);
            }
        }
    }

    /**
     * Determines if the passed-in Object is equivalent to this DefaultURLRewriter.
     * Since there is no member data for this class they will all be equal.
     *
     * @param object the Object to test for equality.
     * @return true if object is another instance of DefaultURLRewriter.
     */

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object == null || !object.getClass().equals(this.getClass())) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code value for the object.
     * Implemented in conjunction with equals() override.
     * Since there is no member data for this class we
     * always return the same value.
     *
     * @return a hash code value for this object.
     */

    public int hashCode() {
        return 0;
    }
}
