/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow;

import org.apache.struts.action.ActionMapping;
import java.util.*;

/**
 *  Stores and manages information that points to an action.
 *
 *@version    $Revision: 1.3 $ $Date: 2004/06/14 18:46:34 $
 */

public class Forward {

    private String uri;
    private String module;
    private String controller;
    private String action;
    private String contid;
    private boolean redirect;
    private Map params;
    private Map bizData;
    private Map options;


    public Forward() { }


    public void populate(Map map) {
        this.uri = (String) setIfNotNull(uri, map.get("uri"));
        this.module = (String) setIfNotNull(module, map.get("module"));
        this.action = (String) setIfNotNull(action, map.get("action"));
        this.params = (Map) setIfNotNull(params, map.get("params"));
        this.controller = (String) setIfNotNull(controller, map.get("controller"));
        this.redirect = Boolean.TRUE.equals(map.get("redirect"));
        this.options = map;
    }

    protected Object setIfNotNull(Object orig, Object obj) {
        return (obj != null ? obj : orig);
    }
            


    /**
     *  Returns the value of redirect.
     *
     *@return    The redirect value
     */
    public boolean isRedirect() {
        return redirect;
    }


    /**
     *  Sets the value of redirect.
     *
     *@param  redirect  The value to assign redirect.
     */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }


    /**
     *  Returns the value of uri.
     *
     *@return    The uri value
     */
    public String getUri() {
        return uri;
    }


    /**
     *  Sets the value of uri.
     *
     *@param  uri  The value to assign uri.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }


    /**
     *  Returns the value of module.
     *
     *@return    The module value
     */
    public String getModule() {
        return module;
    }


    /**
     *  Sets the value of module.
     *
     *@param  module  The value to assign module.
     */
    public void setModule(String module) {
        this.module = module;
    }


    /**
     *  Returns the value of controller.
     *
     *@return    The controller value
     */
    public String getController() {
        return controller;
    }


    /**
     *  Sets the value of controller.
     *
     *@param  controller  The value to assign controller.
     */
    public void setController(String controller) {
        this.controller = controller;
    }


    /**
     *  Returns the value of action.
     *
     *@return    The action value
     */
    public String getAction() {
        return action;
    }


    /**
     *  Sets the value of action.
     *
     *@param  action  The value to assign action.
     */
    public void setAction(String action) {
        this.action = action;
    }


    /**
     *  Returns the value of HashMap().
     *
     *@return    The params value
     */
    public Map getParams() {
        return params;
    }


    /**
     *  Sets the value of HashMap().
     *
     *@param  params     The new params value
     */
    public void setParams(Map params) {
        this.params = params;
    }


    /**
     *  Returns the value of HashMap().
     *
     *@return    The bizData value
     */
    public Map getBizData() {
        return bizData;
    }


    /**
     *  Sets the value of HashMap().
     *
     *@param  bizData    The new bizData value
     */
    public void setBizData(Map bizData) {
        this.bizData = bizData;
    }


    /**
     *  Returns the value of contid.
     *
     *@return    The contid value
     */
    public String getContid() {
        return contid;
    }


    /**
     *  Sets the value of contid.
     *
     *@param  contid  The value to assign contid.
     */
    public void setContid(String contid) {
        this.contid = contid;
    }


    public String toUri(String pattern) {
        if (uri != null) {
            return uri;
        } else {
            Map vars = new HashMap();
            vars.put("M", module);
            vars.put("A", action);
            vars.put("C", controller);
            return createPath(pattern, vars);
        } 
    }


    protected String createPath(String pattern, Map vars) {
        StringBuffer path = new StringBuffer();
        char c;
        Object val;
        for (int x = 0; x < pattern.length(); x++) {
            c = pattern.charAt(x);
            if (c == '$') {
                c = pattern.charAt(++x);
                val = vars.get(String.valueOf(c));
                if (val != null) {
                    path.append(val.toString());
                } else if (c == '$') {
                    path.append("$$");
                } else {
                    // swallow unknown variables
                }
            } else {
                path.append(c);
            }
        }
        return path.toString();
    }
}

