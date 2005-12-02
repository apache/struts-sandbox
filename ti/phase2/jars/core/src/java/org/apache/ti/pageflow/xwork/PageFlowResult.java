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
package org.apache.ti.pageflow.xwork;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

import org.apache.ti.pageflow.*;
import org.apache.ti.pageflow.handler.ForwardRedirectHandler;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.internal.FileUtils;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class PageFlowResult
        implements Result {
    private static final Logger _log = Logger.getInstance(PageFlowResult.class);
    private static final Map /*< String, Class >*/ PRIMITIVE_TYPES = new HashMap /*< String, Class >*/();

    static {
        PRIMITIVE_TYPES.put("boolean", boolean.class);
        PRIMITIVE_TYPES.put("byte", byte.class);
        PRIMITIVE_TYPES.put("char", char.class);
        PRIMITIVE_TYPES.put("double", double.class);
        PRIMITIVE_TYPES.put("float", float.class);
        PRIMITIVE_TYPES.put("int", int.class);
        PRIMITIVE_TYPES.put("long", long.class);
        PRIMITIVE_TYPES.put("short", short.class);
    }

    private String _name;
    private String _location;
    private boolean _isReturnToPage = false;
    private boolean _isReturnToAction = false;
    private String _outputFormBeanType;
    private String _outputFormBeanMember;
    private boolean _hasExplicitRedirectValue = false;
    private Map /*<String, ActionOutput>*/ _actionOutputDeclarations;
    private boolean _restoreQueryString;
    private boolean _redirect = false;
    private boolean _externalRedirect = false;
    private boolean _inheritedPath = false;

    public void execute(ActionInvocation invocation) throws Exception {
        PageFlowActionContext actionContext = (PageFlowActionContext) invocation.getInvocationContext();
        Forward fwd = actionContext.getForward();
        assert fwd != null : "no forward found in context for Result \"" + getName() + '"';

        if (!preprocess(fwd, actionContext)) {
            initFrom(fwd, actionContext, true);
            applyForward(fwd, actionContext);
            finishExecution(fwd, actionContext);
        }
    }

    protected boolean preprocess(Forward fwd, PageFlowActionContext actionContext) {
        return false;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        _location = location;
    }

    public boolean isReturnToPage() {
        return _isReturnToPage;
    }

    public void setReturnToPage(boolean returnToPage) {
        _isReturnToPage = returnToPage;
    }

    public boolean isReturnToAction() {
        return _isReturnToAction;
    }

    public void setReturnToAction(boolean returnToAction) {
        _isReturnToAction = returnToAction;
    }

    public String getOutputFormBeanType() {
        return _outputFormBeanType;
    }

    public void setOutputFormBeanType(String outputFormBeanType) {
        _outputFormBeanType = outputFormBeanType;
    }

    public String getOutputFormBeanMember() {
        return _outputFormBeanMember;
    }

    public void setOutputFormBeanMember(String outputFormBeanMember) {
        _outputFormBeanMember = outputFormBeanMember;
    }

    public boolean hasExplicitRedirectValue() {
        return _hasExplicitRedirectValue;
    }

    public void setHasExplicitRedirectValue(boolean hasExplicitRedirectValue) {
        _hasExplicitRedirectValue = hasExplicitRedirectValue;
    }

    /**
     * Tell whether this forward will restore the original query string on the page restored when a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction},
     * or {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction ti.NavigateTo.previousAction}
     * </code> is used.
     */
    public boolean isRestoreQueryString() {
        return _restoreQueryString;
    }

    /**
     * Set whether this forward will restore the original query string query string on the page restored when a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction},
     * or {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction ti.NavigateTo.previousAction}
     * </code> is used.
     */
    public void setRestoreQueryString(boolean restoreQueryString) {
        _restoreQueryString = restoreQueryString;
    }

    public boolean isRedirect() {
        return _redirect;
    }

    public void setRedirect(boolean redirect) {
        _redirect = redirect;
    }

    /**
     * Tell whether this is a redirect to a URI outside of the current web application.
     */
    public boolean isExternalRedirect() {
        return _externalRedirect;
    }

    /**
     * Specify that this is a redirect to a URI outside of the current web application.
     */
    public void setExternalRedirect(boolean externalRedirect) {
        _externalRedirect = externalRedirect;
    }

    private static class ActionOutput
            implements Serializable {
        private static final long serialVersionUID = 1;
        private String _actionOutputName;
        private String _type;
        private boolean _isNullable;

        public ActionOutput(String name, String type, boolean isNullable) {
            _actionOutputName = name;
            _type = type;
            _isNullable = isNullable;
        }

        public String getName() {
            return _actionOutputName;
        }

        public String getType() {
            return _type;
        }

        public boolean getNullable() {
            return _isNullable;
        }
    }

    protected void setActionOutput(int n, String concatenatedVals) {
        String[] vals = concatenatedVals.split("\\|");
        assert vals.length == 3 : vals.length;

        String name = vals[2];
        String type = vals[0];
        boolean isNullable = Boolean.valueOf(vals[1]).booleanValue();
        _actionOutputDeclarations.put(name, new ActionOutput(name, type, isNullable));
    }

    public void setActionOutput0(String str) {
        _actionOutputDeclarations = new HashMap();
        setActionOutput(0, str);
    }

    public void setActionOutput1(String str) {
        setActionOutput(1, str);
    }

    public void setActionOutput2(String str) {
        setActionOutput(2, str);
    }

    public void setActionOutput3(String str) {
        setActionOutput(3, str);
    }

    public void setActionOutput4(String str) {
        setActionOutput(4, str);
    }

    public void setActionOutput5(String str) {
        setActionOutput(5, str);
    }

    public void setActionOutput6(String str) {
        setActionOutput(6, str);
    }

    public void setActionOutput7(String str) {
        setActionOutput(7, str);
    }

    public void setActionOutput8(String str) {
        setActionOutput(8, str);
    }

    public void setActionOutput9(String str) {
        setActionOutput(9, str);
    }

    public void setActionOutput10(String str) {
        setActionOutput(10, str);
    }

    public void setActionOutput11(String str) {
        setActionOutput(11, str);
    }

    public void setActionOutput12(String str) {
        setActionOutput(12, str);
    }

    public void setActionOutput13(String str) {
        setActionOutput(13, str);
    }

    public void setActionOutput14(String str) {
        setActionOutput(14, str);
    }

    public void setActionOutput15(String str) {
        setActionOutput(15, str);
    }

    public void setActionOutput16(String str) {
        setActionOutput(16, str);
    }

    public void setActionOutput17(String str) {
        setActionOutput(17, str);
    }

    public void setActionOutput18(String str) {
        setActionOutput(18, str);
    }

    public void setActionOutput19(String str) {
        setActionOutput(19, str);
    }

    /**
     * Tell whether the path is inherited from a path in a base class.
     *
     * @return <code>true</code> if the path is inherited from a path in a base class.
     */
    public boolean isInheritedPath() {
        return _inheritedPath;
    }

    public void setInheritedPath(boolean inheritedPath) {
        _inheritedPath = inheritedPath;
    }

    protected void initFrom(Forward fwd, PageFlowActionContext actionContext, boolean checkForErrors) {
        // If there was a path specified on the Forward (programmatically), use that.
        // TODO: enforce an annotation attribute that allows this; otherwise, throw.
        if (fwd.getPath() != null) {
            setLocation(fwd.getPath());
        }

        // Add query params to the path.
        if (fwd.getQueryString() != null) {
            setLocation(getLocation() + fwd.getQueryString());
        }

        Class returnFormClass = null;

        if (_outputFormBeanType != null) {
            try {
                returnFormClass = Class.forName(_outputFormBeanType);
            } catch (ClassNotFoundException e) {
                // This should never happen -- the JPF compiler ensures that it's a valid class.
                assert false : e;
            }
        }

        FlowController flowController = actionContext.getFlowController();

        if (_outputFormBeanMember != null) {
            try {
                assert flowController != null; // should be set in initialize()

                Field field = flowController.getClass().getDeclaredField(_outputFormBeanMember);
                returnFormClass = field.getType();

                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }

                Object form = field.get(flowController);

                if (form != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("using member " + _outputFormBeanMember + " for forward " + getName());
                    }

                    fwd.addOutputForm(form);
                } else {
                    if (_log.isInfoEnabled()) {
                        _log.info("returnFormMember " + _outputFormBeanMember + " was null.");
                    }
                }
            } catch (NoSuchFieldException e) {
                assert false : "could not find field " + _outputFormBeanMember; // compiler should catch this
            } catch (IllegalAccessException e) {
                assert false; // should not get here -- field is accessible.
            }
        }

        if (checkForErrors) {
            checkOutputFormBeans(fwd, returnFormClass, flowController);
            checkActionOutputs(fwd, actionContext);

            //
            // Throw an exception if this is a redirect, and if there was an output form or an action output added.
            // Output forms and action outputs are carried in the request, and will be lost on redirects.
            //
            if (isRedirect()) {
                if ((_actionOutputDeclarations != null) && !_actionOutputDeclarations.isEmpty()) {
                    FlowControllerException ex = new IllegalActionOutputException(_name, flowController,
                                                                                  (String) _actionOutputDeclarations.keySet()
                                                                                                                    .iterator()
                                                                                                                    .next());
                    InternalUtils.throwPageFlowException(ex);
                }

                List outputForms = fwd.getOutputFormBeans();

                if ((outputForms != null) && !outputForms.isEmpty()) {
                    FlowControllerException ex = new IllegalRedirectOutputFormException(_name, flowController,
                                                                                        outputForms.get(0).getClass().getName());
                    InternalUtils.throwPageFlowException(ex);
                }
            }
        }
    }

    private void checkOutputFormBeans(Forward fwd, Class returnFormClass, FlowController flowController) {
        //
        // Make sure that if there's currently an output form, that it confirms to the return-form-type.
        //
        List outputForms = fwd.getOutputFormBeans();

        if ((returnFormClass != null) && (outputForms != null) && (outputForms.size() > 0)) {
            Object outputForm = outputForms.get(0);

            if (!returnFormClass.isInstance(outputForm)) {
                FlowControllerException ex = new IllegalOutputFormTypeException(getName(), flowController,
                                                                                outputForm.getClass().getName(),
                                                                                returnFormClass.getName());
                InternalUtils.throwPageFlowException(ex);
            }
        }
    }

    /**
     * Make sure required action outputs are present, and are of the right type (only make the latter check when not
     * in production mode
     */
    private void checkActionOutputs(Forward fwd, PageFlowActionContext actionContext) {
        if (_actionOutputDeclarations == null) {
            return;
        }

        boolean isInProductionMode = AdapterManager.getContainerAdapter().isInProductionMode();
        Map fwdActionOutputs = fwd.getActionOutputs();

        for (Iterator i = _actionOutputDeclarations.values().iterator(); i.hasNext();) {
            ActionOutput actionOutput = (ActionOutput) i.next();

            if (!actionOutput.getNullable() &&
                    ((fwdActionOutputs == null) || (fwdActionOutputs.get(actionOutput.getName()) == null))) {
                FlowController flowController = actionContext.getFlowController();
                FlowControllerException ex = new MissingActionOutputException(flowController, actionOutput.getName(), getName());
                InternalUtils.throwPageFlowException(ex);
            }

            //
            // If we're *not* in production mode, do some (expensive) checks to ensure that the types for the
            // action outputs match their declared types.
            //
            if (!isInProductionMode && (fwdActionOutputs != null)) {
                Object actualActionOutput = fwdActionOutputs.get(actionOutput.getName());

                if (actualActionOutput != null) {
                    String expectedTypeName = actionOutput.getType();
                    int expectedArrayDims = 0;

                    while (expectedTypeName.endsWith("[]")) {
                        ++expectedArrayDims;
                        expectedTypeName = expectedTypeName.substring(0, expectedTypeName.length() - 2);
                    }

                    Class expectedType = (Class) PRIMITIVE_TYPES.get(expectedTypeName);

                    if (expectedType == null) {
                        try {
                            expectedType = Class.forName(expectedTypeName);
                        } catch (ClassNotFoundException e) {
                            _log.error("Could not load expected action output type " + expectedTypeName + " for action output '" +
                                       actionOutput.getName() + "' on forward '" + getName() + "'; skipping type check.");

                            continue;
                        }
                    }

                    Class actualType = actualActionOutput.getClass();
                    int actualArrayDims = 0;
                    InternalStringBuilder arraySuffix = new InternalStringBuilder();

                    while (actualType.isArray() && (actualArrayDims <= expectedArrayDims)) {
                        ++actualArrayDims;
                        arraySuffix.append("[]");
                        actualType = actualType.getComponentType();
                    }

                    if ((actualArrayDims != expectedArrayDims) || !expectedType.isAssignableFrom(actualType)) {
                        FlowController fc = actionContext.getFlowController();
                        FlowControllerException ex = new MismatchedActionOutputException(fc, actionOutput.getName(), getName(),
                                                                                         expectedTypeName,
                                                                                         actualType.getName() + arraySuffix);
                        InternalUtils.throwPageFlowException(ex);
                    }
                }
            }
        }
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    // TODO: re-add an alternative way of displaying pages (rather than server forward)
    protected boolean processPageForward(String pagePath)
            throws PageFlowException {
        return false;
    }

    protected void doForward(String path) throws PageFlowException {
        if (!processPageForward(path)) {
            ForwardRedirectHandler fwdRedirectHandler = Handlers.get().getForwardRedirectHandler();
            fwdRedirectHandler.forward(path);
        }
    }

    protected void applyForward(Forward fwd, PageFlowActionContext actionContext) {
        //
        // Set ActionForms specified in the forward.  Note that this overwrites any forms restored
        // during return-to="page".
        //
        PageFlowUtils.setOutputForms(fwd, true);
        InternalUtils.addActionOutputs(fwd.getActionOutputs(), true);
    }

    /**
     * This override of the base method ensures that absolute URIs don't get the context
     * path prepended, and handles forwards to special things like return-to="currentPage".
     */
    protected void finishExecution(Forward fwd, PageFlowActionContext actionContext)
            throws PageFlowException {
        Handlers handlers = Handlers.get();
        ForwardRedirectHandler fwdRedirectHandler = handlers.getForwardRedirectHandler();
        FlowController fc = actionContext.getFlowController();

        // Register this module as the one that's handling the action.
        assert fc != null;
        InternalUtils.setForwardingModule(fc.getNamespace());

        //
        // Save info on this forward for return-to="currentPage" or return-to="previousPage".  But, don't save
        // the info if the current forward is a return-to="currentPage" -- we don't want this to turn into
        // the page that's seen for *both* return-to="currentPage" and return-to="previousPage".
        //
        if (shouldSavePreviousPageInfo()) {
            Object formBean = actionContext.getFormBean();
            fc.savePreviousPageInfo(this, fwd, formBean);
        }

        // Try to get a resolved path from the forward; otherwise, just use the configured path.
        String path = getLocation();

        if (path == null) {
            path = getLocation();
        }

        boolean startsWithSlash = (path.length() > 0) && (path.charAt(0) == '/');

        //
        // If the URI is absolute (e.g., starts with "http:"), do a redirect to it no matter what.
        //
        if (FileUtils.isAbsoluteURI(path)) {
            fwdRedirectHandler.redirect(addScopeParams(path));
        } else if (isRedirect()) {
            String redirectURI = path;

            if (!isExternalRedirect() && startsWithSlash) {
                redirectURI = actionContext.getRequestContextPath() + path;
            }

            fwdRedirectHandler.redirect(addScopeParams(redirectURI));
        } else {
            String fwdURI = path;

            if (!startsWithSlash) {
                //
                // First, see if the current module is a Shared Flow module.  If so, unless this is a forward to
                // another action in the shared flow, we need to translate the local path so it makes sense (strip
                // off the shared flow module prefix "/-" and replace it with "/").
                //
                ModuleConfig mc = actionContext.getModuleConfig();

                // TODO: ACTION_EXTENSION can no longer be a constant -- we support arbitrary Servlet mappings
                if (mc.isSharedFlow() && !fwdURI.endsWith(PageFlowConstants.ACTION_EXTENSION) &&
                        fwdURI.startsWith(InternalConstants.SHARED_FLOW_MODULE_PREFIX)) {
                    fwdURI = '/' + fwdURI.substring(InternalConstants.SHARED_FLOW_MODULE_PREFIX_LEN);
                }
            }

            doForward(fwdURI);
        }
    }

    private static String addScopeParams(String path) {
        // TODO: re-add the previous functionality
        return path;
    }

    protected abstract boolean shouldSavePreviousPageInfo();

    /**
     * Tell whether this result was for a straight path, rather than some sort of symbolic navigation (e.g., a
     * navigateTo=ti.NavigateTo.currentPage).
     */
    public abstract boolean isPath();
}
