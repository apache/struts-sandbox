/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.test.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.convert.Converter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.validator.Validator;

/**
 * <p>Mock implementation of <code>Application</code>.</p>
 *
 * $Id$
 */

public class MockApplication extends Application {


    // ------------------------------------------------------------ Constructors

    /**
     * <p>Construct a default instance.</p>
     */
    public MockApplication() {

        setActionListener(new MockActionListener());
        components = new HashMap();
        converters = new HashMap();
        setDefaultLocale(Locale.getDefault());
        setNavigationHandler(new MockNavigationHandler());
        setPropertyResolver(new MockPropertyResolver());
        setStateManager(new MockStateManager());
        setSupportedLocales(new ArrayList());
        validators = new HashMap();
        setVariableResolver(new MockVariableResolver());
        setViewHandler(new MockViewHandler());

    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    private ActionListener actionListener = null;
    private Map components = null;
    private Map converters = null;
    private Locale defaultLocale = null;
    private String defaultRenderKitId = null;
    private String messageBundle = null;
    private NavigationHandler navigationHandler = null;
    private PropertyResolver propertyResolver = null;
    private StateManager stateManager = null;
    private Collection supportedLocales = null;
    private Map validators = null;
    private VariableResolver variableResolver = null;
    private ViewHandler viewHandler = null;


    // ----------------------------------------------------- Application Methods


    public ActionListener getActionListener() {

        return this.actionListener;

    }


    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }


    public Locale getDefaultLocale(){

        return this.defaultLocale;

    }

    public void setDefaultLocale(Locale defaultLocale) {

        this.defaultLocale = defaultLocale;

    }

    public String getDefaultRenderKitId() { 

        return this.defaultRenderKitId;

    }

    public void setDefaultRenderKitId(String defaultRenderKitId) {

        this.defaultRenderKitId = defaultRenderKitId;

    }


    public String getMessageBundle() {

        return this.messageBundle;

    }


    public void setMessageBundle(String messageBundle) {

        this.messageBundle = messageBundle;

    }


    public NavigationHandler getNavigationHandler() {

        return this.navigationHandler;

    }


    public void setNavigationHandler(NavigationHandler navigationHandler) {

        this.navigationHandler = navigationHandler;

    }


    public PropertyResolver getPropertyResolver() {

        return this.propertyResolver;

    }


    public void setPropertyResolver(PropertyResolver propertyResolver) {

        this.propertyResolver = propertyResolver;

    }


    public StateManager getStateManager() {

        return this.stateManager;

    }


    public void setStateManager(StateManager stateManager) {

        this.stateManager = stateManager;

    }


    public Iterator getSupportedLocales() {

        return this.supportedLocales.iterator();

    }


    public void setSupportedLocales(Collection newLocales) {

        this.supportedLocales = supportedLocales;

    }


    public VariableResolver getVariableResolver() {

        return this.variableResolver;
    }


    public void setVariableResolver(VariableResolver variableResolver) {

        this.variableResolver = variableResolver;

    }


    public ViewHandler getViewHandler() {

        return this.viewHandler;

    }


    public void setViewHandler(ViewHandler viewHandler) {

        this.viewHandler = viewHandler;

    }


    public void addComponent(String componentType, String componentClass) {

        components.put(componentType, componentClass);

    }


    public UIComponent createComponent(String componentType) {

        String componentClass = (String) components.get(componentType);
        try {
            Class clazz = Class.forName(componentClass);
            return ((UIComponent) clazz.newInstance());
        } catch (Exception e) {
            throw new FacesException(e);
        }

    }


    public UIComponent createComponent(ValueBinding componentBinding,
                                       FacesContext context,
                                       String componentType)
        throws FacesException {

	throw new FacesException(new UnsupportedOperationException());

    }


    public Iterator getComponentTypes() {

        return (components.keySet().iterator());

    }


    public void addConverter(String converterId, String converterClass) {

        converters.put(converterId, converterClass);

    }


    public void addConverter(Class targetClass, String converterClass) {

        throw new UnsupportedOperationException();

    }


    public Converter createConverter(String converterId) {

        String converterClass = (String) converters.get(converterId);
        try {
            Class clazz = Class.forName(converterClass);
            return ((Converter) clazz.newInstance());
        } catch (Exception e) {
            throw new FacesException(e);
        }

    }


    public Converter createConverter(Class targetClass) {

        throw new UnsupportedOperationException();

    }


    public Iterator getConverterIds() {

        return (converters.keySet().iterator());

    }


    public Iterator getConverterTypes() {

        throw new UnsupportedOperationException();

    }
    

    public MethodBinding createMethodBinding(String ref, Class params[]) {

        if (ref == null) {
            throw new NullPointerException();
        } else {
            return (new MockMethodBinding(this, ref, params));
        }

    }


    public ValueBinding createValueBinding(String ref) {

        if (ref == null) {
            throw new NullPointerException();
        } else {
            return (new MockValueBinding(this, ref));
        }

    }


    public void addValidator(String validatorId, String validatorClass) {

        validators.put(validatorId, validatorClass);

    }


    public Validator createValidator(String validatorId) {

        String validatorClass = (String) validators.get(validatorId);
        try {
            Class clazz = Class.forName(validatorClass);
            return ((Validator) clazz.newInstance());
        } catch (Exception e) {
            throw new FacesException(e);
        }

    }


    public Iterator getValidatorIds() {
        return (validators.keySet().iterator());
    }


}
