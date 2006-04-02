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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts.flow.Constants;

//import org.apache.avalon.framework.context.Context;
//import org.apache.avalon.framework.context.ContextException;
//import org.apache.avalon.framework.logger.Logger;
//import org.apache.avalon.framework.service.ServiceManager;
//import org.apache.cocoon.components.ContextHelper;
//import org.apache.cocoon.components.LifecycleHelper;
import org.apache.struts.flow.core.ContinuationsManager;
import org.apache.struts.flow.Forward;
import org.apache.struts.flow.core.WebContinuation;
import org.apache.struts.flow.core.javascript.ConversionHelper;
import org.apache.struts.flow.core.Interpreter.Argument;
//import org.apache.cocoon.environment.ObjectModelHelper;
//import org.apache.cocoon.environment.Redirector;
//import org.apache.cocoon.environment.Request;
//import org.apache.cocoon.environment.Response;
//import org.apache.cocoon.environment.Session;
//import org.apache.cocoon.util.ClassUtils;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.continuations.Continuation;

import org.apache.commons.chain.web.WebContext;
import org.apache.struts.flow.core.Factory;
import org.apache.struts.flow.core.Logger;

/**
 * Implementation of FOM (Flow Object Model).
 *
 * @since 2.1
 * @author <a href="mailto:coliver.at.apache.org">Christopher Oliver</a>
 * @author <a href="mailto:reinhard.at.apache.org">Reinhard P\u00F6tz</a>
 * @version CVS $Id: FOM_Flow.java 292491 2005-09-29 17:44:16Z bloritsch $
 */
public class FOM_Flow extends ScriptableObject {

    class CallContext {
        CallContext caller;
        FOM_JavaScriptInterpreter interpreter;
        WebContext webctx;
        Logger logger;
        Scriptable context;
        Scriptable parameters;
        Scriptable log;
        WebContinuation lastContinuation;
        FOM_WebContinuation fwk;
        PageLocalScopeImpl currentPageLocal;

        public CallContext(CallContext caller,
                           FOM_JavaScriptInterpreter interp,
                           WebContext webctx,
                           Logger logger,
                           WebContinuation lastContinuation) {
            this.caller = caller;
            this.interpreter = interp;
            this.webctx = webctx;
            this.logger = logger;
            this.lastContinuation = lastContinuation;
            if (lastContinuation != null) {
                fwk = new FOM_WebContinuation(lastContinuation);
                Scriptable scope = FOM_Flow.this.getParentScope();
                fwk.setParentScope(scope);
                fwk.setPrototype(getClassPrototype(scope, fwk.getClassName()));
                this.currentPageLocal = fwk.getPageLocal();
            }
            if (this.currentPageLocal != null) {
                // "clone" the page local scope
                this.currentPageLocal = this.currentPageLocal.duplicate();
            } else {
                this.currentPageLocal = new PageLocalScopeImpl(getTopLevelScope(FOM_Flow.this));
            }
            pageLocal.setDelegate(this.currentPageLocal);
        }

        public FOM_WebContinuation getLastContinuation() {
            return fwk;
        }

        public void setLastContinuation(FOM_WebContinuation fwk) {
            this.fwk = fwk;
            if (fwk != null) {
                pageLocal.setDelegate(fwk.getPageLocal());
                this.lastContinuation = fwk.getWebContinuation();
            } else {
                this.lastContinuation = null;
            }
        }

        public WebContext getWebContext() {
            return webctx;
        }
        
        public Scriptable getContext() {
            if (context != null) {
                return context;
            }
            context = org.mozilla.javascript.Context.toObject(webctx, getParentScope());
            return context;
        }

        public Scriptable getLog() {
            if (log != null) {
                return log;
            }
            log = org.mozilla.javascript.Context.toObject(logger, getParentScope());
            return log;
        }

        public Scriptable getParameters() {
            return parameters;
        }

        public void setParameters(Scriptable parameters) {
            this.parameters = parameters;
        }
    }

    private CallContext currentCall;
    protected PageLocalScopeHolder pageLocal;

    public String getClassName() {
        return "FOM_Flow";
    }


    // Called by FOM_JavaScriptInterpreter
    static void init(Scriptable scope) throws Exception {
        //FIXME(SW) what is the exact purpose of defineClass() ??
        defineClass(scope, FOM_Flow.class);
//        defineClass(scope, FOM_Request.class);
//        defineClass(scope, FOM_Response.class);
//        defineClass(scope, FOM_Cookie.class);
//        defineClass(scope, FOM_Session.class);
//        defineClass(scope, FOM_Context.class);
//        defineClass(scope, FOM_Log.class);
        defineClass(scope, FOM_WebContinuation.class);
        defineClass(scope, PageLocalImpl.class);
    }

    void pushCallContext(FOM_JavaScriptInterpreter interp,
                         WebContext webctx,
                         Logger logger,
                         WebContinuation lastContinuation) {
        if (pageLocal == null) {
            pageLocal = new PageLocalScopeHolder(getTopLevelScope(this));
        }
        
        this.currentCall = new CallContext(currentCall, interp, webctx,
                                           logger, lastContinuation);
    }

    void popCallContext() {
        // Clear the scope attribute
        //FOM_JavaScriptFlowHelper.setFOM_FlowScope(this.getObjectModel(), null);

        this.currentCall = this.currentCall.caller;
        // reset current page locals
        if (this.currentCall != null) {
            pageLocal.setDelegate(this.currentCall.currentPageLocal);
        } else {
            pageLocal.setDelegate(null);
        }
    }


    public FOM_WebContinuation jsGet_continuation() {
        // FIXME: This method can return invalid continuation! Is it OK to do so?
        return currentCall.getLastContinuation();
    }

    public void jsSet_continuation(Object obj) {
        FOM_WebContinuation fwk = (FOM_WebContinuation)ConversionHelper.jsobjectToObject(obj);
        currentCall.setLastContinuation(fwk);
    }

    public FOM_WebContinuation jsFunction_forward(Object options,
                                                   Object bizdata,
                                                   Object wk)
        throws Exception {
            
        WebContext ctx = currentCall.getWebContext();
        FOM_WebContinuation fom_wk = (FOM_WebContinuation)ConversionHelper.jsobjectToObject(wk);
        
        Forward forward = (Forward) ctx.get(Constants.FORWARD_KEY);
       
        if (fom_wk != null) {
            // save page locals
            fom_wk.setPageLocal(pageLocal.getDelegate());
            forward.setContid(fom_wk.jsGet_id());
        }
       
        if (options instanceof String) {
            forward.setUri((String) options);
        } else if (options instanceof Scriptable 
                && options != Undefined.instance) {
            Map vals = (Map)ConversionHelper.jsobjectToMap((Scriptable)options);
            forward.populate(vals);
        }
        
        if (bizdata != null && bizdata != Undefined.instance) {
            forward.setBizData((Map)ConversionHelper.jsobjectToMap((Scriptable)bizdata));
        }
        ctx.put(Constants.FORWARD_KEY, forward);
        return fom_wk;
    }
    
    public String jsFunction_calculateUri(Object fwd, Object options, String pattern) {
        Forward forward = (Forward)fwd;
        if (options instanceof String) {
            forward.setUri((String) options);
        } else if (options instanceof Scriptable 
                && options != Undefined.instance) {
            forward.populate((Map)ConversionHelper.jsobjectToMap((Scriptable)options));
        }
        return forward.toUri(pattern);
    }

    public Scriptable jsFunction_createPageLocal() {
        return pageLocal.createPageLocal();
    }

/*

 NOTE (SM): These are the hooks to the future FOM Event Model that will be
 designed in the future. It has been postponed because we think
 there are more important things to do at the moment, but these
 are left here to indicate that they are planned.

    public void jsFunction_addEventListener(String eventName,
                                            Object function) {
        // what is this?
    }

    public void jsFunction_removeEventListener(String eventName,
                                               Object function) {
        // what is this?
    }

*/


    /**
     * Load the script file specified as argument.
     *
     * @param filename a <code>String</code> value
     * @return an <code>Object</code> value
     * @exception JavaScriptException if an error occurs
     */
    public Object jsFunction_load( String filename )
        throws Exception {
        org.mozilla.javascript.Context cx =
            org.mozilla.javascript.Context.getCurrentContext();
        Scriptable scope = getParentScope();
        Script script = getInterpreter().compileScript(cx, filename);
        return script.exec( cx, scope );
    }


    /**
     * Base JS wrapper for Cocoon's request/session/context objects.
     * <p>
     * FIXME(SW): The only thing added to the regular Java object is the fact that
     * attributes can be accessed as properties. Do we want to keep this?
     */
    private static abstract class AttributeHolderJavaObject extends NativeJavaObject {
        
        private static Map classProps = new HashMap();
        private Set propNames;

        public AttributeHolderJavaObject(Scriptable scope, Object object, Class clazz) {
            super(scope, object, clazz);
            this.propNames = getProperties(object.getClass());
        }
        
        /** Compute the names of JavaBean properties so that we can filter them our in get() */
        private static Set getProperties(Class clazz) {
            Set result = (Set)classProps.get(clazz);
            if (result == null) {
                try {
                    PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
                    result = new HashSet();
                    for (int i = 0; i < descriptors.length; i++) {
                        result.add(descriptors[i].getName());
                    }
                } catch (IntrospectionException e) {
                    // Cannot introspect: just consider there are no properties
                    result = Collections.EMPTY_SET;
                }
                classProps.put(clazz, result);
            }
            return result;
        }
        
        
        protected abstract Enumeration getAttributeNames();
        protected abstract Object getAttribute(String name);
        
        public Object[] getIds() {
            // Get class Ids
            Object [] classIds = super.getIds();
            
            // and add attribute names
            ArrayList idList = new ArrayList(Arrays.asList(classIds));
            Enumeration iter = getAttributeNames();
            while(iter.hasMoreElements()) {
                idList.add(iter.nextElement());
            }
            return idList.toArray();
        }
        
        public boolean has(String name, Scriptable start) {
            return super.has(name, start) || getAttribute(name) != null;
        }
        
        public Object get(String name, Scriptable start) {
            Object result;
            // Filter out JavaBean properties. We only want methods of the underlying object.
            if (this.propNames.contains(name)) {
                result = NOT_FOUND;
            } else {
                result = super.get(name, start);
            }
            if (result == NOT_FOUND) {
                result = getAttribute(name);
                if (result != null) {
                    result = org.mozilla.javascript.Context.javaToJS(result, start);
                } else {
                    result = NOT_FOUND;
                }
            }
            return result;
        }
    }

    
    public Scriptable jsGet_log() {
        return currentCall.getLog();
    }

    public Scriptable jsGet_context() {
        return currentCall.getContext();
    }

    /**
     * Get Sitemap parameters
     *
     * @return a <code>Scriptable</code> value whose properties represent
     * the Sitemap parameters from <map:call>
     */
    public Scriptable jsGet_parameters() {
        return getParameters();
    }

    public Scriptable getParameters() {
        return currentCall.getParameters();
    }

    void setParameters(Scriptable value) {
        currentCall.setParameters(value);
    }
    
    /**
     *  Converts a JavaScript object to a HashMap
     */
    public Map jsFunction_jsobjectToMap(Scriptable jsobject) {
        return ConversionHelper.jsobjectToMap(jsobject);
    }

    // Make everything available to JavaScript objects implemented in Java:

    /**
     * Get the current context
     * @return The context
     */
    public WebContext getWebContext() {
        return currentCall.webctx;
    }

    private Logger getLogger() {
        return currentCall.logger;
    }

    private FOM_JavaScriptInterpreter getInterpreter() {
        return currentCall.interpreter;
    }

    /**
     * Required by FOM_WebContinuation. This way we do not make whole Interpreter public
     * @return interpreter Id associated with this FOM.
     */
    public String getInterpreterId() {
        return getInterpreter().getInterpreterID();
    }

    /**
     * Perform the behavior of <map:call continuation="blah">
     * This can be used in cases where the continuation id is not encoded
     * in the request in a form convenient to access in the sitemap.
     * Your script can extract the id from the request and then call
     * this method to process it as normal.
     * @param kontId The continuation id
     * @param parameters Any parameters you want to pass to the continuation (may be null)
     */
    public void handleContinuation(String kontId, Scriptable parameters)
        throws Exception {
        List list = null;
        if (parameters == null || parameters == Undefined.instance) {
            parameters = getParameters();
        }
        Object[] ids = parameters.getIds();
        list = new ArrayList();
        for (int i = 0; i < ids.length; i++) {
            String name = ids[i].toString();
            Argument arg = new Argument(name,
                                        org.mozilla.javascript.Context.toString(getProperty(parameters, name)));
            list.add(arg);
        }
        getInterpreter().handleContinuation(kontId, list, this.currentCall.webctx);
    }

    /**
     * Return this continuation if it is valid, or first valid parent
     */
    private FOM_WebContinuation findValidParent(FOM_WebContinuation wk) {
        if (wk != null) {
            WebContinuation wc = wk.getWebContinuation();
            while (wc != null && wc.disposed()) {
                wc = wc.getParentContinuation();
            }
            if (wc != null) {
                return new FOM_WebContinuation(wc);
            }
        }

        return null;
    }

    /**
     * Create a Bookmark WebContinuation from a JS Continuation with the last
     * continuation of sendPageAndWait as its parent.
     * PageLocal variables will be shared with the continuation of
     * the next call to sendPageAndWait().
     * @param k The JS continuation
     * @param ttl Lifetime for this continuation (zero means no limit)
     */
    public FOM_WebContinuation jsFunction_makeWebContinuation(Object k,
                                                              Object ttl)
        throws Exception {
        double d = org.mozilla.javascript.Context.toNumber(ttl);
        FOM_WebContinuation result =
            makeWebContinuation((Continuation)ConversionHelper.jsobjectToObject(k),
                                findValidParent(jsGet_continuation()),
                                (int)d);
        result.setPageLocal(pageLocal.getDelegate());
        currentCall.setLastContinuation(result);
        return result;
    }

    /**
     * Create a Web Continuation from a JS Continuation
     * @param k The JS continuation (may be null - null will be returned in that case)
     * @param parent The parent of this continuation (may be null)
     * @param timeToLive Lifetime for this continuation (zero means no limit)
     */
    public FOM_WebContinuation makeWebContinuation(Continuation k,
                                                   FOM_WebContinuation parent,
                                                   int timeToLive)
        throws Exception {
        if (k == null) {
            return null;
        }
        WebContinuation wk;
        ContinuationsManager contMgr;
        contMgr = Factory.getContinuationsManager();
        wk = contMgr.createWebContinuation(ConversionHelper.jsobjectToObject(k),
                                           (parent == null ? null : parent.getWebContinuation()),
                                           timeToLive,
                                           getInterpreter().getInterpreterID(),
                                           null,
                                           currentCall.getWebContext());
        FOM_WebContinuation result = new FOM_WebContinuation(wk);
        result.setParentScope(getParentScope());
        result.setPrototype(getClassPrototype(getParentScope(),
                                              result.getClassName()));
        return result;
    }
}
