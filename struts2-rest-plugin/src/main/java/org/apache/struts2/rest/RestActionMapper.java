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

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 *  <li><code>GET:    /movie/Thrillers      => method="view", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/Thrillers!edit => method="edit", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/new            => method="editNew"</code></li>
 *  <li><code>POST:   /movie/               => method="create"</code></li>
 *  <li><code>PUT:    /movie/Thrillers      => method="update", id="Thrillers"</code></li>
 *  <li><code>DELETE: /movie/Thrillers      => method="remove", id="Thrillers"</code></li>
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

    protected static final Log LOG = LogFactory.getLog(RestActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "__http_method";
    private String idParameterName = null;
    private boolean allowDynamicMethodCalls;
    private List<String> extensions;
    
    public RestActionMapper() {
    }
    
    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String allow) {
        allowDynamicMethodCalls = "true".equals(allow);
    }
    
    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtensions(String extensions) {
        if (!"".equals(extensions)) {
            this.extensions = Arrays.asList(extensions.split(","));
        } else {
            this.extensions = null;
        }
    }
    
    
    public ActionMapping getMapping(HttpServletRequest request,
            ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = getUri(request);

        uri = dropExtension(uri);
        if (uri == null) {
            return null;
        }

        String fullName = parseNameAndNamespace(uri, mapping, configManager);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        if (allowDynamicMethodCalls) {
            // handle "name!method" convention.
            String name = mapping.getName();
            int exclamation = name.lastIndexOf("!");
            if (exclamation != -1) {
                mapping.setName(name.substring(0, exclamation));
                mapping.setMethod(name.substring(exclamation + 1));
            }
        }

        // Only try something if the action name is specified
        if (fullName != null && fullName.length() > 0) {
            int lastSlashPos = fullName.lastIndexOf('/');

            // If a method hasn't been explicitly named, try to guess using ReST-style patterns
            if (mapping.getMethod() == null) {

                if (lastSlashPos == fullName.length() -1) {

                    // Index e.g. foo/
                    if (isGet(request)) {
                        mapping.setMethod("index");
                        
                    // Creating a new entry on POST e.g. foo/
                    } else if (isPost(request)) {
                        mapping.setMethod("create");
                    }

                } else if (lastSlashPos > -1) {
                    String id = fullName.substring(lastSlashPos+1);

                    // Viewing the form to create a new item e.g. foo/new
                    if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod("editNew");

                    // Viewing an item e.g. foo/1
                    } else if (isGet(request)) {
                        mapping.setMethod("view");

                    // Removing an item e.g. foo/1
                    } else if (isDelete(request)) {
                        mapping.setMethod("remove");
                    
                    // Updating an item e.g. foo/1    
                    }  else if (isPut(request)) {
                        mapping.setMethod("update");
                    }
                    
                    if (idParameterName != null) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap());
                        }
                        mapping.getParams().put(idParameterName, id);
                    }
                }
                
                if (idParameterName != null && lastSlashPos > -1) {
                    fullName = fullName.substring(0, lastSlashPos);
                }
            }

            // Try to determine parameters from the url before the action name
            int actionSlashPos = fullName.lastIndexOf('/', lastSlashPos - 1);
            if (actionSlashPos > 0 && actionSlashPos < lastSlashPos) {
                String params = fullName.substring(0, actionSlashPos);
                HashMap<String,String> parameters = new HashMap<String,String>();
                try {
                    StringTokenizer st = new StringTokenizer(params, "/");
                    boolean isNameTok = true;
                    String paramName = null;
                    String paramValue;

                    while (st.hasMoreTokens()) {
                        if (isNameTok) {
                            paramName = URLDecoder.decode(st.nextToken(), "UTF-8");
                            isNameTok = false;
                        } else {
                            paramValue = URLDecoder.decode(st.nextToken(), "UTF-8");

                            if ((paramName != null) && (paramName.length() > 0)) {
                                parameters.put(paramName, paramValue);
                            }

                            isNameTok = true;
                        }
                    }
                    if (parameters.size() > 0) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap());
                        }
                        mapping.getParams().putAll(parameters);
                    }
                } catch (Exception e) {
                    LOG.warn(e);
                }
                fullName = fullName.substring(actionSlashPos+1);
            }
            mapping.setName(fullName);
        }

        return mapping;
    }
    
    /**
     * Gets the uri from the request
     *
     * @param request
     *            The request
     * @return The uri
     */
    String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request
                .getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        uri = RequestUtils.getServletPath(request);
        if (uri != null && !"".equals(uri)) {
            return uri;
        }

        uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }
    
    /**
     * Parses the name and namespace from the uri
     *
     * @param uri
     *            The uri
     * @param mapping
     *            The action mapping to populate
     */
    String parseNameAndNamespace(String uri, ActionMapping mapping,
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
            // Try to find the namespace in those defined, defaulting to ""
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            namespace = "";
            // Find the longest matching namespace, defaulting to the default
            for (Iterator i = config.getPackageConfigs().values().iterator(); i
                    .hasNext();) {
                String ns = ((PackageConfig) i.next()).getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
            }

            name = uri.substring(namespace.length() + 1);
        }
        String fullName = name;

        if (name != null) {
            int pos = name.lastIndexOf('/');
            if (pos > -1 && pos < name.length() - 1) {
                name = name.substring(pos + 1);
            }
        }

        mapping.setNamespace(namespace);
        mapping.setName(name);
        return fullName;
    }

    /**
     * Drops the extension from the action name
     *
     * @param name
     *            The action name
     * @return The action name without its extension
     */
    String dropExtension(String name) {
        if (name != null) {
            int pos = name.lastIndexOf('.');
            if (pos > -1) {
                return name.substring(0, name.lastIndexOf('.'));
            } else {
                return name;
            }
                 
        }
        return null;
    }

    /**
     * Returns null if no extension is specified.
     */
    String getDefaultExtension() {
        if (extensions == null) {
            return null;
        } else {
            return (String) extensions.get(0);
        }
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
            return isPost(request) && "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

	public String getIdParameterName() {
		return idParameterName;
	}

	@Inject(required=false,value=StrutsConstants.STRUTS_ID_PARAMETER_NAME)
	public void setIdParameterName(String idParameterName) {
		this.idParameterName = idParameterName;
	}
    
    

}
