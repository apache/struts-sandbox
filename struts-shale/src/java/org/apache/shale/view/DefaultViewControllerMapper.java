/*
 * Copyright 2004-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.view;

import java.util.HashSet;
import java.util.Set;
import org.apache.shale.ViewControllerMapper;

/**
 * <p>{@link DefaultViewControllerMapper} is a default implementation of {@link ViewControllerMapper}.  The following
 * algorithm is implemented:</p>
 * <ul>
 * <li>Strip any leading slash ("/") character.</li>
 * <li>Strip any traling extension (".xxx"), as long as it occurs
 *     after any remaining slash ("/") character.</li>
 * <li>Convert each instance of a slash ("/")
 *     character into a dollar sign ("$") character.</li>
 * <li>If the resulting name matches one of the reserved names recognized
 *     by the default <code>VariableResolver</code>, prefix it with an
 *     underscore character ("_"), to avoid problems loading managed beans.</li>
 * </ul>
 *
 * $Id$
 */

public class DefaultViewControllerMapper implements ViewControllerMapper {
    

    // -------------------------------------------------------- Static Variables


    /**
     * <p>Reserved variable names.</p>
     */
    private static Set reserved = new HashSet();

    static {
        reserved.add("applicationScope");
        reserved.add("cookies");
        reserved.add("facesContext");
        reserved.add("header");
        reserved.add("headerValues");
        reserved.add("initParam");
        reserved.add("param");
        reserved.add("paramValues");
        reserved.add("requestScope");
        reserved.add("sessionScope");
        reserved.add("view");
    }


    // ---------------------------------------------------------- Public Methods


    // Specified by ViewControllerMapper
    public String mapViewId(String viewId) {
        
        if (viewId == null) {
            return null;
        }
        if (viewId.startsWith("/")) {
            viewId = viewId.substring(1);
        }
        int slash = viewId.lastIndexOf("/");
        int period = viewId.lastIndexOf(".");
        if ((period >= 0) && (period > slash)) {
            viewId = viewId.substring(0, period);
        }
        viewId = viewId.replace('/', '$');
        if (reserved.contains(viewId)) {
            return "_" + viewId;
        } else {
            return viewId;
        }

    }


}
