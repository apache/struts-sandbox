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
package org.apache.ti.compiler.internal.model;

import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Represents an action in an XWork-based application.
 */
public class XWorkActionModel
        extends AbstractResultContainer
        implements XWorkResultContainer, XWorkExceptionHandlerContainer, JpfLanguageConstants {

    public static final String DEFAULT_FORM_SCOPE = "request";


    private ArrayList _exceptionCatches = new ArrayList();
    private String _attribute;
    private String _className;
    private String _validationErrorForward;
    private String _formBeanType;
    private String _formBeanAttribute;
    private String _name;  // required to be set
    private String _prefix;
    private String _scope = DEFAULT_FORM_SCOPE;
    private String _suffix;
    private String _type;
    private boolean _unknown;
    private String _roles;
    private boolean _validate;
    private String _unqualifiedName;
    private boolean _loginRequired;
    private boolean _isOverloaded;
    private boolean _readonly;
    private boolean _isSimpleAction = false;
    private boolean _preventDoubleSubmit = false;
    private String _formBeanMember;     // pageflow-scoped form
    private Map _conditionalForwards;
    private String _formBeanMessageResourcesKey;
    private String _defaultForwardName;     // for Simple Actions


    public XWorkActionModel(String path, XWorkModuleConfigModel parent) {
        super(parent);
        _name = path;
    }

    protected XWorkActionModel(XWorkModuleConfigModel parent) {
        this(null, parent);
    }

    /**
     * Construct a copy of the given mapping, with the given path.
     */
    public XWorkActionModel(XWorkActionModel src, String newPath) {
        super(src);
        _name = newPath;
        _formBeanType = src._formBeanType;
        _formBeanAttribute = src._formBeanAttribute;
        _exceptionCatches = (ArrayList) src._exceptionCatches.clone();
        _attribute = src._attribute;
        _className = src._className;
        _validationErrorForward = src._validationErrorForward;
        _validate = src._validate;
        _prefix = src._prefix;
        _scope = src._scope;
        _suffix = src._suffix;
        _type = src._type;
        _unknown = src._unknown;
        _formBeanMember = src._formBeanMember;
        _roles = src._roles;
        _loginRequired = src._loginRequired;
        _preventDoubleSubmit = src._preventDoubleSubmit;
        _isSimpleAction = src._isSimpleAction;
        _isOverloaded = src._isOverloaded;
        _readonly = src._readonly;
        _unqualifiedName = src._unqualifiedName;
        _defaultForwardName = src._defaultForwardName;
    }

    public void writeXML(XmlModelWriter xw, Element parentElement) {
        Element actionElement = xw.addElement(parentElement, "action");
        xw.addComment(actionElement, getComment());

        actionElement.setAttribute("name", _name);
        actionElement.setAttribute("class", PAGEFLOW_XWORK_PACKAGE + ".PageFlowAction");

        addParam(xw, actionElement, "formBeanType", _formBeanType);
        addParam(xw, actionElement, "formBeanAttribute", _formBeanAttribute);
        addParam(xw, actionElement, "validationErrorForward", _validationErrorForward);
        addParam(xw, actionElement, "unqualifiedName", _unqualifiedName);
        addParam(xw, actionElement, "formBeanMember", _formBeanMember);
        addParam(xw, actionElement, "rolesAllowed", _roles);
        if (_loginRequired) addParam(xw, actionElement, "loginRequired", true);
        if (_preventDoubleSubmit) addParam(xw, actionElement, "preventDoubleSubmit", true);
        if (_isOverloaded) addParam(xw, actionElement, "overloaded", true);
        if (_readonly) addParam(xw, actionElement, "readonly", true);
        if (_isSimpleAction) addParam(xw, actionElement, "simpleAction", true);
        addParam(xw, actionElement, "defaultForward", _defaultForwardName);

        if (_conditionalForwards != null) {
            addParam(xw, actionElement, "conditionalForwards", getMapString(_conditionalForwards));
        }

        if (_formBeanMessageResourcesKey != null) {
            addParam(xw, actionElement, "formBeanMessageResourcesKey", _formBeanMessageResourcesKey);
        }

        if (_exceptionCatches != null && ! _exceptionCatches.isEmpty()) {
            for (Iterator i = _exceptionCatches.iterator(); i.hasNext();) {
                XWorkExceptionHandlerModel exceptionModel = (XWorkExceptionHandlerModel) i.next();
                exceptionModel.writeXML(xw, parentElement);
            }
        }

        writeForwards(xw, actionElement);
    }

    /**
     * Implemented for {@link XWorkExceptionHandlerContainer}.
     */
    public void addException(XWorkExceptionHandlerModel em) {
        _exceptionCatches.add(em);
    }

    public String getAttribute() {
        return _attribute;
    }

    public void setAttribute(String attribute) {
        _attribute = attribute;
    }

    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        _className = className;
    }

    public String getValidationErrorForward() {
        return _validationErrorForward;
    }

    public void setValidationErrorForward(String validationErrorForward) {
        _validationErrorForward = validationErrorForward;
    }

    public String getFormBeanType() {
        return _formBeanType;
    }

    public void setFormBeanType(String formBeanType) {
        _formBeanType = formBeanType;
        _formBeanAttribute = generateFormBeanAttribute(formBeanType, getParentApp());
    }

    public boolean isValidate() {
        return _validate;
    }

    public void setValidate(boolean validate) {
        _validate = validate;
    }

    public String getName() {
        return _name;
    }

    public String getPath(boolean useUnqualifiedPath) {
        if (useUnqualifiedPath && _unqualifiedName != null) {
            return _unqualifiedName;
        } else {
            return _name;
        }
    }

    public void setName(String name) {
        _name = name;
    }

    public String getPrefix() {
        return _prefix;
    }

    public void setPrefix(String prefix) {
        _prefix = prefix;
    }

    public String getScope() {
        return _scope == null ? DEFAULT_FORM_SCOPE : _scope;
    }

    public void setScope(String scope) {
        _scope = scope;
    }

    public String getSuffix() {
        return _suffix;
    }

    public void setSuffix(String suffix) {
        _suffix = suffix;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public boolean isUnknown() {
        return _unknown;
    }

    public void setUnknown(boolean unknown) {
        _unknown = unknown;
    }

    public String getUnqualifiedName() {
        return _unqualifiedName;
    }

    public void setUnqualifiedName(String unqualifiedName) {
        _unqualifiedName = unqualifiedName;
    }

    public String getDefaultForwardName() {
        return _defaultForwardName;
    }

    public void setDefaultForwardName(String defaultForwardName) {
        _defaultForwardName = defaultForwardName;
    }

    public String getRoles() {
        return _roles;
    }

    public void setRoles(String roles) {
        _roles = roles;
    }

    public void setLoginRequired(boolean loginRequired) {
        _loginRequired = loginRequired;
    }

    public void setPreventDoubleSubmit(boolean preventDoubleSubmit) {
        _preventDoubleSubmit = preventDoubleSubmit;
    }

    public boolean isSimpleAction() {
        return _isSimpleAction;
    }

    public void setSimpleAction(boolean simpleAction) {
        _isSimpleAction = simpleAction;
    }

    public boolean isOverloaded() {
        return _isOverloaded;
    }

    public void setOverloaded(boolean overloaded) {
        _isOverloaded = overloaded;
    }

    public String getFormBeanMember() {
        return _formBeanMember;
    }

    public void setFormBeanMember(String formBeanMember) {
        _formBeanMember = formBeanMember;
    }

    public boolean isReadonly() {
        return _readonly;
    }

    public void setReadonly(boolean readonly) {
        _readonly = readonly;
    }

    public void addConditionalForward(String expression, String forwardName) {
        if (_conditionalForwards == null) _conditionalForwards = new LinkedHashMap();
        _conditionalForwards.put(expression, forwardName);
    }

    private static String getMapString(Map map) {
        StringBuffer retVal = new StringBuffer();

        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            retVal.append(entry.getValue()).append(':').append(entry.getKey()).append(';');
        }

        return retVal.toString();
    }

    public void setFormBeanMessageResourcesKey(String formBeanMessageResourcesKey) {
        _formBeanMessageResourcesKey = formBeanMessageResourcesKey;
    }

    public String getHandlerPrefix() {
        return _name + ':';
    }

    public String getDescription() {
        return "action " + _name;
    }

    /**
     * Generate the attribute name of the form bean (to be set in request scope at runtime).
     */
    private static String generateFormBeanAttribute(String formBeanType, XWorkModuleConfigModel parentApp) {
        if (formBeanType == null) {
            return null;
        }

        int lastQualifier = formBeanType.lastIndexOf('$');

        if (lastQualifier == -1) {
            lastQualifier = formBeanType.lastIndexOf('.');
        }

        String formBeanAttr = formBeanType.substring(lastQualifier + 1);
        formBeanAttr = Character.toLowerCase(formBeanAttr.charAt(0)) + formBeanAttr.substring(1);

        // If this attribute is already being used by another form bean type, add one based on the fully-qualified name.
        for (Iterator i = parentApp.getActions().iterator(); i.hasNext();) {
            XWorkActionModel action = (XWorkActionModel) i.next();
            if (formBeanAttr.equals(action._formBeanAttribute) && ! formBeanType.equals(action.getFormBeanType())) {
                formBeanAttr = formBeanType.replace('.', '_').replace('$', '_');
            }
        }

        return formBeanAttr;
    }
}
