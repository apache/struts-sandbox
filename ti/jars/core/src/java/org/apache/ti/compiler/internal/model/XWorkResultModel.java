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
import java.util.List;

/**
 * Represents an action forward in a Struts application.
 */
public class XWorkResultModel
        extends XWorkElementSupport
        implements JpfLanguageConstants {

    private static final int NAVIGATE_TO_PAGE = 0;
    private static final int NAVIGATE_TO_ACTION = 1;

    private String _returnAction = null;
    private String _name;  // required to be set
    private String _path = null;
    private int _navigateToType = -1;
    private int _navigateToPreviousIndex = -1;
    private boolean _redirect = false;
    private boolean _externalRedirect = false;
    private String _outputFormBeanType;
    private String _outputFormBeanMember;
    private boolean _hasExplicitRedirectValue = false;
    private List _actionOutputs = null;
    private boolean _restoreQueryString = false;
    private boolean _inheritedPath = false;


    protected XWorkResultModel(XWorkModuleConfigModel parent) {
        super(parent);
    }

    public XWorkResultModel(String name, String path, XWorkModuleConfigModel parent) {
        super(parent);
        _name = name;
        _path = path;
    }

    public void writeXML(XmlModelWriter xw, Element parentElement) {
        assert _name != null;

        Element resultElement = xw.addElement(parentElement, "result");
        xw.addComment(resultElement, getComment());
        resultElement.setAttribute("name", _name);

        if (_navigateToType == NAVIGATE_TO_PAGE) {
            resultElement.setAttribute("type", XWorkModuleConfigModel.NAVIGATE_TO_PAGE_RESULT);
            addParam(xw, resultElement, "previousPageIndex", _navigateToPreviousIndex);
        } else if (_navigateToType == NAVIGATE_TO_ACTION) {
            resultElement.setAttribute("type", XWorkModuleConfigModel.NAVIGATE_TO_ACTION_RESULT);
            addParam(xw, resultElement, "previousActionIndex", _navigateToPreviousIndex);
        } else if (_returnAction != null) {
            assert _path == null : _path;
            resultElement.setAttribute("type", XWorkModuleConfigModel.RETURN_ACTION_RESULT);
        } else {
            assert _navigateToType == -1 : _navigateToType;
            resultElement.setAttribute("type", XWorkModuleConfigModel.PATH_RESULT);
        }

        addParam(xw, resultElement, "location", _path);
        if (_redirect) addParam(xw, resultElement, "redirect", _redirect);

        //
        // TODO: comment
        //
        if (_inheritedPath) addParam(xw, resultElement, "inheritedPath", true);

        //
        // "externalRedirect" is set using set-property, to indicate that the redirect
        // is to another app.
        //
        if (_externalRedirect) addParam(xw, resultElement, "externalRedirect", true);

        if (_hasExplicitRedirectValue) addParam(xw, resultElement, "hasExplicitRedirectValue", true);

        if (_restoreQueryString) addParam(xw, resultElement, "restoreQueryString", true);

        if (_actionOutputs != null && _actionOutputs.size() > 0) {
            int n = _actionOutputs.size();
            addParam(xw, resultElement, "actionOutputCount", Integer.toString(n));

            for (int i = 0; i < n; ++i) {
                ActionOutputModel pi = (ActionOutputModel) _actionOutputs.get(i);
                String val = pi.getType() + '|' + pi.getNullable() + '|' + pi.getName();
                addParam(xw, resultElement, "actionOutput" + i, val);
            }
        }

        if (_returnAction != null) addParam(xw, resultElement, "returnAction", _returnAction);
        if (_outputFormBeanType != null) addParam(xw, resultElement, "outputFormBeanType", _outputFormBeanType);
        if (_outputFormBeanMember != null) addParam(xw, resultElement, "outputFormBeanMember", _outputFormBeanMember);
    }

    public void setReturnToPage(int prevPageIndex) {
        _navigateToType = NAVIGATE_TO_PAGE;
        _navigateToPreviousIndex = prevPageIndex;
    }

    public void setReturnToAction() {
        _navigateToType = NAVIGATE_TO_ACTION;
        _navigateToPreviousIndex = 0;
    }

    public boolean isNavigateTo() {
        return _navigateToType != -1;
    }

    public boolean isNestedReturn() {
        return _returnAction != null;
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

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getReturnAction() {
        return _returnAction;
    }

    public void setReturnAction(String returnAction) {
        _returnAction = returnAction;
    }

    public boolean isRedirect() {
        return _redirect;
    }

    public void setRedirect(boolean redirect) {
        _redirect = redirect;
        _hasExplicitRedirectValue = redirect;
    }

    public boolean isExternalRedirect() {
        return _externalRedirect;
    }

    public void setExternalRedirect(boolean externalRedirect) {
        _externalRedirect = externalRedirect;
        if (externalRedirect) setRedirect(externalRedirect);
    }

    public boolean isRestoreQueryString() {
        return _restoreQueryString;
    }

    public void setRestoreQueryString(boolean restore) {
        _restoreQueryString = restore;
    }

    /**
     * @see #forwardsToPage
     * @deprecated
     */
    public final boolean isPageForward() {
        return forwardsToPage();
    }

    public boolean forwardsToPage() {
        return ! _path.endsWith(".do") && ! _path.endsWith(".jpf");  // NOI18N
    }

    public boolean forwardsToAction() {
        return _path.endsWith(ACTION_EXTENSION_DOT);  // NOI18N
    }

    public final boolean forwardsToPageFlow() {
        return _path.endsWith(JPF_FILE_EXTENSION_DOT);  // NOI18N
    }

    public String getPageName() {
        assert forwardsToPage() : "getPageName() called for non-page " + _path;  // NOI18N

        int slash = _path.lastIndexOf('/');  // NOI18N
        return slash != -1 ? _path.substring(slash + 1) : _path;
    }

    public String getActionName() {
        assert forwardsToAction() : "getActionName() called for non-action" + _path;  // NOI18N

        int index = _path.indexOf(ACTION_EXTENSION_DOT);  // NOI18N
        assert index != -1;
        return _path.substring(0, index);
    }

    public void addActionOutput(ActionOutputModel actionOutput) {
        if (_actionOutputs == null) _actionOutputs = new ArrayList();
        _actionOutputs.add(actionOutput);
    }

    public boolean isInheritedPath() {
        return _inheritedPath;
    }

    public void setInheritedPath(boolean inheritedPath) {
        _inheritedPath = inheritedPath;
    }
}
