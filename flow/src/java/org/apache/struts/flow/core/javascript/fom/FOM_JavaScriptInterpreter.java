/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.struts.flow.core.javascript.fom;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;

import org.apache.struts.flow.core.Logger;
import org.apache.struts.flow.core.Factory;

import org.apache.commons.chain.web.WebContext;
import java.lang.reflect.InvocationTargetException;
//import org.apache.avalon.framework.activity.Initializable;
//import org.apache.avalon.framework.configuration.Configurable;
//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.configuration.ConfigurationException;
//mport org.apache.avalon.framework.service.ServiceManager;
//mport org.apache.flow.ResourceNotFoundException;
//mport org.apache.flow.components.ContextHelper;
import org.apache.struts.flow.core.CompilingInterpreter;
import org.apache.struts.flow.core.Interpreter;
import org.apache.struts.flow.core.InvalidContinuationException;
import org.apache.struts.flow.core.*;
import org.apache.struts.flow.core.javascript.ConversionHelper;
import org.apache.struts.flow.core.javascript.JSErrorReporter;
import org.apache.struts.flow.core.javascript.LocationTrackingDebugger;
//import org.apache.struts.flow.core.javascript.ScriptablePointerFactory;
//import org.apache.struts.flow.core.javascript.ScriptablePropertyHandler;
//import org.apache.flow.environment.ObjectModelHelper;
//import org.apache.flow.environment.Redirector;
//import org.apache.flow.environment.Request;
//import org.apache.flow.environment.Session;
//import org.apache.commons.jxpath.JXPathIntrospector;
//import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.struts.flow.core.source.Source;
//import org.apache.regexp.RE;
//import org.apache.regexp.RECompiler;
//import org.apache.regexp.REProgram;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.PropertyException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.continuations.Continuation;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Interface with the JavaScript interpreter.
 *
 * @author <a href="mailto:ovidiu@apache.org">Ovidiu Predescu</a>
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @since March 25, 2002
 * @version CVS $Id: FOM_JavaScriptInterpreter.java 307410 2005-10-09 12:17:33Z reinhard $
 */
public class FOM_JavaScriptInterpreter extends CompilingInterpreter {

    /**
     * A long value is stored under this key in each top level JavaScript
     * thread scope object. When you enter a context any scripts whose
     * modification time is later than this value will be recompiled and reexecuted,
     * and this value will be updated to the current time.
     */
    private final static String LAST_EXEC_TIME = "__PRIVATE_LAST_EXEC_TIME__";

    /**
     * Prefix for session/request attribute storing JavaScript global scope object.
     */
    private static final String USER_GLOBAL_SCOPE = "FOM JavaScript GLOBAL SCOPE/";

    /**
     * The function name to call if the desired function cannot be found.
     */
    private static final String MISSING_FUNCTION_NAME = "unknown";

    /**
     * This is the only optimization level that supports continuations
     * in the Christoper Oliver's Rhino JavaScript implementation
     */
    private static final int OPTIMIZATION_LEVEL = -2;

    /**
     * When was the last time we checked for script modifications. Used
     * only if {@link #reloadScripts} is true.
     */
    private long lastTimeCheck;

    /**
     * Shared global scope for scripts and other immutable objects
     */
    private Global scope;

    /**
     * List of <code>String</code> objects that represent files to be
     * read in by the JavaScript interpreter.
     */
    private List topLevelScripts = new ArrayList();

    private boolean enableDebugger;
    
    private WrapFactory wrapFactory;
    private Map flowVars = new HashMap();
    private Map globalVars = new HashMap();

    /**
     * JavaScript debugger: there's only one of these: it can debug multiple
     * threads executing JS code.
     */
    private static Main debugger;

    static synchronized Main getDebugger() {
        if (debugger == null) {
            final Main db = new Main("Flow Debugger");
            db.pack();
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            size.width *= 0.75;
            size.height *= 0.75;
            db.setSize(size);
            db.setExitAction(new Runnable() {
                    public void run() {
                        db.setVisible(false);
                    }
                });
            db.setOptimizationLevel(OPTIMIZATION_LEVEL);
            db.setVisible(true);
            debugger = db;
            Context.addContextListener(debugger);
        }
        return debugger;
    }
    
    /**
     *  Gets the logger attribute of the JavaScriptInterpreter object
     *
     *@return    The logger value
     */
    public Logger getLogger() {
        return Factory.getLogger();
    }
    
    /**
     *  Sets the wrap factory to use with Rhino
     *
     *@param wf The WrapFactory instance
     */
    public void setWrapFactory(WrapFactory wf) {
        this.wrapFactory = wf;
    }
    
    /**
     *  Adds a class that will register a global variable
     *
     * @param reg The variable registrar
     */
    public void addGlobalVariable(String name, Object var) {
        globalVars.put(name, var);
    }

    /**
     *  Removes a class that will register a global variable
     *
     * @param reg The variable registrar
     */
    public void removeGlobalVariable(String name) {
        globalVars.remove(name);
    }
    
    /**
     *  Adds a class that will register a global variable
     *
     * @param reg The variable registrar
     */
    public void addFlowVariable(String name, FlowVariableFactory fac) {
        flowVars.put(name, fac);
    }

    /**
     *  Removes a class that will register a global variable
     *
     * @param reg The variable registrar
     */
    public void removeFlowVariable(String name) {
        flowVars.remove(name);
    }
    

    /**
     *  Sets the interval between when the script should be looked at to see if
     *  it needs to be reloaded
     *
     *@param  time  The interval time in milliseconds
     */
    public void setCheckTime(long time) {
        checkTime = time;
    }
    
    
    /**
     *  Sets whether to enable the debugger
     *
     *@param  val  The new debugger value
     */
    public void setDebugger(boolean val) {
        enableDebugger = val;
    }


    /**
     *  Sets whether to try to reload modified scripts or not
     *
     *@param  val  True to reload
     */
    public void setReloadScripts(boolean val) {
        reloadScripts = val;
    }

    /**
     *  Initialize the global scope
     *
     *@exception  FlowException  If anything goes wrong
     */
    public void initialize() throws FlowException {
        initialize(null);
    }
    

    /**
     *  Initialize the global scope
     *
     *@exception  FlowException  If anything goes wrong
     */
    public void initialize(List classes) throws FlowException {
        if (enableDebugger) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Flow debugger enabled, creating");
            }
            getDebugger().doBreak();
        }
        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setCompileFunctionsWithDynamicScope(true);
        context.setGeneratingDebug(true);
        if (wrapFactory != null) {
            context.setWrapFactory(wrapFactory);
        }

        try {
            scope = new Global(context);
            
            // Register some handy classes with JavaScript, so we can make
            // use of them from the flow layer.
            initScope(context, scope);

            // Register any custom classes
            if (classes != null) {
                for (Iterator i = classes.iterator(); i.hasNext(); ) {
                    ScriptableObject.defineClass(scope, (Class)i.next());
                }
            }
            
            // Access to Cocoon internal objects
            FOM_Flow.init(scope);
        } catch (Exception e) {
            Context.exit();
            e.printStackTrace();
            throw new FlowException(e);
        }
    }
    
    
    /**
     *  Initialize the global scope
     *
     *@param  context                        The context
     *@param  scope                          The scope to initialize
     *@exception  IllegalAccessException     If anything goes wrong
     *@exception  InstantiationException     If anything goes wrong
     *@exception  InvocationTargetException  If anything goes wrong
     *@exception  JavaScriptException        If anything goes wrong
     */
    protected void initScope(Context context, Global scope) throws IllegalAccessException,
            InstantiationException, InvocationTargetException, JavaScriptException {
        
        WrapFactory factory = context.getWrapFactory();
        for (Iterator i = globalVars.keySet().iterator(); i.hasNext(); ) {
            String name = (String) i.next();
            Object bean = globalVars.get(name);
            
            if (bean instanceof Scriptable) {
                scope.put(name, scope, (Scriptable)bean);
            } else {
                Scriptable var = factory.wrapAsJavaObject(context, scope, bean, bean.getClass());
                scope.put(name, scope, var);
            }
        }        
    }

    private void setFlash(WebContext ctx, Scriptable scope, Map flash) {
        Factory.getLogger().debug("Adding flash to session: "+flash.size());
        if (flash.size() > 0) {
            ctx.getSessionScope().put("flash", new HashMap(flash));
            flash.clear();
        }
    }
    
    private void useFlash(WebContext webctx, Scriptable scope) {
        Map flash = (Map) webctx.getSessionScope().get("flash");
        webctx.getSessionScope().remove("flash");
        if (flash != null) {
            final Context context = Context.getCurrentContext();
            WrapFactory factory = context.getWrapFactory();
            if (flash != null) {
                Factory.getLogger().debug("Adding flash to context");
                String key;
                Object val;
                for (Iterator i = flash.keySet().iterator(); i.hasNext(); ) {
                    key = (String) i.next();
                    val = flash.get(key);
                    Factory.getLogger().debug("Adding flash to context: "+key);
                    
                    scope.put(key, scope, factory.wrap(context, scope, val, val.getClass()));
                }
            }
        }
    }
    
    private void initFlowVariables(Map vars, WebContext ctx) {
        for (Iterator i = vars.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof WebContextAware) {
                ((WebContextAware)obj).init(ctx);
            }
        }  
    }
    
    private void cleanupFlowVariables(Map vars) {
        for (Iterator i = vars.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof WebContextAware) {
                ((WebContextAware)obj).cleanup();
            }
        }  
    }

    

    /**
     * Returns the JavaScript scope, a Scriptable object, from the user
     * session instance. Each interpreter instance can have a scope
     * associated with it.
     *
     * @return a <code>ThreadScope</code> value
     */
    private ThreadScope getSessionScope(WebContext ctx) throws Exception {
        final String scopeID = USER_GLOBAL_SCOPE + getInterpreterID();

        ThreadScope scope = null;

        // Get/create the scope attached to the current context
        //Session session = request.getSession(false);
        //if (session != null) {
        //    scope = (ThreadScope) session.getAttribute(scopeID);
        //} else {
        //    scope = (ThreadScope) request.getAttribute(scopeID);
        //}
        
        // FIXME: This might create a unwanted session
        scope = (ThreadScope) ctx.getSessionScope().get(scopeID);
        if (scope == null) {
            scope = (ThreadScope) ctx.getRequestScope().get(scopeID);
        }
        

        if (scope == null) {
            scope = createThreadScope();
            // Save scope in the request early to allow recursive Flow calls
            ctx.getRequestScope().put(scopeID, scope);
        }

        return scope;
    }

    /**
     * Associates a JavaScript scope, a Scriptable object, with
     * {@link #getInterpreterID() identifier} of this {@link Interpreter}
     * instance.
     *
     * @param scope a <code>ThreadScope</code> value
     */
    private void setSessionScope(ThreadScope scope, WebContext ctx) throws Exception {
        if (scope.useSession) {
            final String scopeID = USER_GLOBAL_SCOPE + getInterpreterID();

            // FIXME: Where "session scope" should go when session is invalidated?
            // Attach the scope to the current context
            try {
                ctx.getSessionScope().put(scopeID, scope);
            } catch (IllegalStateException e) {
                // Session might be invalidated already.
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Got '" + e + "' while trying to set session scope.", e);
                }
            }
        }
    }

    public static class ThreadScope extends ScriptableObject {
        private static final String[] BUILTIN_PACKAGES = {"javax", "org", "com"};

        private ClassLoader classLoader;

        /* true if this scope has assigned any global vars */
        boolean useSession;

        boolean locked = false;
        
        private Map flash = new HashMap();
        
        private Map flowVars = new HashMap();
        
        /**
         * Initializes new top-level scope.
         */
        public ThreadScope(Global scope, Map flowVarFactories, WrapFactory wrapFactory) throws Exception {
            final Context context = Context.getCurrentContext();
            if (wrapFactory != null) {
                context.setWrapFactory(wrapFactory);
            }

            final String[] names = { "importClass" };
            try {
                defineFunctionProperties(names,
                                         ThreadScope.class,
                                         ScriptableObject.DONTENUM);
            } catch (PropertyException e) {
                throw new Error();  // should never happen
            }

            setPrototype(scope);

            // We want this to be a new top-level scope, so set its
            // parent scope to null. This means that any variables created
            // by assignments will be properties of this.
            setParentScope(null);

            // Put in the thread scope the Cocoon object, which gives access
            // to the interpreter object, and some Cocoon objects. See
            // FOM_Flow for more details.
            final Object[] args = {};
            FOM_Flow flow = (FOM_Flow) context.newObject(this,
                                                               "FOM_Flow",
                                                               args);
            flow.setParentScope(this);
            super.put("flow", this, flow);
            
            WrapFactory factory = context.getWrapFactory();
            
            super.put("flash", this, (Scriptable) factory.wrapAsJavaObject(context, this, flash, flash.getClass()));
            
            for (Iterator i = flowVarFactories.keySet().iterator(); i.hasNext(); ) {
                String name = (String) i.next();
                FlowVariableFactory varfactory = (FlowVariableFactory) flowVarFactories.get(name);
                
                Object bean = varfactory.getInstance(this, flow);
                flowVars.put(name, bean);
                if (bean instanceof Scriptable) {
                    super.put(name, this, (Scriptable) bean);
                } else {
                    Scriptable var = (Scriptable) factory.wrapAsJavaObject(context, this, bean, bean.getClass());
                    super.put(name, this, var);
                }
            }  

            defineProperty(LAST_EXEC_TIME,
                           new Long(0),
                           ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
        }

        public String getClassName() {
            return "ThreadScope";
        }
        
        public void setLock(boolean lock) {
            // if we aren't using the session, no need to lock
            this.locked = lock;
        }
        
        /** Used to bypass the lock and useSession setting for internal use only */
        public void putInternal(String name, Scriptable start, Object value) {
            super.put(name, start, value);
        }
        
        public void put(String name, Scriptable start, Object value) {
            //Allow setting values to existing variables, or if this is a
            //java class (used by importClass & importPackage)
            if (this.locked && !has(name, start) && !(value instanceof NativeJavaClass)) {
                // Need to wrap into a runtime exception as Scriptable.put has no throws clause...
                throw new WrappedException (new JavaScriptException("Implicit declaration of global variable '" + name +
                  "' forbidden. Please ensure all variables are explicitely declared with the 'var' keyword"));
            }
            this.useSession = true;
            super.put(name, start, value);
        }

        public void put(int index, Scriptable start, Object value) {
            // FIXME(SW): do indexed properties have a meaning on the global scope?
            if (this.locked && !has(index, start)) {
                throw new WrappedException(new JavaScriptException("Global scope locked. Cannot set value for index " + index));
            }
            this.useSession = true;
            super.put(index, start, value);
        }

        // Invoked after script execution
        void onExec() {
            this.useSession = false;
            super.put(LAST_EXEC_TIME, this, new Long(System.currentTimeMillis()));
        }

        /** Override importClass to allow reloading of classes */
        public static void importClass(Context ctx,
                                       Scriptable thisObj,
                                       Object[] args,
                                       Function funObj) {
            for (int i = 0; i < args.length; i++) {
                Object clazz = args[i];
                if (!(clazz instanceof NativeJavaClass)) {
                    throw Context.reportRuntimeError("Not a Java class: " +
                                                     Context.toString(clazz));
                }
                String s = ((NativeJavaClass) clazz).getClassObject().getName();
                String n = s.substring(s.lastIndexOf('.') + 1);
                thisObj.put(n, thisObj, clazz);
            }
        }

        public void setupPackages(ClassLoader cl) throws Exception {
            final String JAVA_PACKAGE = "JavaPackage";
            if (classLoader != cl) {
                classLoader = cl;
                Scriptable newPackages = new NativeJavaPackage("", cl);
                newPackages.setParentScope(this);
                newPackages.setPrototype(ScriptableObject.getClassPrototype(this, JAVA_PACKAGE));
                super.put("Packages", this, newPackages);
                for (int i = 0; i < BUILTIN_PACKAGES.length; i++) {
                    String pkgName = BUILTIN_PACKAGES[i];
                    Scriptable pkg = new NativeJavaPackage(pkgName, cl);
                    pkg.setParentScope(this);
                    pkg.setPrototype(ScriptableObject.getClassPrototype(this, JAVA_PACKAGE));
                    super.put(pkgName, this, pkg);
                }
            }
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }
        
        public Map getFlash() {
            return flash;
        }
        
        public Map getFlowVariables() {
            return flowVars;
        }
    }

    private ThreadScope createThreadScope() throws Exception {
        return new ThreadScope(scope, flowVars, wrapFactory);
    }

    /**
     * Returns a new Scriptable object to be used as the global scope
     * when running the JavaScript scripts in the context of a request.
     *
     * <p>If you want to maintain the state of global variables across
     * multiple invocations of <code>&lt;map:call
     * function="..."&gt;</code>, you need to instanciate the session
     * object which is a property of the flow object
     * <code>var session = flow.session</code>. This will place the
     * newly create Scriptable object in the user's session, where it
     * will be retrieved from at the next invocation of {@link #callFunction}.</p>
     *
     * @exception Exception if an error occurs
     */
    private void setupContext(WebContext webctx, Context context,
                              ThreadScope thrScope)
    throws Exception {
        // Try to retrieve the scope object from the session instance. If
        // no scope is found, we create a new one, but don't place it in
        // the session.
        //
        // When a user script "creates" a session using
        // flow.createSession() in JavaScript, the thrScope is placed in
        // the session object, where it's later retrieved from here. This
        // behaviour allows multiple JavaScript functions to share the
        // same global scope.

        FOM_Flow flow = (FOM_Flow) thrScope.get("flow", thrScope);
        long lastExecTime = ((Long) thrScope.get(LAST_EXEC_TIME,
                                                 thrScope)).longValue();
        boolean needsRefresh = false;
        if (reloadScripts) {
            long now = System.currentTimeMillis();
            if (now >= lastTimeCheck + checkTime) {
                needsRefresh = true;
            }
            lastTimeCheck = now;
        }
        
        // We need to setup the FOM_Flow object according to the current
        // request. Everything else remains the same.
        ClassLoader contextClassloader = Thread.currentThread().getContextClassLoader();
        thrScope.setupPackages(contextClassloader);
        flow.pushCallContext(this, webctx, getLogger(), null);
        
        // Check if we need to compile and/or execute scripts
        synchronized (compiledScripts) {
            List execList = new ArrayList();
            // If we've never executed scripts in this scope or
            // if reload-scripts is true and the check interval has expired
            // or if new scripts have been specified in the sitemap,
            // then create a list of scripts to compile/execute
            if (lastExecTime == 0 || needsRefresh || needResolve.size() > 0) {
                topLevelScripts.addAll(needResolve);
                if (lastExecTime != 0 && !needsRefresh) {
                    execList.addAll(needResolve);
                } else {
                    execList.addAll(topLevelScripts);
                }
                needResolve.clear();
            }
            // Compile all the scripts first. That way you can set breakpoints
            // in the debugger before they execute.
            for (int i = 0, size = execList.size(); i < size; i++) {
                String sourceURI = (String)execList.get(i);
                ScriptSourceEntry entry =
                    (ScriptSourceEntry)compiledScripts.get(sourceURI);
                if (entry == null) {
                    Source src = this.sourceresolver.resolveURI(sourceURI);
                    entry = new ScriptSourceEntry(src);
                    compiledScripts.put(sourceURI, entry);
                }
                // Compile the script if necessary
                entry.getScript(context, this.scope, needsRefresh, this);
            }
            // Execute the scripts if necessary
            for (int i = 0, size = execList.size(); i < size; i++) {
                String sourceURI = (String) execList.get(i);
                ScriptSourceEntry entry =
                    (ScriptSourceEntry) compiledScripts.get(sourceURI);
                long lastMod = entry.getSource().getLastModified();
                Script script = entry.getScript(context, this.scope, false, this);
                if (lastExecTime == 0 || lastMod > lastExecTime) {
                    script.exec(context, thrScope);
                    thrScope.onExec();
                }
            }
        }
        
    }

    /**
     * Compile filename as JavaScript code
     *
     * @param cx Rhino context
     * @param fileName resource uri
     * @return compiled script
     */
    Script compileScript(Context cx, String fileName) throws Exception {
        Source src = this.sourceresolver.resolveURI(fileName);
        if (src != null) {
            synchronized (compiledScripts) {
                ScriptSourceEntry entry =
                    (ScriptSourceEntry)compiledScripts.get(src.getURI());
                Script compiledScript = null;
                if (entry == null) {
                    compiledScripts.put(src.getURI(),
                            entry = new ScriptSourceEntry(src));
                } else {
                    this.sourceresolver.release(src);
                }
                compiledScript = entry.getScript(cx, this.scope, false, this);
                return compiledScript;
            }
        }
        throw new FlowException(fileName + ": not found");

    }

    protected Script compileScript(Context cx, Scriptable scope, Source src)
    throws Exception {
        PushbackInputStream is = new PushbackInputStream(src.getInputStream(), ENCODING_BUF_SIZE);
        try {
            String encoding = findEncoding(is);
            Reader reader = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
            reader = new BufferedReader(reader);
            Script compiledScript = cx.compileReader(scope, reader,
                    src.getURI(), 1, null);
            return compiledScript;
        } finally {
            is.close();
        }
    }
    
    // A charset name can be up to 40 characters taken from the printable characters of US-ASCII
    // (see http://www.iana.org/assignments/character-sets). So reading 100 bytes should be more than enough.
    private final static int ENCODING_BUF_SIZE = 100;
    // Match 'encoding = xxxx' on the first line
    Pattern encodingRE = Pattern.compile("^.*encoding\\s*=\\s*([^\\s]*)");
    
    /**
     * Find the encoding of the stream, or null if not specified
     */
    String findEncoding(PushbackInputStream is) throws IOException {
        // Read some bytes
        byte[] buffer = new byte[ENCODING_BUF_SIZE];
        int len = is.read(buffer, 0, buffer.length);
        // and push them back
        is.unread(buffer, 0, len);
        
        // Interpret them as an ASCII string
        String str = new String(buffer, 0, len, "ASCII");
        Matcher re = encodingRE.matcher(str);
        if (re.matches()) {
            return re.group(1);
        }
        return null;
    }

    
    /**
     * Calls a JavaScript function, passing <code>params</code> as its
     * arguments. In addition to this, it makes available the parameters
     * through the <code>flow.parameters</code> JavaScript array
     * (indexed by the parameter names).
     *
     * @param funName a <code>String</code> value
     * @param params a <code>List</code> value
     * @param redirector
     * @exception Exception if an error occurs
     */
    public Object callFunction(String funName, List params, WebContext webctx)
    throws Exception {
        return callController(null, funName, params, webctx);
    }
    
    /**
     * Calls a JavaScript function, passing <code>params</code> as its
     * arguments. In addition to this, it makes available the parameters
     * through the <code>flow.parameters</code> JavaScript array
     * (indexed by the parameter names).
     *
     * @param funName a <code>String</code> value
     * @param params a <code>List</code> value
     * @param redirector
     * @exception Exception if an error occurs
     */
    public Object callController(String constName, String funName, List params, WebContext webctx)
    throws Exception {
        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setGeneratingDebug(true);
        context.setCompileFunctionsWithDynamicScope(true);
        context.setErrorReporter(new JSErrorReporter(getLogger()));
        if (wrapFactory != null) {
            context.setWrapFactory(wrapFactory);
        }
        
        LocationTrackingDebugger locationTracker = new LocationTrackingDebugger();
        if (!enableDebugger) {
            //FIXME: add a "tee" debugger that allows both to be used simultaneously
            context.setDebugger(locationTracker, null);
        }
        Object ret = null;
        ThreadScope thrScope = getSessionScope(webctx);
        synchronized (thrScope) {
            ClassLoader savedClassLoader =
                Thread.currentThread().getContextClassLoader();
            FOM_Flow flow = null;
            Scriptable controller = null;
            try {
                try {
                    setupContext(webctx, context, thrScope);
                    
                    flow = (FOM_Flow) thrScope.get("flow", thrScope);

                    // Register the current scope for scripts indirectly called from this function
                    //FOM_JavaScriptFlowHelper.setFOM_FlowScope(flow.getObjectModel(), thrScope);

                    if (enableDebugger) {
                        if (!getDebugger().isVisible()) {
                            // only raise the debugger window if it isn't already visible
                            getDebugger().setVisible(true);
                        }
                    }

                    int size = (params != null ? params.size() : 0);
                    Scriptable parameters = context.newObject(thrScope);
                    for (int i = 0; i < size; i++) {
                        Interpreter.Argument arg = (Interpreter.Argument)params.get(i);
                        if (arg.name == null) {
                            arg.name = "";
                        }
                        parameters.put(arg.name, parameters, arg.value);
                    }
                    flow.setParameters(parameters);

                    controller = thrScope;
                    if (constName != null) {
                        controller = context.newObject(thrScope, constName);
                        
                        // We store the new controller as a variable in order to access it
                        // later in handleContinuation
                        thrScope.putInternal("controller", thrScope, controller);
                    }
                    Object fun = ScriptableObject.getProperty(controller, funName);
                    if (fun == Scriptable.NOT_FOUND) {
                        getLogger().info("Function \"javascript:" + funName + "()\" not found");
                        fun = ScriptableObject.getProperty(controller, MISSING_FUNCTION_NAME);
                        if (fun == Scriptable.NOT_FOUND) {
                            throw new FlowException("Unable to find either the " + funName +
                                " or " + MISSING_FUNCTION_NAME + " function.");
                        }        
                    }
                    useFlash(webctx, controller);
                    initFlowVariables(thrScope.getFlowVariables(), webctx);
                    thrScope.setLock(true);
                    ret = ScriptRuntime.call(context, fun, controller, new Object[0], controller);
                    if (constName != null) {
                        Map map = ConversionHelper.jsobjectToMap((Scriptable)controller);
                        webctx.getRequestScope().putAll(map);
                    }
                } catch (JavaScriptException ex) {
                    throw locationTracker.getException("Error calling flowscript function " + funName, ex);
                } catch (EcmaError ee) {
                    throw locationTracker.getException("Error calling function " + funName, ee);
                } catch (WrappedException ee) {
                    throw locationTracker.getException("Error calling function " + funName, ee);
                }
            } finally {
                thrScope.setLock(false);
                setSessionScope(thrScope, webctx);
                if (flow != null) {
                    flow.popCallContext();
                }
                cleanupFlowVariables(thrScope.getFlowVariables());
                setFlash(webctx, controller, thrScope.getFlash());
                Context.exit();
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
        
        return ret;
    }

    public Object handleContinuation(String id, List params,
                                   WebContext webctx) throws Exception
    {
        Object ret = null;
        WebContinuation wk = continuationsMgr.lookupWebContinuation(id, getInterpreterID(), webctx);

        if (wk == null) {
            /*
             * Throw an InvalidContinuationException to be handled inside the
             * <map:handle-errors> sitemap element.
             */
            throw new InvalidContinuationException("The continuation ID " + id + " is invalid.");
        }

        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setGeneratingDebug(true);
        context.setCompileFunctionsWithDynamicScope(true);
        LocationTrackingDebugger locationTracker = new LocationTrackingDebugger();
        if (wrapFactory != null) {
            context.setWrapFactory(wrapFactory);
        }
        if (!enableDebugger) {
            //FIXME: add a "tee" debugger that allows both to be used simultaneously
            context.setDebugger(locationTracker, null);
        }

        // Obtain the continuation object from it, and setup the
        // FOM_Flow object associated in the dynamic scope of the saved
        // continuation with the environment and context objects.
        Continuation k = (Continuation)wk.getContinuation();
        ThreadScope kScope = (ThreadScope)k.getParentScope();
        synchronized (kScope) {
            ClassLoader savedClassLoader =
                Thread.currentThread().getContextClassLoader();
            FOM_Flow flow = null;
            try {
                Thread.currentThread().setContextClassLoader(kScope.getClassLoader());
                
                flow = (FOM_Flow)kScope.get("flow", kScope);
                kScope.setLock(true);
                flow.pushCallContext(this, webctx, getLogger(), wk);

                // Register the current scope for scripts indirectly called from this function
                //FOM_JavaScriptFlowHelper.setFOM_FlowScope(flow.getObjectModel(), kScope);

                if (enableDebugger) {
                    getDebugger().setVisible(true);
                }
                Scriptable parameters = context.newObject(kScope);
                int size = params != null ? params.size() : 0;
                for (int i = 0; i < size; i++) {
                    Interpreter.Argument arg = (Interpreter.Argument)params.get(i);
                    parameters.put(arg.name, parameters, arg.value);
                }
                flow.setParameters(parameters);
                FOM_WebContinuation fom_wk = new FOM_WebContinuation(wk);
                fom_wk.setParentScope(kScope);
                fom_wk.setPrototype(ScriptableObject.getClassPrototype(kScope,
                                                                       fom_wk.getClassName()));
                initFlowVariables(kScope.getFlowVariables(), webctx);
                Object[] args = new Object[] {k, fom_wk};
                try {
                    ret = ScriptableObject.callMethod(flow,
                                                "handleContinuation", args);
                } catch (JavaScriptException ex) {
                    throw locationTracker.getException("Error calling continuation", ex);

                } catch (EcmaError ee) {
                    throw locationTracker.getException("Error calling continuation", ee);

                }
                
                Object controller = kScope.get("controller", kScope);
                if (controller != null && controller != Scriptable.NOT_FOUND) {
                    Map map = ConversionHelper.jsobjectToMap((Scriptable)controller);
                    webctx.getRequestScope().putAll(map);
                }
            } finally {
                kScope.setLock(false);
                setSessionScope(kScope, webctx);
                if (flow != null) {
                    flow.popCallContext();
                }
                setFlash(webctx, null, kScope.getFlash());
                cleanupFlowVariables(kScope.getFlowVariables());
                Context.exit();
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
            return ret;
        }
    }

    private Throwable unwrap(JavaScriptException e) {
        Object value = e.getValue();
        while (value instanceof Wrapper) {
            value = ((Wrapper)value).unwrap();
        }
        if (value instanceof Throwable) {
            return (Throwable)value;
        }
        return e;
    }
}
