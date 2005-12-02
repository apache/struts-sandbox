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
package org.apache.ti.pageflow;

import org.apache.ti.core.factory.Factory;
import org.apache.ti.core.factory.FactoryUtils;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.ModuleRegistrationHandler;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.PageFlowConfig;
import org.apache.ti.util.config.bean.PageFlowFactoriesConfig;
import org.apache.ti.util.config.bean.PageFlowFactoryConfig;
import org.apache.ti.util.config.bean.SharedFlowRefConfig;
import org.apache.ti.util.logging.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory for creating {@link FlowController}s - user {@link PageFlowController}s and {@link SharedFlowController}s.
 */
public class FlowControllerFactory
        extends Factory {
    private static final Logger _log = Logger.getInstance(FlowControllerFactory.class);
    private static final String CONTEXT_ATTR = InternalConstants.ATTR_PREFIX + "fcFactory";

    protected FlowControllerFactory() {
    }

    protected void onCreate() {
    }

    public static void init(Map appScope) {
        PageFlowFactoriesConfig factoriesBean = ConfigUtil.getConfig().getPageFlowFactories();
        FlowControllerFactory factory = null;

        if (factoriesBean != null) {
            PageFlowFactoryConfig fcFactoryBean = factoriesBean.getPageFlowFactory();
            factory = (FlowControllerFactory) FactoryUtils.getFactory(fcFactoryBean, FlowControllerFactory.class);
        }

        if (factory == null) {
            factory = new FlowControllerFactory();
        }

        factory.reinit();

        appScope.put(CONTEXT_ATTR, factory);
    }

    /**
     * Called to reinitialize this instance, most importantly after it has been serialized/deserialized.
     */
    protected void reinit() {
        super.reinit();
    }

    /**
     * Get a FlowControllerFactory.
     *
     * @return a FlowControllerFactory for the given application.  It may or may not be a cached instance.
     */
    public static FlowControllerFactory get() {
        Map appScope = PageFlowActionContext.get().getApplication();
        FlowControllerFactory factory = (FlowControllerFactory) appScope.get(CONTEXT_ATTR);
        assert factory != null : FlowControllerFactory.class.getName() + " was not found in application attribute " +
        CONTEXT_ATTR;
        factory.reinit();

        return factory;
    }

    /**
     * Get the page flow instance that should be associated with the given request.  If it doesn't exist, create it.
     * If one is created, the page flow stack (for nesting) will be cleared or pushed, and the new instance will be
     * stored as the current page flow.
     *
     * @return the {@link PageFlowController} for the request, or <code>null</code> if none was found.
     */
    public PageFlowController getPageFlowForRequest() throws InstantiationException, IllegalAccessException {
        PageFlowController cur = PageFlowUtils.getCurrentPageFlow();

        //
        // Reinitialize transient data that may have been lost on session failover.
        //
        if (cur != null) {
            cur.reinitialize();
        }

        //
        // If there's no current PageFlow, or if the current PageFlowController has a namespace that
        // is incompatible with the current request URI, then create the appropriate PageFlowController.
        //
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        String namespace = actionContext.getNamespace();

        if ((namespace != null) && ((cur == null) || !cur.getNamespace().equals(namespace))) {
            try {
                String className = InternalUtils.getFlowControllerClassName(namespace);

                return (className != null) ? createPageFlow(className) : null;
            } catch (ClassNotFoundException e) {
                if (_log.isInfoEnabled()) {
                    _log.info("No page flow exists for namespace " + namespace);
                }

                return null;
            }
        }

        return cur;
    }

    /**
     * Create a page flow of the given type.  The page flow stack (for nesting) will be cleared or pushed, and the new
     * instance will be stored as the current page flow.
     *
     * @param pageFlowClassName the type name of the desired page flow.
     * @return the newly-created {@link PageFlowController}, or <code>null</code> if none was found.
     */
    public PageFlowController createPageFlow(String pageFlowClassName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class pageFlowClass = getFlowControllerClass(pageFlowClassName);

        return createPageFlow(pageFlowClass);
    }

    /**
     * Create a {@link PageFlowController} of the given type.  The PageFlowController stack (for
     * nesting) will be cleared or pushed, and the new instance will be stored as the current
     * PageFlowController.
     *
     * @param pageFlowClass the type of the desired PageFlowController.
     * @return the newly-created PageFlowController, or <code>null</code> if none was found.
     */
    public PageFlowController createPageFlow(Class pageFlowClass)
            throws InstantiationException, IllegalAccessException {
        if (!PageFlowController.class.isAssignableFrom(pageFlowClass)) {
            return null;
        }

        //
        // First check if this is a request for a "long lived" page flow.  If so, try
        // PageFlowUtils.getCurrentPageFlow again, with the longLived flag.
        //
        PageFlowController retVal = null;
        String namespace = InternalUtils.inferNamespaceFromClassName(pageFlowClass.getName());
        ModuleRegistrationHandler mrh = Handlers.get().getModuleRegistrationHandler();
        ModuleConfig mc = mrh.getModuleConfig(namespace);

        if (mc == null) {
            _log.error("Struts module " + namespace + " not found for " + pageFlowClass.getName() + "; cannot create page flow.");

            return null;
        }

        if (mc.isLongLivedFlow()) {
            retVal = PageFlowUtils.getLongLivedPageFlow(namespace);

            if (_log.isDebugEnabled()) {
                if (retVal != null) {
                    _log.debug("Using long lived PageFlowController of type " + pageFlowClass.getName());
                }
            }
        }

        //
        // First, see if this is a nested page flow that's already on the stack.  Unless "renesting" is explicitly
        // enabled, we don't want to allow another instance of this page flow to be nested.  This is a common
        // browser back-button problem:
        //    1) request nested page flow A
        //    2) request nested page flow B
        //    3) press back button, and execute an action on A.
        //
        // This logic does not deal with immediate self-nesting (A->A), which is taken care of in
        // PageFlowController.forwardTo().  Nested page flows can only self-nest by forwarding to the .jpf URI, not
        // indirectly by executing actions on themselves (think about it -- that would be a disaster).
        //
        boolean createdNew = false;
        boolean isNestable = mc.isNestedFlow();
        PageFlowStack pfStack = PageFlowStack.get(false);

        if (isNestable && (pfStack != null)) {
            PageFlowConfig options = ConfigUtil.getConfig().getPageFlowConfig();

            if ((options == null) || !options.isEnableSelfNesting()) {
                int lastIndexOfJpfClass = pfStack.lastIndexOf(pageFlowClass);

                if (lastIndexOfJpfClass != -1) {
                    retVal = pfStack.popUntil(lastIndexOfJpfClass);
                    retVal.persistInSession();

                    return retVal;
                }
            }
        }

        //
        // OK, if it's not an existing long lived page flow, and if this wasn't a nested page flow already on the
        // stack, then create a new instance.
        //
        if (retVal == null) {
            if (_log.isDebugEnabled()) {
                _log.debug("Creating PageFlowController of type " + pageFlowClass.getName());
            }

            retVal = (PageFlowController) getFlowControllerInstance(pageFlowClass);
            createdNew = true;
        }

        //
        // Store the previous PageFlowController on the nesting stack (if this one is nestable),
        // or destroy the nesting stack.
        //
        if (isNestable) {
            //
            // Call create() on the newly-created page flow.
            //
            if (createdNew) {
                retVal.create();
            }

            PageFlowController current = PageFlowUtils.getCurrentPageFlow();

            if (current != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Pushing PageFlowController " + current + " onto the nesting stack");
                }

                if (pfStack == null) {
                    pfStack = PageFlowStack.get(true);
                }

                pfStack.push(current);
            }

            retVal.reinitialize();
            retVal.persistInSession();
        } else {
            //
            // Going to a non-nested pageflow.  Blow away the pageflow stack.
            //
            if (pfStack != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Destroying the PageFlowController stack.");
                }

                //
                // Start popping page flows until 1) there are none left on the stack, or 2) we find
                // one of the type we're returning.  If (2), we'll use that one (this means that executing
                // an action on a nesting page flow while in a nested one will not destroy the nesting
                // page flow only to create a new instance of it).
                //
                PageFlowController onStackAlready = pfStack.popUntil(retVal.getClass());

                if (onStackAlready != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found a page flow of type " + retVal.getClass() + " in the stack; " +
                                   "using that instance and stopping destruction of the nesting stack.");
                    }

                    retVal = onStackAlready;
                    retVal.persistInSession();
                } else {
                    //
                    // We're actually using the newly-created page flow, so call create() on it.
                    // Note that we make the call to persistInSession *before* create, so the previous flow's
                    // onDestroy() gets called before the new one's onCreate().
                    //
                    retVal.reinitialize();
                    retVal.persistInSession();
                    retVal.create();
                }
            } else {
                //
                // We're actually using the newly-created page flow, so call create() on it (*after* persisting
                // in the session so the previous page flow's onDestroy() gets called before the new one's
                // onCreate()).
                //
                retVal.reinitialize();
                retVal.persistInSession();

                if (createdNew) {
                    retVal.create();
                }
            }
        }

        return retVal;
    }

    /**
     * Create a {@link SharedFlowController} of the given type.
     *
     * @param sharedFlowClassName the type name of the desired SharedFlowController.
     * @return the newly-created SharedFlowController, or <code>null</code> if none was found.
     */
    public SharedFlowController createSharedFlow(String sharedFlowClassName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class sharedFlowClass = getFlowControllerClass(sharedFlowClassName);

        return createSharedFlow(sharedFlowClass);
    }

    /**
     * Create a {@link SharedFlowController} of the given type.
     *
     * @param sharedFlowClass the type of the desired SharedFlowController.
     * @return the newly-created SharedFlowController, or <code>null</code> if none was found.
     */
    public SharedFlowController createSharedFlow(Class sharedFlowClass)
            throws InstantiationException, IllegalAccessException {
        assert SharedFlowController.class.isAssignableFrom(sharedFlowClass) : sharedFlowClass.getName();

        if (_log.isDebugEnabled()) {
            _log.debug("Creating SharedFlowController of type " + sharedFlowClass.getName());
        }

        SharedFlowController retVal = (SharedFlowController) getFlowControllerInstance(sharedFlowClass);
        retVal.create();

        if (_log.isDebugEnabled()) {
            _log.debug("Storing " + retVal + " in the session...");
        }

        retVal.persistInSession();

        return retVal;
    }

    /**
     * Get the map of shared flows for the given request.  The map is derived from the shared flows
     * that are declared (through the <code>sharedFlowRefs</code> attribute of
     * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}) in the page flow for the request.
     *
     * @return a Map of shared-flow-name (String) to {@link SharedFlowController}.
     * @throws ClassNotFoundException if a declared shared flow class could not be found.
     * @throws InstantiationException if a declared shared flow class could not be instantiated.
     * @throws IllegalAccessException if a declared shared flow class was not accessible.
     */
    public Map /*< String, SharedFlowController >*/ getSharedFlowsForRequest()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        String namespace = actionContext.getNamespace();
        LinkedHashMap /*< String, SharedFlowController >*/ sharedFlows = getDefaultSharedFlows();

        if (namespace == null) {
            return null;
        }

        ModuleRegistrationHandler mrh = Handlers.get().getModuleRegistrationHandler();
        ModuleConfig mc = mrh.getModuleConfig(namespace);

        if (mc != null) {
            Map /*< String, String >*/ sharedFlowTypes = mc.getSharedFlowTypes();

            if ((sharedFlowTypes != null) && (sharedFlowTypes.size() > 0)) {
                if (sharedFlows == null) {
                    sharedFlows = new LinkedHashMap /*< String, SharedFlowController >*/();
                }

                for (Iterator /*<Map.Entry>*/ i = sharedFlowTypes.entrySet().iterator(); i.hasNext();) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String name = (String) entry.getKey();
                    String type = (String) entry.getValue();
                    addSharedFlow(name, type, sharedFlows);
                }

                return sharedFlows;
            }
        }

        return sharedFlows;
    }

    LinkedHashMap /*< String, SharedFlowController >*/ getDefaultSharedFlows()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        SharedFlowRefConfig[] defaultRefs = ConfigUtil.getConfig().getSharedFlowRefs();

        if (defaultRefs != null) {
            if (defaultRefs.length > 0) {
                LinkedHashMap /*< String, SharedFlowController >*/ sharedFlows = new LinkedHashMap();

                for (int i = 0; i < defaultRefs.length; i++) {
                    SharedFlowRefConfig ref = defaultRefs[i];

                    if (_log.isInfoEnabled()) {
                        _log.info("Shared flow of type " + ref.getType() + " is a default shared flow reference " + "with name " +
                                  ref.getName());
                    }

                    addSharedFlow(ref.getName(), ref.getType(), sharedFlows);
                }

                return sharedFlows;
            }
        }

        return null;
    }

    private void addSharedFlow(String name, String type, LinkedHashMap sharedFlows)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        SharedFlowController sf = PageFlowUtils.getSharedFlow(type);

        //
        // Reinitialize transient data that may have been lost on session failover.
        //
        if (sf != null) {
            sf.reinitialize();
        } else {
            sf = createSharedFlow(type);
        }

        sharedFlows.put(name, sf);
    }

    /**
     * Get a FlowController class.  By default, this loads the class using the thread context class loader.
     *
     * @param className the name of the {@link FlowController} class to load.
     * @return the loaded {@link FlowController} class.
     * @throws ClassNotFoundException if the requested class could not be found.
     */
    public Class getFlowControllerClass(String className)
            throws ClassNotFoundException {
        return Handlers.get().getReloadableClassHandler().loadClass(className);
    }

    /**
     * Get a FlowController instance, given a FlowController class.
     *
     * @param flowControllerClass the Class, which must be assignable to {@link FlowController}.
     * @return a new FlowController instance.
     */
    public FlowController getFlowControllerInstance(Class flowControllerClass)
            throws InstantiationException, IllegalAccessException {
        assert FlowController.class.isAssignableFrom(flowControllerClass) : "Class " + flowControllerClass.getName() +
        " does not extend " + FlowController.class.getName();

        return (FlowController) flowControllerClass.newInstance();
    }
}
