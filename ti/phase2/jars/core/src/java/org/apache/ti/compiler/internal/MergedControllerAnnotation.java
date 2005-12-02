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
package org.apache.ti.compiler.internal;

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MergedControllerAnnotation
        implements JpfLanguageConstants {

    private String _validatorVersion;
    private String _validatorMerge;
    private List _tilesDefsConfigs;
    private boolean _nested = false;
    private boolean _longLived = false;
    private List _rolesAllowed;
    private List _customValidatorConfigs;
    private Boolean _loginRequired = null;
    private boolean _readOnly = false;
    private boolean _inheritLocalPaths = false;
    private LinkedHashMap _forwards = new LinkedHashMap();
    private LinkedHashMap _sharedFlowRefs = new LinkedHashMap();
    private LinkedHashMap _catches = new LinkedHashMap();
    private LinkedHashMap _simpleActions = new LinkedHashMap();
    private LinkedHashMap _validatableBeans = new LinkedHashMap();
    private LinkedHashMap _messageResources = new LinkedHashMap();
    private LinkedHashMap _messageBundles = new LinkedHashMap();
    private String _multipartHandler;

    public MergedControllerAnnotation(TypeDeclaration jclass) {
        mergeControllerAnnotations(jclass);
    }

    public void mergeAnnotation(AnnotationInstance controllerAnnotation) {
        String validatorVersion = CompilerUtils.getEnumFieldName(controllerAnnotation, VALIDATOR_VERSION_ATTR, true);
        if (validatorVersion != null) _validatorVersion = validatorVersion;

        String validatorMerge = CompilerUtils.getString(controllerAnnotation, VALIDATOR_MERGE_ATTR, true);
        if (validatorMerge != null) _validatorMerge = validatorMerge;

        Boolean nested = CompilerUtils.getBoolean(controllerAnnotation, NESTED_ATTR, true);
        if (nested != null) _nested = nested.booleanValue();

        Boolean longLived = CompilerUtils.getBoolean(controllerAnnotation, LONGLIVED_ATTR, true);
        if (longLived != null) _longLived = longLived.booleanValue();

        Boolean loginRequired = CompilerUtils.getBoolean(controllerAnnotation, LOGIN_REQUIRED_ATTR, true);
        if (loginRequired != null) _loginRequired = loginRequired;

        Boolean readOnly = CompilerUtils.getBoolean(controllerAnnotation, READONLY_ATTR, true);
        if (readOnly != null) _readOnly = readOnly.booleanValue();

        Boolean inheritLocalPaths = CompilerUtils.getBoolean(controllerAnnotation, INHERIT_LOCAL_PATHS_ATTR, true);
        if (inheritLocalPaths != null) _inheritLocalPaths = inheritLocalPaths.booleanValue();

        _rolesAllowed = mergeStringArray(_rolesAllowed, controllerAnnotation, ROLES_ALLOWED_ATTR);
        _customValidatorConfigs =
                mergeStringArray(_customValidatorConfigs, controllerAnnotation, CUSTOM_VALIDATOR_CONFIGS_ATTR);
        _tilesDefsConfigs = mergeStringArray(_tilesDefsConfigs, controllerAnnotation, TILES_DEFINITIONS_CONFIGS_ATTR);
        mergeAnnotationArray(_forwards, controllerAnnotation, FORWARDS_ATTR, NAME_ATTR);
        mergeAnnotationArray(_sharedFlowRefs, controllerAnnotation, SHARED_FLOW_REFS_ATTR, NAME_ATTR);
        mergeAnnotationArray(_catches, controllerAnnotation, CATCHES_ATTR, TYPE_ATTR);
        mergeAnnotationArray(_simpleActions, controllerAnnotation, SIMPLE_ACTIONS_ATTR, NAME_ATTR);
        mergeAnnotationArray(_validatableBeans, controllerAnnotation, VALIDATABLE_BEANS_ATTR, TYPE_ATTR);
        mergeAnnotationArray(_messageBundles, controllerAnnotation, MESSAGE_BUNDLES_ATTR, BUNDLE_PATH_ATTR);

        String multipartHandler = CompilerUtils.getEnumFieldName(controllerAnnotation, MULTIPART_HANDLER_ATTR, true);
        if (multipartHandler != null) _multipartHandler = multipartHandler;
    }

    private static List mergeStringArray(List memberList, AnnotationInstance parentAnnotation,
                                         String attr) {
        List newList = CompilerUtils.getStringArray(parentAnnotation, attr, true);

        if (newList != null) {
            if (memberList == null) return newList;
            memberList.addAll(newList);
        }

        return memberList;
    }

    private static void mergeAnnotationArray(LinkedHashMap keyedList,
                                             AnnotationInstance parentAnnotation, String attr, String keyAttr) {
        List annotations = CompilerUtils.getAnnotationArray(parentAnnotation, attr, true);

        if (annotations != null) {
            for (Iterator ii = annotations.iterator(); ii.hasNext();) {
                AnnotationInstance ann = (AnnotationInstance) ii.next();
                Object key = CompilerUtils.getAnnotationValue(ann, keyAttr, true);
                if (key != null) keyedList.put(key.toString(), ann);
            }
        }
    }

    public String getValidatorVersion() {
        return _validatorVersion;
    }

    public String getValidatorMerge() {
        return _validatorMerge;
    }

    public List getTilesDefinitionsConfigs() {
        return _tilesDefsConfigs;
    }

    public boolean isNested() {
        return _nested;
    }

    public boolean isLongLived() {
        return _longLived;
    }

    public List getRolesAllowed() {
        return _rolesAllowed;
    }

    public List getCustomValidatorConfigs() {
        return _customValidatorConfigs;
    }

    public Boolean isLoginRequired() {
        return _loginRequired;
    }

    public boolean isReadOnly() {
        return _readOnly;
    }

    public boolean isInheritLocalPaths() {
        return _inheritLocalPaths;
    }

    public Collection getForwards() {
        return _forwards.values();
    }

    public Collection getSharedFlowRefs() {
        return _sharedFlowRefs.values();
    }

    public Collection getCatches() {
        return _catches.values();
    }

    public Collection getSimpleActions() {
        return _simpleActions.values();
    }

    public Collection getValidatableBeans() {
        return _validatableBeans.values();
    }

    public Collection getMessageResources() {
        return _messageResources.values();
    }

    public Collection getMessageBundles() {
        return _messageBundles.values();
    }

    public String getMultipartHandler() {
        return _multipartHandler;
    }

    private void mergeControllerAnnotations(TypeDeclaration jclass) {
        //
        // Merge in all the controller annotations, starting with the most remote superclass first.
        //
        if (jclass != null && jclass instanceof ClassDeclaration) {
            ClassType superClass = ((ClassDeclaration) jclass).getSuperclass();
            if (superClass != null) mergeControllerAnnotations(superClass.getDeclaration());
            AnnotationInstance controllerAnnotation = CompilerUtils.getAnnotation(jclass, CONTROLLER_TAG_NAME);
            if (controllerAnnotation != null) mergeAnnotation(controllerAnnotation);
        }
    }
}
