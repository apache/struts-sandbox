/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.config.mapper;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.servlet.StrutsTiServlet;

/**
 * Handles creation of ActionMapping and reconstruction of URI's from one.  Uses
 * original servlet mapping to determine action mapping and reconstructed uri.
 */
public class ServletActionMapper implements ActionMapper {
    
    protected static final Log log = LogFactory.getLog(ServletActionMapper.class);
    
    public ActionMapping getMapping(WebContext ctx) {
        
        HttpServletRequest request = ((ServletWebContext)ctx).getRequest();
        List mappings = (List) ctx.get(StrutsTiServlet.SERVLET_MAPPINGS_KEY);
        String servletPath = request.getServletPath();
        return getMapping(servletPath, mappings);
    }
    
    protected ActionMapping getMapping(String servletPath, List mappings) {
        String uri = null;
        String mapping = null;
        for (Iterator i = mappings.iterator(); i.hasNext(); ) {
            mapping = (String)i.next();
            
            // Try to match prefix-based mapping
            if (mapping.charAt(mapping.length() - 1) == '*') {
                String prefix = mapping.substring(0, mapping.length() - 1);
                if (servletPath.startsWith(prefix)) {
                    uri = servletPath.substring(prefix.length());
                    log.debug("matched prefix:"+prefix);
                    break;
                }
            
            // Try to match extension mapping
            } else if (mapping.charAt(0) == '*') {
                String ext = mapping.substring(1);
                if (servletPath.endsWith(ext)) {
                    uri = servletPath.substring(1, (servletPath.length() - ext.length()));
                    log.debug("matched ext:"+ext);
                    break;
                }
            }
        }
        
        if (uri != null) {
            log.debug("uri:"+uri);
            int div = uri.lastIndexOf('/');
            String action = uri.substring(div + 1);
            String namespace = "";
            if (div > 0) {
                namespace = uri.substring(0, div);
            }
            
            return new ActionMapping(action, namespace, mapping, null);
        } else {
            // Couldn't find any action mapping
            return null;
        }
    }

    public String getUriFromActionMapping(ActionMapping mapping) {
        
        String ext = mapping.getExternalMapping();
        int star = ext.indexOf('*');
        
        StringBuffer sb = new StringBuffer();
        if (star > 0) {
            sb.append(ext.substring(0, star));
        } else {
            sb.append('/');
        }
        sb.append(mapping.getNamespace());
        sb.append('/');
        sb.append(mapping.getName());
        if (star < ext.length() - 1) {
            sb.append(ext.substring(star + 1));
        }
        return sb.toString();
    }
}
