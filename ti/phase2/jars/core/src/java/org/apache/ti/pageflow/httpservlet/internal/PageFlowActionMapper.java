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

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.config.mapper.ServletActionMapper;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.internal.InternalConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

public class PageFlowActionMapper extends ServletActionMapper {

    private static final String ALREADY_OVERRODE_ACTION_ATTR = InternalConstants.ATTR_PREFIX + "overrodeAction";

    public ActionMapping getMapping(WebContext ctx) {
        ActionMapping mapping = super.getMapping(ctx);
        
        // *.jpf automatically gets translated to action "begin"
        if (mapping.getExternalMapping().endsWith(PageFlowConstants.PAGEFLOW_EXTENSION)) {
            mapping = new ActionMapping(PageFlowConstants.BEGIN_ACTION_NAME, mapping.getNamespace(),
                    mapping.getExternalMapping(), mapping.getParams());
        }

        HttpServletRequest request = ((ServletWebContext) ctx).getRequest();
        if (request.getAttribute(ALREADY_OVERRODE_ACTION_ATTR) == null) {
            String actionOverride = getActionOverride(request);

            if (actionOverride != null) {
                HashMap params = new HashMap();
                params.put(InternalConstants.ORIGINAL_ACTION_KEY, mapping.getName());
                if (mapping.getParams() != null) {
                    params.putAll(mapping.getParams());
                }
                mapping = new ActionMapping(actionOverride, mapping.getNamespace(), mapping.getExternalMapping(), params);
                request.setAttribute(ALREADY_OVERRODE_ACTION_ATTR, Boolean.TRUE);
            }
        }

        return mapping;
    }

    protected String getActionOverride(HttpServletRequest request) {

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            if (paramName.startsWith(PageFlowConstants.ACTION_OVERRIDE_PARAM_PREFIX)) {
                return paramName.substring(PageFlowConstants.ACTION_OVERRIDE_PARAM_PREFIX.length());
            }
        }

        return null;
    }


}
