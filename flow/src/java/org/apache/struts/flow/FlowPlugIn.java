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

import java.util.*;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.flow.core.Factory;
import org.apache.struts.flow.core.CompilingInterpreter;
import org.apache.struts.flow.core.javascript.fom.FOM_JavaScriptInterpreter;
import org.apache.struts.flow.core.DefaultFlowVariableFactory;
import org.apache.struts.flow.sugar.SugarWrapFactory;
import org.apache.struts.flow.ibatis.SqlMap;
import org.apache.struts.flow.core.source.impl.ChainSourceResolver;

import org.apache.commons.chain.web.servlet.ServletWebContext;

import com.ibatis.sqlmap.client.*;
import com.ibatis.common.resources.*;

/**
 *  Initializes the Flow interpreter and loads system and user scripts. There
 *  are two mutually exclusive ways to specify scripts:
 *  <ol>
 *    <li> <code>scripts</code> - A comma-delimited list of scripts to load. All
 *    loaded scripts share the same script scope.</li>
 *    <li> <code>scriptBase</code> - The base path to use when resolving
 *    scripts. Each action mapping then needs to set the <code>script</code>
 *    property for the name of the actual script. The scripts will be looked up
 *    by concatinating the script base with the script name. Each script gets
 *    its own scope.</li>
 *  </ol>
 *  <p>
 *
 *  Scripts can be located either in the webapp directory, as an absolute path
 *  on the filesystem, or in the classpath like in a jar file. </p> <p>
 *
 *  The following optional properties can be specified:</p>
 *  <ul>
 *    <li> <code>debugger</code> - Whether to enable the Swing debugger</li>
 *
 *    <li> <code>reloadScripts</code> - Whether to enable the reloading of
 *    scripts if their contents have been modified. The check for modification
 *    occurs every <code>checkTime</code> interval</li>
 *    <li> <code>checkTime</code> - The interval time in milliseconds between
 *    checking for script modifications, if enabled</li>
 *    <li> <code>timeToLive</code> - The length in milliseconds continuations
 *    will live from when they were last accessed</li>
 *  </ul>
 *
 */
public final class FlowPlugIn implements PlugIn {

    /**  Servlet context key flow interpreter is stored under */
    public final static String INTERPRETER_KEY = "interpreter";

    /**  Commons Logging instance. */
    private static Log log = LogFactory.getLog(FlowPlugIn.class);

    private ServletContext context;
    private String scripts = null;
    private String scriptBase = null;
    private List classesToRegister = new ArrayList();

    private boolean debugger = false;
    private boolean reloadScripts;
    private long checkTime;

    private int ttl;


    /**
     *  Gets a comma delimitted list of user scripts.
     *
     *@return    comma delimited list of user script path names
     */
    public String getScripts() {
        return scripts;
    }


    /**
     *  Sets a comma delimitted list of user scripts.
     *
     *@param  scripts  delimited list of user script path names
     */
    public void setScripts(String scripts) {
        this.scripts = scripts;
    }


    /**
     *  Sets the base path to resolve scripts against
     *
     *@param  scriptBase  The base path
     */
    public void setScriptBase(String scriptBase) {
        this.scriptBase = scriptBase;
    }


    /**
     *  Sets the value of reloadScripts.
     *
     *@param  reloadScripts  The value to assign reloadScripts.
     */
    public void setReloadScripts(boolean reloadScripts) {
        this.reloadScripts = reloadScripts;
    }


    /**
     *  Sets the value of checkTime.
     *
     *@param  checkTime  The value to assign checkTime.
     */
    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }


    /**
     *  Sets the debugger attribute of the FlowPlugIn object
     *
     *@param  val  The new debugger value
     */
    public void setDebugger(boolean val) {
        debugger = val;
    }


    /**
     *  Sets the timeToLive attribute of the FlowPlugIn object
     *
     *@param  ttl  The new timeToLive value
     */
    public void setTimeToLive(int ttl) {
        this.ttl = ttl;
    }

    /**
     *  Initialize the flow interpreter
     *
     *@param  servlet               The ActionServlet for this web application
     *@param  config                The ModuleConfig for our owning module
     *@exception  ServletException  if we cannot configure ourselves correctly
     */
    public void init(ActionServlet servlet, ModuleConfig config)
             throws ServletException {

        if ((scripts == null || scripts.length() == 0) && scriptBase == null) {
            throw new ServletException("No scripts or script base defined");
        }
        String key = INTERPRETER_KEY+"/"+config.getPrefix();
        context = servlet.getServletContext();
        Factory.setLogger(new CommonsLogger());
        Factory.getContinuationsManager().setDefaultTimeToLive(ttl);

        if (scripts != null && scripts.length() > 0) {
            CompilingInterpreter interp = createInterpreter(config.getPrefix());
            context.setAttribute(key, interp);
            String path = null;
            String paths = scripts;
            try {
                // Process each specified resource path
                while (paths.length() > 0) {
                    int comma = paths.indexOf(',');
                    if (comma >= 0) {
                        path = paths.substring(0, comma).trim();
                        paths = paths.substring(comma + 1);
                    } else {
                        path = paths.trim();
                        paths = "";
                    }
        
                    if (path.length() < 1) {
                        break;
                    }
                    if (log.isInfoEnabled()) {
                        log.info("Registering script '" + path + "'");
                    }
                    interp.register(path);
                }
            } catch (Exception ex) {
                throw new ServletException("Unable to create global JavaScript interpreter and register scripts", ex);
            }
        } else {
            Map map =
                new HashMap() {
                    public Object get(Object key) {
                        CompilingInterpreter interp = (CompilingInterpreter) super.get(key);
                        if (interp == null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Creating interpreter for " + key);
                            }
                            interp = createInterpreter(key.toString());
                            interp.register(scriptBase + key);
                            put(key, interp);
                        }
                        return interp;
                    }
                };
            log.debug("Pushing interpreter map in context: "+key);
            context.setAttribute(key, map);
        }
    }


    private CompilingInterpreter createInterpreter(String id) {
        FOM_JavaScriptInterpreter interp = new FOM_JavaScriptInterpreter();
        interp.setInterpreterID(id);
        interp.setSourceResolver(new ChainSourceResolver(new ServletWebContext(context, null, null)));
        interp.setDebugger(debugger);
        interp.setCheckTime(checkTime);
        interp.setReloadScripts(reloadScripts);
        interp.setWrapFactory(new SugarWrapFactory());
        
        classesToRegister.add(SqlMap.class);
        interp.initialize(classesToRegister);
        interp.register("/org/apache/struts/flow/core/javascript/fom/fom_system.js");
        interp.register("/org/apache/struts/flow/core/javascript/fom/template.js");
        interp.addFlowVariable("struts", new DefaultFlowVariableFactory(Struts.class));
        interp.addFlowVariable("params", new DefaultFlowVariableFactory(Params.class));
        return interp;
    }


    /**
     *  Release any resources that were allocated at initialization, including
     *  any continuations.
     */
    public void destroy() {

        log.info("Finalizing flow plug in");
        Factory.getContinuationsManager().destroy();

        context.removeAttribute(INTERPRETER_KEY);
        scriptBase = null;
        context = null;
        scripts = null;
    }

}

