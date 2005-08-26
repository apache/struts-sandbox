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
package org.apache.ti.pageflow.xwork;

import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PreviousInfo;

public abstract class NavigateToResult extends PageFlowResult {

    protected String getQueryString(Forward pageFlowFwd, PreviousInfo previousInfo) {
        String query = pageFlowFwd.getQueryString();
        if (query == null) query = "";
        
        //
        // If the restoreQueryString attribute was set, use the query string from the original action URI.
        //
        boolean restoreQueryString = isRestoreQueryString();
        if (restoreQueryString) {
            String prevQuery = previousInfo.getQueryString();
            if (prevQuery != null) query += (query.length() > 0 ? "&" : "?") + prevQuery;
        }

        return query;
    }

    public abstract String getNavigateToAsString();
}
