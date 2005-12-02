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
package org.apache.ti.pageflow.faces.internal;

import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Internal class used in JSF/Page Flow integration.  This is the main hook for overriding base JSF behavior.
 *
 * @see org.apache.ti.pageflow.faces.PageFlowApplicationFactory
 */
public class PageFlowApplication
        extends Application {

    private static final String BACKING_BINDING_START = "#{" + InternalConstants.BACKING_CLASS_IMPLICIT_OBJECT + '.';

    private static final Logger _log = Logger.getInstance(PageFlowApplication.class);

    private Application _delegate;


    public PageFlowApplication(Application delegate) {
        if (_log.isDebugEnabled()) {
            _log.debug("Adapting Application" + delegate);
        }

        _delegate = delegate;
    }

    public ActionListener getActionListener() {
        return _delegate.getActionListener();
    }

    public void setActionListener(ActionListener listener) {
        _delegate.setActionListener(new PageFlowActionListener(listener));
    }

    public Locale getDefaultLocale() {
        return _delegate.getDefaultLocale();
    }

    public void setDefaultLocale(Locale locale) {
        _delegate.setDefaultLocale(locale);
    }

    public String getDefaultRenderKitId() {
        return _delegate.getDefaultRenderKitId();
    }

    public void setDefaultRenderKitId(String renderKitId) {
        _delegate.setDefaultRenderKitId(renderKitId);
    }

    public String getMessageBundle() {
        return _delegate.getMessageBundle();
    }

    public void setMessageBundle(String bundle) {
        _delegate.setMessageBundle(bundle);
    }

    public NavigationHandler getNavigationHandler() {
        return _delegate.getNavigationHandler();
    }

    public void setNavigationHandler(NavigationHandler handler) {
        _delegate.setNavigationHandler(new PageFlowNavigationHandler(handler));
    }

    public PropertyResolver getPropertyResolver() {
        return _delegate.getPropertyResolver();
    }

    public void setPropertyResolver(PropertyResolver resolver) {
        _delegate.setPropertyResolver(resolver);
    }

    public VariableResolver getVariableResolver() {
        return _delegate.getVariableResolver();
    }

    public void setVariableResolver(VariableResolver resolver) {
        _delegate.setVariableResolver(resolver);
    }

    public ViewHandler getViewHandler() {
        return _delegate.getViewHandler();
    }

    public void setViewHandler(ViewHandler handler) {
        _delegate.setViewHandler(handler instanceof PageFlowViewHandler ? handler : new PageFlowViewHandler(handler));
    }

    public StateManager getStateManager() {
        return _delegate.getStateManager();
    }

    public void setStateManager(StateManager manager) {
        _delegate.setStateManager(manager);
    }

    public void addComponent(String componentType, String componentClass) {
        _delegate.addComponent(componentType, componentClass);
    }

    public UIComponent createComponent(String componentType)
            throws FacesException {
        return _delegate.createComponent(componentType);
    }

    public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
            throws FacesException {
        return _delegate.createComponent(componentBinding, context, componentType);
    }

    public Iterator getComponentTypes() {
        return _delegate.getComponentTypes();
    }

    public void addConverter(String converterId, String converterClass) {
        _delegate.addConverter(converterId, converterClass);
    }

    public void addConverter(Class targetClass, String converterClass) {
        _delegate.addConverter(targetClass, converterClass);
    }

    public Converter createConverter(String converterId) {
        return _delegate.createConverter(converterId);
    }

    public Converter createConverter(Class targetClass) {
        return _delegate.createConverter(targetClass);
    }

    public Iterator getConverterIds() {
        return _delegate.getConverterIds();
    }

    public Iterator getConverterTypes() {
        return _delegate.getConverterTypes();
    }

    public MethodBinding createMethodBinding(String ref, Class params[])
            throws ReferenceSyntaxException {
        MethodBinding mb = _delegate.createMethodBinding(ref, params);

        if (ref.startsWith(BACKING_BINDING_START) && ref.charAt(ref.length() - 1) == '}') {
            String methodName = ref.substring(BACKING_BINDING_START.length(), ref.length() - 1);
            return new BackingClassMethodBinding(methodName, params, mb);
        } else {
            return mb;
        }
    }

    public Iterator getSupportedLocales() {
        return _delegate.getSupportedLocales();
    }

    public void setSupportedLocales(Collection locales) {
        _delegate.setSupportedLocales(locales);
    }

    public void addValidator(String validatorId, String validatorClass) {
        _delegate.addValidator(validatorId, validatorClass);
    }

    public Validator createValidator(String validatorId)
            throws FacesException {
        return _delegate.createValidator(validatorId);
    }

    public Iterator getValidatorIds() {
        return _delegate.getValidatorIds();
    }

    public ValueBinding createValueBinding(String ref)
            throws ReferenceSyntaxException {
        return _delegate.createValueBinding(ref);
    }
}
