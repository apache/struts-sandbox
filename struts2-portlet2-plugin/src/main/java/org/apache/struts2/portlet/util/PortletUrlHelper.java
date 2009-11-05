/*
 * $Id: PortletUrlHelper.java 582626 2007-10-07 13:26:12Z mrdon $
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
package org.apache.struts2.portlet.util;

import static org.apache.struts2.portlet.PortletContstants.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.BaseURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.PortletRequest;
import javax.portlet.MimeResponse;
import javax.portlet.WindowState;

import org.apache.struts2.StrutsException;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.commons.lang.xwork.StringUtils;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
/**
 * Helper class for creating Portlet URLs. Portlet URLs are fundamentally different from regular
 * servlet URLs since they never target the application itself; all requests go through the portlet
 * container and must therefore be programatically constructed using the
 * {@link javax.portlet.MimeResponse#createActionURL()} and
 * {@link javax.portlet.MimeResponse#createRenderURL()} APIs.
 *
 */
public class PortletUrlHelper {
    public static final String ENCODING = "UTF-8";

    private static final Logger LOG = LoggerFactory.getLogger(PortletUrlHelper.class);

    /**
     * Create a portlet URL with for the specified action and namespace.
     *
     * @param action The action the URL should invoke.
     * @param namespace The namespace of the action to invoke.
     * @param method The method of the action to invoke.
     * @param params The parameters of the URL.
     * @param type The type of the url, either <tt>action</tt> or <tt>render</tt>
     * @param mode The PortletMode of the URL.
     * @param state The WindowState of the URL.
     * @return The URL String.
     */
    public static String buildUrl(String action, String namespace, String method, Map<String, Object> params,
            String type, String mode, String state) {
        return buildUrl(action, namespace, method, params, null, type, mode, state,
                true, true);
    }

    /**
     * Create a portlet URL with for the specified action and namespace.
     *
     * @see #buildUrl(String, String, Map, String, String, String)
     */
    public static String buildUrl(String action, String namespace, String method, Map<String, Object> params,
            String scheme, String type, String portletMode, String windowState,
            boolean includeContext, boolean encodeResult) {
    	StringBuffer resultingAction = new StringBuffer();
        PortletRequest request = PortletActionContext.getRequest();
        MimeResponse response = (MimeResponse)PortletActionContext.getResponse();
        LOG.debug("Creating url. Action = " + action + ", Namespace = "
                + namespace + ", Type = " + type);
        namespace = prependNamespace(namespace, portletMode);
        if (StringUtils.isEmpty(portletMode)) {
            portletMode = PortletActionContext.getRequest().getPortletMode().toString();
        }
        String result = null;
        int paramStartIndex = action.indexOf('?');
        if (paramStartIndex > 0) {
            String value = action;
            action = value.substring(0, value.indexOf('?'));
            String queryStr = value.substring(paramStartIndex + 1);
            StringTokenizer tok = new StringTokenizer(queryStr, "&");
            while (tok.hasMoreTokens()) {
                String paramVal = tok.nextToken();
                String key = paramVal.substring(0, paramVal.indexOf('='));
                String val = paramVal.substring(paramVal.indexOf('=') + 1);
                params.put(key, new String[] { val });
            }
        }
        if (StringUtils.isNotEmpty(namespace)) {
            resultingAction.append(namespace);
            if(!action.startsWith("/") && !namespace.endsWith("/")) {
                resultingAction.append("/");
            }
        }
        resultingAction.append(action);
        if(StringUtils.isNotEmpty(method)) {
        	resultingAction.append("!").append(method);
        }
        if (LOG.isDebugEnabled()) LOG.debug("Resulting actionPath: " + resultingAction);
        params.put(ACTION_PARAM, new String[] { resultingAction.toString() });

        BaseURL url = null;
        if ("action".equalsIgnoreCase(type)) {
            if (LOG.isDebugEnabled()) LOG.debug("Creating action url");
            url = response.createActionURL();
        }
        else if("resource".equalsIgnoreCase(type)) {
        	if (LOG.isDebugEnabled()) LOG.debug("Creating resource url");
        	url = response.createResourceURL();
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Creating render url");
            url = response.createRenderURL();
        }

        params.put(MODE_PARAM, new String[]{portletMode});
        url.setParameters(ensureParamsAreStringArrays(params));

        if ("HTTPS".equalsIgnoreCase(scheme)) {
            try {
                url.setSecure(true);
            } catch (PortletSecurityException e) {
                LOG.error("Cannot set scheme to https", e);
            }
        }
        if(url instanceof PortletURL) {
        	try {
                final PortletURL portletUrl = (PortletURL) url;
                portletUrl.setPortletMode(getPortletMode(request, portletMode));
        		portletUrl.setWindowState(getWindowState(request, windowState));
        	} catch (Exception e) {
        		LOG.error("Unable to set mode or state:" + e.getMessage(), e);
        	}
        }
        result = url.toString();
        // TEMP BUG-WORKAROUND FOR DOUBLE ESCAPING OF AMPERSAND
        if(result.indexOf("&amp;") >= 0) {
            result = result.replace("&amp;", "&");
        }
        return result;

    }

    /**
     *
     * Prepend the namespace configuration for the specified namespace and PortletMode.
     *
     * @param namespace The base namespace.
     * @param portletMode The PortletMode.
     *
     * @return prepended namespace.
     */
    private static String prependNamespace(String namespace, String portletMode) {
        StringBuffer sb = new StringBuffer();
        PortletMode mode = PortletActionContext.getRequest().getPortletMode();
        if(StringUtils.isNotEmpty(portletMode)) {
            mode = new PortletMode(portletMode);
        }
        String portletNamespace = PortletActionContext.getPortletNamespace();
        String modeNamespace = (String)PortletActionContext.getModeNamespaceMap().get(mode);
        if (LOG.isDebugEnabled()) LOG.debug("PortletNamespace: " + portletNamespace + ", modeNamespace: " + modeNamespace);
        if(StringUtils.isNotEmpty(portletNamespace)) {
            sb.append(portletNamespace);
        }
        if(StringUtils.isNotEmpty(modeNamespace)) {
            if(!modeNamespace.startsWith("/")) {
                sb.append("/");
            }
            sb.append(modeNamespace);
        }
        if(StringUtils.isNotEmpty(namespace)) {
            if(!namespace.startsWith("/")) {
                sb.append("/");
            }
            sb.append(namespace);
        }
        if (LOG.isDebugEnabled()) LOG.debug("Resulting namespace: " + sb);
        return sb.toString();
    }

    /**
     * Encode an url to a non Struts action resource, like stylesheet, image or
     * servlet.
     *
     * @param value
     * @return encoded url to non Struts action resources.
     */
    public static String buildResourceUrl(String value, Map params) {
        StringBuffer sb = new StringBuffer();
        // Relative URLs are not allowed in a portlet
        if (!value.startsWith("/")) {
            sb.append("/");
        }
        sb.append(value);
        if(params != null && params.size() > 0) {
            sb.append("?");
            Iterator it = params.keySet().iterator();
            try {
            while(it.hasNext()) {
                String key = (String)it.next();
                String val = (String)params.get(key);

                sb.append(URLEncoder.encode(key, ENCODING)).append("=");
                sb.append(URLEncoder.encode(val, ENCODING));
                if(it.hasNext()) {
                    sb.append("&");
                }
            }
            } catch (UnsupportedEncodingException e) {
                throw new StrutsException("Encoding "+ENCODING+" not found");
            }
        }
        MimeResponse resp = (MimeResponse)PortletActionContext.getResponse();
        PortletRequest req = PortletActionContext.getRequest();
        return resp.encodeURL(req.getContextPath() + sb.toString());
    }

    /**
     * Will ensure that all entries in <code>params</code> are String arrays,
     * as requried by the setParameters on the PortletURL.
     *
     * @param params The parameters to the URL.
     * @return A Map with all parameters as String arrays.
     */
    public static Map<String, String[]> ensureParamsAreStringArrays(Map<String, Object> params) {
        Map<String, String[]> result = null;
        if (params != null) {
            result = new LinkedHashMap<String, String[]>(params.size());
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object val = params.get(key);
                if (val instanceof String[]) {
                    result.put(key, (String[])val);
                } else {
                    result.put(key, new String[] { val.toString() });
                }
            }
        }
        return result;
    }

    /**
     * Convert the given String to a WindowState object.
     *
     * @param portletReq The PortletRequest.
     * @param windowState The WindowState as a String.
     * @return The WindowState that mathces the <tt>windowState</tt> String, or if
     * the String is blank, the current WindowState.
     */
    private static WindowState getWindowState(PortletRequest portletReq,
            String windowState) {
        WindowState state = portletReq.getWindowState();
        if (StringUtils.isNotEmpty(windowState)) {
            if ("maximized".equalsIgnoreCase(windowState)) {
                state = WindowState.MAXIMIZED;
            } else if ("normal".equalsIgnoreCase(windowState)) {
                state = WindowState.NORMAL;
            } else if ("minimized".equalsIgnoreCase(windowState)) {
                state = WindowState.MINIMIZED;
            }
        }
        if(state == null) {
            state = WindowState.NORMAL;
        }
        return state;
    }

    /**
     * Convert the given String to a PortletMode object.
     *
     * @param portletReq The PortletRequest.
     * @param portletMode The PortletMode as a String.
     * @return The PortletMode that mathces the <tt>portletMode</tt> String, or if
     * the String is blank, the current PortletMode.
     */
    private static PortletMode getPortletMode(PortletRequest portletReq,
            String portletMode) {
        PortletMode mode = portletReq.getPortletMode();

        if (StringUtils.isNotEmpty(portletMode)) {
            if ("edit".equalsIgnoreCase(portletMode)) {
                mode = PortletMode.EDIT;
            } else if ("view".equalsIgnoreCase(portletMode)) {
                mode = PortletMode.VIEW;
            } else if ("help".equalsIgnoreCase(portletMode)) {
                mode = PortletMode.HELP;
            }
        }
        if(mode == null) {
            mode = PortletMode.VIEW;
        }
        return mode;
    }
}
