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

public class XWorkExceptionHandlerModel
        extends AbstractResultContainer
        implements JpfLanguageConstants {

    private String _type;
    private String _path;
    private String _handlerMethod;
    private String _message;
    private String _messageKey;
    private String _handlerClass;
    private boolean _readonly = false;
    private boolean _inheritedPath = false;

    protected XWorkExceptionHandlerModel(XWorkModuleConfigModel parentApp) {
        super(parentApp);
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getHandlerMethod() {
        return _handlerMethod;
    }

    public void setHandlerMethod(String handlerMethod) {
        _handlerMethod = handlerMethod;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessageKey() {
        return _messageKey;
    }

    public void setMessageKey(String messageKey) {
        _messageKey = messageKey;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    public void writeXML(XmlModelWriter xw, Element parentElement)

    {
        Element actionElement = xw.addElement(parentElement, "action");
        xw.addComment(actionElement, getComment());
        actionElement.setAttribute("name", _type);
        actionElement.setAttribute("class", PAGEFLOW_XWORK_PACKAGE + ".PageFlowExceptionHandler");

        if (_inheritedPath) addParam(xw, actionElement, "inheritedPath", true);
        addParam(xw, actionElement, "methodName", _handlerMethod);
        addParam(xw, actionElement, "messageKey", _messageKey);
        addParam(xw, actionElement, "message", _message);
        if (_readonly) addParam(xw, actionElement, "readonly", true);

        if (_path != null) {
            XWorkResultModel fwd = new XWorkResultModel("success", _path, getParentApp());
            fwd.writeXML(xw, actionElement);
        }

        writeForwards(xw, actionElement);
    }

    public boolean isReadonly() {
        return _readonly;
    }

    public void setReadonly(boolean readonly) {
        _readonly = readonly;
    }

    public boolean isInheritedPath() {
        return _inheritedPath;
    }

    public void setInheritedPath(boolean inheritedPath) {
        _inheritedPath = inheritedPath;
    }
}
