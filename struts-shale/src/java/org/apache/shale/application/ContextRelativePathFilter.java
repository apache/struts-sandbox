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

package org.apache.shale.application;

import org.apache.shale.faces.ShaleWebContext;

/**
 * <p>Command that filters incoming requests based on matching the
 * context-relative portion of the request URI (in other words, servlet
 * path plus path info) against regular expression patterns that are
 * configured on this instance.  See {@link AbstractRegExpFilter} for
 * details of the matching algorithm.</p>
 *
 * <p><strong>USAGE NOTE:</strong> - This command will only be effective if
 * used before the regular filter chain is processed.  In other words, you
 * should invoke it as part of a <code>preprocess</code> chain in the
 * <code>shale</code> catalog.</p>
 *
 * $Id$
 *
 * @see AbstractRegExpFilter
 */
public class ContextRelativePathFilter extends AbstractRegExpFilter {
    

    // ------------------------------------------------------- Protected Methods


    /**
     * <p>Return the servlet path (if any) concatenated with the path info
     * (if any) for this request.</p>
     *
     * @param context <code>Context</code> for the current request
     */
    protected String value(ShaleWebContext context) {

        String servletPath = context.getRequest().getServletPath();
        if (servletPath == null) {
            servletPath = "";
        }
        String pathInfo = context.getRequest().getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }
        return servletPath + pathInfo;

    }


}
