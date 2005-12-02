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
package org.apache.ti.pageflow.httpservlet.internal;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.config.mapper.ActionMapper;
import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.servlet.http.HttpServletRequest;

public class PopulatePageFlowContext implements Command {

    public boolean execute(Context context) throws Exception {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        ServletWebContext swc = (ServletWebContext) actionContext.getWebContext();
        HttpServletRequest request = swc.getRequest();
        
        
        
        // First try to construct the request path from the ActionMapper.
        ActionMapping mapping = actionContext.getActionMapping();

        if (mapping != null) {
            ActionMapper mapper = actionContext.getActionMapper();
            actionContext.setRequestPath(mapper.getUriFromActionMapping(mapping));
        } else {
            // If there was no ActionMapping (this is not an action request), just use the Servlet path.
            // TODO: re-add the ignoreIncludeServletPath() check, for page template support
            // if ( ignoreIncludeServletPath() ) return request.getServletPath();
            String servletIncludePath = (String) request.getAttribute("javax.servlet.include.servlet_path");
            actionContext.setRequestPath(servletIncludePath != null ? servletIncludePath : request.getServletPath());
        }

        actionContext.setRequestContextPath(request.getContextPath());
        actionContext.setRequestQueryString(request.getQueryString());
        actionContext.setRequestSecure(request.isSecure());
        return false;
    }
}
