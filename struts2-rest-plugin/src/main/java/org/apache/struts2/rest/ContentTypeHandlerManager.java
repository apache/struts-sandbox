/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class ContentTypeHandlerManager {

    private Map<String,ContentTypeHandler> handlers = new HashMap<String,ContentTypeHandler>();
    private String defaultHandlerName;

    @Inject("struts.rest.defaultHandlerName")
    public void setDefaultHandlerName(String name) {
        this.defaultHandlerName = name;
    }
    
    @Inject
    public void setContainer(Container container) {
        Set<String> names = container.getInstanceNames(ContentTypeHandler.class);
        for (String name : names) {
            ContentTypeHandler handler = container.getInstance(ContentTypeHandler.class, name);
            this.handlers.put(handler.getExtension(), handler);
        }
    }
    
    public ContentTypeHandler getHandlerForRequest(HttpServletRequest req) {
        String extension = findExtension(req.getRequestURI());
        if (extension == null) {
            extension = defaultHandlerName;
        }
        return handlers.get(extension);
    }
    
    public String handleResult(ActionConfig actionConfig, Object methodResult, Object target)
            throws IOException {
        String resultCode = null;
        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven)target).getModel();
        }
        
        boolean statusNotOk = false;
        if (methodResult instanceof RestInfo) {
            RestInfo info = (RestInfo) methodResult;
            resultCode = info.apply(req, res, target);
            if (info.getStatus() != SC_OK) {
                statusNotOk = true;
            }
        } else {
            resultCode = (String) methodResult;
        }
        
        // Don't return any content for PUT, DELETE, and POST where there are no errors
        if (!statusNotOk && !"get".equalsIgnoreCase(req.getMethod())) {
            target = null;
        }
        
        ContentTypeHandler handler = getHandlerForRequest(req);
        String extCode = resultCode+"-"+handler.getExtension();
        if (actionConfig.getResults().get(extCode) != null) {
            resultCode = extCode;
        } else {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            
            resultCode = handler.fromObject(target, resultCode, bout);
            if (bout.size() > 0) {
                res.setContentLength(bout.size());
                res.setContentType(handler.getContentType());
                res.getOutputStream().write(bout.toByteArray());
                res.getOutputStream().close();
            }
        }
        return resultCode;
        
    }
    
    protected String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos+1);
        }
        return null;
    }
}
