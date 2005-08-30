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

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import org.apache.ti.pageflow.internal.InternalConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleConfig {

    private boolean _isNestedFlow;
    private boolean _isLongLivedFlow;
    private String _controllerClassName;
    private String _namespace;
    private boolean _isReturnToPageDisabled;
    private boolean _isReturnToActionDisabled;
    private LinkedHashMap/*<String, String>*/ _referencedSharedFlowTypes;
    private boolean _isSharedFlow;
    private String _overrideMultipartClass = null;
    private String _overrideMemFileSize = null;
    private boolean _forceMultipartDisabled = false;
    private Map/*<String, ActionConfig>*/ _actionConfigs;
    private Map/*<String, List>*/ _formBeanTypeToAttrs;
    private Map/*<String, String>*/ _formBeanAttrToType;


    protected ModuleConfig() {
    }

    public ModuleConfig(String namespace, ActionConfig moduleMetadata, Map actionConfigs) {
        _namespace = namespace;
        _actionConfigs = actionConfigs;
        Map params = moduleMetadata.getParams();
        _controllerClassName = (String) params.get("controllerClassName");
        assert _controllerClassName != null : "No controllerClassName for module " + namespace;
        _isNestedFlow = Boolean.valueOf((String) params.get("nestedFlow")).booleanValue();
        _isLongLivedFlow = Boolean.valueOf((String) params.get("longLivedFlow")).booleanValue();
        _isSharedFlow = Boolean.valueOf((String) params.get("sharedFlow")).booleanValue();
        _isReturnToPageDisabled = Boolean.valueOf((String) params.get("returnToPageDisabled")).booleanValue();
        _isReturnToActionDisabled = Boolean.valueOf((String) params.get("returnToActionDisabled")).booleanValue();
        // TODO: parse shared flow references
        // TODO: parse multipart params
        
        // Cache:
        //     - a Map of type name (String) -> list of form bean attribute names (List).
        //     - a Map of form bean attribute (String) -> form bean type name (String)
        HashMap formBeanTypeToAttrs = new HashMap();
        HashMap formBeanAttrToType = new HashMap();
        for (Iterator i = getActionConfigs().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String actionName = (String) entry.getKey();
            ActionConfig actionConfig = (ActionConfig) entry.getValue();
            Map actionParams = actionConfig.getParams();
            String formBeanAttr = (String) actionParams.get(InternalConstants.FORM_BEAN_ATTR_PARAM);

            if (formBeanAttr != null) {
                String formBeanType = (String) actionParams.get(InternalConstants.FORM_BEAN_TYPE_PARAM);
                assert formBeanType != null : "No form bean type for attr " + formBeanAttr + " on action " + actionName;
                List attrNames = (List) formBeanTypeToAttrs.get(formBeanType);
                if (attrNames == null) {
                    attrNames = new ArrayList();
                    formBeanTypeToAttrs.put(formBeanType, attrNames);
                }
                attrNames.add(formBeanAttr);

                assert !formBeanAttrToType.containsKey(formBeanAttr)
                        || formBeanAttrToType.get(formBeanAttr).equals(formBeanType)
                        : "Duplicate entry for attr \"" + formBeanAttr + "\": " + formBeanAttrToType.get(formBeanAttr)
                        + ", " + formBeanType;
                formBeanAttrToType.put(formBeanAttr, formBeanType);
            }
        }

        _formBeanTypeToAttrs = Collections.unmodifiableMap(formBeanTypeToAttrs);
        _formBeanAttrToType = Collections.unmodifiableMap(formBeanAttrToType);
    }

    public String getNamespace() {
        return _namespace;
    }

    public void setNestedFlow(boolean nestedFlow) {
        _isNestedFlow = nestedFlow;
    }

    public boolean isNestedFlow() {
        return _isNestedFlow;
    }

    public boolean isLongLivedFlow() {
        return _isLongLivedFlow;
    }

    public void setLongLivedFlow(boolean longLivedFlow) {
        _isLongLivedFlow = longLivedFlow;
    }

    public String getControllerClassName() {
        return _controllerClassName;
    }

    public void setControllerClassName(String controllerClassName) {
        _controllerClassName = controllerClassName;
    }

    public ActionConfig findActionConfig(String actionName) {
        return (ActionConfig) getActionConfigs().get(actionName);
    }

    /**
     * @return a Map of action-name (String) to action-config ({@link ActionConfig}).
     */
    public Map getActionConfigs() {
        // TODO: we could also get this with:
        //     ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfigs().get(getNamespace());
        //     Is there any disadvantage to holding onto them?
        return _actionConfigs;
    }

    public ResultConfig findGlobalResult(String resultName) {
        throw new UnsupportedOperationException("NYI"); // TODO: NYI        
    }

    /**
     * @return a Map of type name (String) -> list of form bean attribute names (List).
     */
    public Map getFormBeanAttributeNames() {
        return _formBeanTypeToAttrs;
    }

    /**
     * @return a Map of form bean attribute (String) -> form bean type name (String)
     */
    public Map getFormBeans() {
        return _formBeanAttrToType;
    }

    public boolean isReturnToActionDisabled() {
        return _isReturnToActionDisabled;
    }

    public void setReturnToActionDisabled(boolean returnToActionDisabled) {
        _isReturnToActionDisabled = returnToActionDisabled;
    }

    public boolean isReturnToPageDisabled() {
        return _isReturnToPageDisabled;
    }

    public void setReturnToPageDisabled(boolean returnToPageDisabled) {
        _isReturnToPageDisabled = returnToPageDisabled;
    }

    public boolean isSharedFlow() {
        return _isSharedFlow;
    }

    public void setSharedFlow(boolean sharedFlow) {
        _isSharedFlow = sharedFlow;
    }

    public String getOverrideMultipartClass() {
        return _overrideMultipartClass;
    }

    public void setOverrideMultipartClass(String overrideMultipartClass) {
        _overrideMultipartClass = overrideMultipartClass;
    }

    public String getOverrideMemFileSize() {
        return _overrideMemFileSize;
    }

    public void setOverrideMemFileSize(String overrideMemFileSize) {
        _overrideMemFileSize = overrideMemFileSize;
    }

    public boolean isForceMultipartDisabled() {
        return _forceMultipartDisabled;
    }

    public void setForceMultipartDisabled(boolean forceMultipartDisabled) {
        _forceMultipartDisabled = forceMultipartDisabled;
    }

    /**
     * @return a Map of shared-flow-name (String) -> shared-flow-typename (String)
     */
    public Map/*<String, String>*/ getSharedFlowTypes() {
        return _referencedSharedFlowTypes;
    }

    public String findExceptionHandler(Class exceptionType) {
        do {
            String handlerName = exceptionType.getName();
            if (findActionConfig(handlerName) != null) return handlerName;
            exceptionType = exceptionType.getSuperclass();
        } while (exceptionType != null);

        return null;
    }
}
