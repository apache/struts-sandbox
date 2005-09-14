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
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.io.File;
import java.util.*;


public class FlowControllerInfo
        extends SourceFileInfo
        implements JpfLanguageConstants {

    private static final ActionInfo[] EMPTY_ACTION_INFO_ARRAY = new ActionInfo[0];

    private Set _actions = new HashSet();
    private Set _returnActions = null;
    private Map _sharedFlowTypes = Collections.EMPTY_MAP;
    private Map _sharedFlowTypeNames = Collections.EMPTY_MAP;
    private Map _sharedFlowFiles = Collections.EMPTY_MAP;
    private List _referencedFiles = new ArrayList();
    private boolean _isBuilding = false;
    private Map _messageBundlesByName = new HashMap();
    private boolean _navigateToActionEnabled = false;
    private boolean _navigateToPageEnabled = false;
    private boolean _isNested;
    private MergedControllerAnnotation _mergedControllerAnnotation;


    public static class ActionInfo {

        private String _name;
        private String _beanType = null;

        public ActionInfo(String name) {
            _name = name;
        }

        public ActionInfo(String name, String beanType) {
            _name = name;
            _beanType = beanType;
        }

        public void setBeanType(String beanType) {
            _beanType = beanType;
        }

        public String getName() {
            return _name;
        }

        public String getBeanType() {
            return _beanType;
        }

        public boolean equals(Object o) {
            if (o == null || ! (o instanceof ActionInfo)) {
                return false;
            }

            ActionInfo other = (ActionInfo) o;
            if (! _name.equals(other.getName())) return false;
            String otherBeanType = other.getBeanType();
            return ((_beanType == null && otherBeanType == null)
                    || (_beanType != null && otherBeanType != null && _beanType.equals(otherBeanType)));
        }

        public int hashCode() {
            int nameHash = _name.hashCode();
            if (_beanType == null) return nameHash;
            return nameHash != 0 ? _beanType.hashCode() % nameHash : _beanType.hashCode();
        }
    }


    public FlowControllerInfo(ClassDeclaration jclass) {
        super(CompilerUtils.getSourceFile(jclass, true), jclass.getQualifiedName());
    }

    void startBuild(AnnotationProcessorEnvironment env, ClassDeclaration jclass) {
        _isBuilding = true;
        _mergedControllerAnnotation = new MergedControllerAnnotation(jclass);
        _isNested = _mergedControllerAnnotation.isNested();
        setSharedFlowInfo(env);
    }

    void endBuild() {
        _isBuilding = false;
        _sharedFlowTypes = null;    // don't hang onto ClassDeclarations
        _mergedControllerAnnotation = null;
    }

    public ActionInfo[] getActions() {
        return (ActionInfo[]) _actions.toArray(new ActionInfo[ _actions.size() ]);
    }

    public boolean isNested() {
        return _isNested;
    }

    public ActionInfo[] getReturnActions() {
        if (_returnActions == null) {
            return EMPTY_ACTION_INFO_ARRAY;
        }

        return (ActionInfo[]) _returnActions.toArray(new ActionInfo[ _returnActions.size() ]);
    }

    public String getFormBeanType(String actionName) {
        String bestType = null;

        for (Iterator ii = _actions.iterator(); ii.hasNext();) {
            ActionInfo actionInfo = (ActionInfo) ii.next();
            if (actionInfo.getName().equals(actionName)) {
                String beanType = actionInfo.getBeanType();

                //
                // In the case of overloaded actions, the non-form-bean action takes precedence.  Otherwise,
                // we look at the bean type names in alphabetical order.
                //
                if (beanType == null) return null;
                else if (bestType == null) bestType = beanType;
                else if (beanType.compareTo(bestType) < 0) bestType = beanType;
            }
        }

        return bestType;
    }

    int countReturnActions() {
        return _returnActions != null ? _returnActions.size() : 0;
    }

    public void addAction(String actionName, String formBeanType) {
        _actions.add(new ActionInfo(actionName, formBeanType));
    }

    public void addReturnAction(String returnActionName, String formBeanType) {
        if (_returnActions == null) _returnActions = new HashSet();
        _returnActions.add(new ActionInfo(returnActionName, formBeanType));
    }

    /**
     * Get a list of referenced files (files that appear in Jpf.Forward paths).
     */
    public List getReferencedFiles() {
        return _referencedFiles;
    }

    public void addReferencedFile(File file) {
        if (! file.equals(getSourceFile())) {
            _referencedFiles.add(file);
        }
    }

    private void setSharedFlowInfo(AnnotationProcessorEnvironment env) {
        //
        // First, find all referenced Shared Flow types.
        //
        _sharedFlowTypes = new LinkedHashMap();

        Collection sharedFlowRefs = _mergedControllerAnnotation.getSharedFlowRefs();

        if (sharedFlowRefs != null) {
            for (Iterator i = sharedFlowRefs.iterator(); i.hasNext();) {
                AnnotationInstance sharedFlowRef = (AnnotationInstance) i.next();
                String name = CompilerUtils.getString(sharedFlowRef, NAME_ATTR, true);
                TypeInstance type = CompilerUtils.getTypeInstance(sharedFlowRef, TYPE_ATTR, true);

                if (type instanceof DeclaredType)   // if it's not a DeclaredType, the error will be caught elsewhere.
                {
                    TypeDeclaration typeDecl = ((DeclaredType) type).getDeclaration();

                    if (typeDecl != null)     // If the declaration is null, it's an error type.
                    {
                        _sharedFlowTypes.put(name, typeDecl);
                    }
                }
            }
        }

        _sharedFlowTypeNames = new LinkedHashMap();
        _sharedFlowFiles = new LinkedHashMap();

        for (Iterator i = _sharedFlowTypes.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            TypeDeclaration type = (TypeDeclaration) entry.getValue();
            _sharedFlowTypeNames.put(entry.getKey(), type.getQualifiedName());
            File file = CompilerUtils.getSourceFile(type, false);

            if (file != null) {
                _sharedFlowFiles.put(entry.getKey(), file);
                _referencedFiles.add(file);
            }
        }
    }

    public Map getSharedFlowTypes() {
        assert _isBuilding : "use getSharedFlowTypeNames after check or generate phases";
        return _sharedFlowTypes;
    }

    public Map getSharedFlowTypeNames() {
        return _sharedFlowTypeNames;
    }

    public MergedControllerAnnotation getMergedControllerAnnotation() {
        assert _isBuilding : "only valid during the check or generate phases";
        return _mergedControllerAnnotation;
    }

    public Map getMessageBundlesByName() {
        return _messageBundlesByName;
    }

    public void addMessageBundle(String bundleName, String bundlePath) {
        _messageBundlesByName.put(bundleName, bundlePath);
    }

    public String getControllerClassName() {
        return getClassName();
    }

    public Map getSharedFlowFiles() {
        return _sharedFlowFiles;
    }

    public void enableNavigateToAction() {
        _navigateToActionEnabled = true;
    }

    public void enableNavigateToPage() {
        _navigateToPageEnabled = true;
    }

    public boolean isNavigateToActionEnabled() {
        return _navigateToActionEnabled;
    }

    public boolean isNavigateToPageEnabled() {
        return _navigateToPageEnabled;
    }

    /**
     * Add a return-action from an annotation.
     *
     * @return the form bean type, or null</code> if there is no form bean.
     */
    public TypeInstance addReturnAction(String returnActionName, AnnotationInstance annotation, TypeDeclaration outerType) {
        TypeInstance formBeanType = CompilerUtils.getTypeInstance(annotation, OUTPUT_FORM_BEAN_TYPE_ATTR, true);

        if (formBeanType == null) {
            String memberFieldName = CompilerUtils.getString(annotation, OUTPUT_FORM_BEAN_ATTR, true);

            if (memberFieldName != null) {
                FieldDeclaration field = CompilerUtils.findField(outerType, memberFieldName);
                if (field != null) formBeanType = field.getType();
            }
        }

        String formTypeName =
                formBeanType != null && formBeanType instanceof DeclaredType
                        ? CompilerUtils.getDeclaration((DeclaredType) formBeanType).getQualifiedName()
                        : null;
        addReturnAction(returnActionName, formTypeName);
        return formBeanType;
    }
}
