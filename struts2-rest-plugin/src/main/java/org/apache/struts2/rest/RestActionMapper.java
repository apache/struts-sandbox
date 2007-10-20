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

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.net.URLDecoder;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

/**
 * <!-- START SNIPPET: description -->
 *
 * Improved restful action mapper that adds several ReST-style improvements to
 * action mapping, but supports fully-customized URL's via XML.  The two primary
 * ReST enhancements are:
 * <ul>
 *  <li>If the method is not specified (via '!' or 'method:' prefix), the method is
 *      "guessed" at using ReST-style conventions that examine the URL and the HTTP
 *      method.</li>
 *  <li>Parameters are extracted from the action name, if parameter name/value pairs
 *      are specified using PARAM_NAME/PARAM_VALUE syntax.
 * </ul>
 * <p>
 * These two improvements allow a GET request for 'category/action/movie/Thrillers' to
 * be mapped to the action name 'movie' with an id of 'Thrillers' with an extra parameter
 * named 'category' with a value of 'action'.  A single action mapping can then handle
 * all CRUD operations using wildcards, e.g.
 * </p>
 * <pre>
 *   &lt;action name="movie/*" className="app.MovieAction"&gt;
 *     &lt;param name="id"&gt;{0}&lt;/param&gt;
 *     ...
 *   &lt;/action&gt;
 * </pre>
 * <p>
 *   This mapper supports the following parameters:
 * </p>
 * <ul>
 *   <li><code>struts.mapper.idParameterName</code> - If set, this value will be the name
 *       of the parameter under which the id is stored.  The id will then be removed
 *       from the action name.  This allows restful actions to not require wildcards.
 *   </li>
 * </ul>
 * <p>
 * The following URL's will invoke its methods:
 * </p>
 * <ul> 
 *  <li><code>GET:    /movie/               => method="index"</code></li>
 *  <li><code>GET:    /movie/Thrillers      => method="show", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/Thrillers;edit => method="input", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/new            => method="input"</code></li>
 *  <li><code>POST:   /movie/               => method="create"</code></li>
 *  <li><code>PUT:    /movie/Thrillers      => method="update", id="Thrillers"</code></li>
 *  <li><code>DELETE: /movie/Thrillers      => method="destroy", id="Thrillers"</code></li>
 * </ul>
 * <p>
 * To simulate the HTTP methods PUT and DELETE, since they aren't supported by HTML,
 * the HTTP parameter "__http_method" will be used.
 * </p>
 * <p>
 * The syntax and design for this feature was inspired by the ReST support in Ruby on Rails.
 * See <a href="http://ryandaigle.com/articles/2006/08/01/whats-new-in-edge-rails-simply-restful-support-and-how-to-use-it">
 * http://ryandaigle.com/articles/2006/08/01/whats-new-in-edge-rails-simply-restful-support-and-how-to-use-it
 * </a>
 * </p>
 *
 * <!-- END SNIPPET: description -->
 */
public class RestActionMapper extends DefaultActionMapper {

    protected static final Logger LOG = LoggerFactory.getLogger(RestActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "_method";
    private String idParameterName = "id";
    
    public RestActionMapper() {
    }
    
    public String getIdParameterName() {
        return idParameterName;
    }

    @Inject(required=false,value=StrutsConstants.STRUTS_ID_PARAMETER_NAME)
    public void setIdParameterName(String idParameterName) {
        this.idParameterName = idParameterName;
    }
    
    public ActionMapping getMapping(HttpServletRequest request,
            ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = getUri(request);

        uri = dropExtension(uri, mapping);
        if (uri == null) {
            return null;
        }

        parseNameAndNamespace(uri, mapping, configManager);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        // handle "name!method" convention.
        String name = mapping.getName();
        int exclamation = name.lastIndexOf("!");
        if (exclamation != -1) {
            mapping.setName(name.substring(0, exclamation));
            mapping.setMethod(name.substring(exclamation + 1));
        }

        String fullName = mapping.getName();
        // Only try something if the action name is specified
        if (fullName != null && fullName.length() > 0) {
            int lastSlashPos = fullName.lastIndexOf('/');
            String id = null;
            if (lastSlashPos > -1) {
                id = fullName.substring(lastSlashPos+1);
            }

            // If a method hasn't been explicitly named, try to guess using ReST-style patterns
            if (mapping.getMethod() == null) {

                // Handle uris ending in '/'
                if (lastSlashPos == fullName.length() -1) {

                    // Index e.g. foo/
                    if (isGet(request)) {
                        mapping.setMethod("index");
                        
                    // Creating a new entry on POST e.g. foo/
                    } else if (isPost(request)) {
                        mapping.setMethod("create");
                    }

                // Handle uris with an id at the end
                } else if (id != null) {
                    
                    // Viewing the form to edit an item e.g. foo/1;edit
                    if (isGet(request) && id.endsWith(";edit")) {
                        id = id.substring(0, id.length() - ";edit".length());
                        mapping.setMethod("input");
                        
                    // Viewing the form to create a new item e.g. foo/new
                    } else if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod("input");

                    // Removing an item e.g. foo/1
                    } else if (isDelete(request)) {
                        mapping.setMethod("destroy");
                        
                    // Viewing an item e.g. foo/1
                    } else if (isGet(request)) {
                        mapping.setMethod("show");
                    
                    // Updating an item e.g. foo/1    
                    }  else if (isPut(request)) {
                        mapping.setMethod("update");
                    }
                }
            }
            
            // cut off the id parameter, even if a method is specified
            if (id != null) {
                if (!"new".equals(id)) {
                    if (mapping.getParams() == null) {
                        mapping.setParams(new HashMap());
                    }
                    mapping.getParams().put(idParameterName, new String[]{id});
                }
                fullName = fullName.substring(0, lastSlashPos);
            }

            mapping.setName(fullName);
        }

        return mapping;
    }
    
    /**
     * Parses the name and namespace from the uri.  Doesn't allow slashes in name.
     *
     * @param uri
     *            The uri
     * @param mapping
     *            The action mapping to populate
     */
    protected void parseNameAndNamespace(String uri, ActionMapping mapping,
            ConfigurationManager configManager) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf("/");
        if (lastSlash == -1) {
            namespace = "";
            name = uri;
        } else if (lastSlash == 0) {
            // ww-1046, assume it is the root namespace, it will fallback to
            // default
            // namespace anyway if not found in root namespace.
            namespace = "/";
            name = uri.substring(lastSlash + 1);
        } else {
            int secondToLastSlash = uri.lastIndexOf('/', lastSlash - 1);
            if (secondToLastSlash == 0) {
                namespace = "/";
                name = uri.substring(secondToLastSlash + 1);
            } else if (secondToLastSlash > -1) {
                namespace = uri.substring(0, secondToLastSlash);
                name = uri.substring(secondToLastSlash + 1);
            } else {
                namespace = "";
                name = uri;
            }
        }

        mapping.setNamespace(namespace);
        mapping.setName(name);
    }

    protected boolean isGet(HttpServletRequest request) {
        return "get".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPost(HttpServletRequest request) {
        return "post".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPut(HttpServletRequest request) {
        if ("put".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return isPost(request) && "put".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

    protected boolean isDelete(HttpServletRequest request) {
        if ("delete".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

}
