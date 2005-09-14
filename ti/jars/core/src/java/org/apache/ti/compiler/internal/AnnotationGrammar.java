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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Our base class for customizable annotation tag grammars.  It has stock behavior for basic
 * things like making sure required attributes exist, and provides plugin points for more
 * complex checks.
 */
public abstract class AnnotationGrammar
        implements JpfLanguageConstants {

    /**
     * If this tag requires a particular runtime version...
     */
    private String _requiredRuntimeVersion = null;
    private RuntimeVersionChecker _runtimeVersionChecker;
    private AnnotationProcessorEnvironment _env;
    private Diagnostics _diagnostics;
    private Map _memberGrammars = new HashMap();
    private Map _memberArrayGrammars = new HashMap();
    private Map _memberTypes = new HashMap();


    /**
     * @param requiredRuntimeVersion causes an error to be produced if the version in the manifest of beehive-netui-pageflow.jar
     *                               is not high enough.
     */
    protected AnnotationGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, String requiredRuntimeVersion,
                                RuntimeVersionChecker runtimeVersionChecker) {
        _env = env;
        _diagnostics = diags;
        _runtimeVersionChecker = runtimeVersionChecker;
        _requiredRuntimeVersion = requiredRuntimeVersion;
    }

    public final AnnotationProcessorEnvironment getEnv() {
        return _env;
    }

    public Diagnostics getDiagnostics() {
        return _diagnostics;
    }

    public final Object check(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                              MemberDeclaration classMember)
            throws FatalCompileTimeException {
        return check(annotation, parentAnnotations, classMember, -1);
    }

    public final Object check(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                              MemberDeclaration classMember, int annotationArrayIndex)
            throws FatalCompileTimeException {
        if (! beginCheck(annotation, parentAnnotations, classMember)) return null;

        Map valuesPresent = annotation.getElementValues();
        HashSet wasPresent = new HashSet();
        HashMap checkResults = new HashMap();

        if (parentAnnotations == null) parentAnnotations = new AnnotationInstance[0];
        int oldLen = parentAnnotations.length;
        AnnotationInstance[] parentsIncludingMe = new AnnotationInstance[oldLen + 1];
        System.arraycopy(parentAnnotations, 0, parentsIncludingMe, 0, oldLen);
        parentsIncludingMe[oldLen] = annotation;

        for (Iterator ii = valuesPresent.entrySet().iterator(); ii.hasNext();) {
            Map.Entry i = (Map.Entry) ii.next();
            AnnotationTypeElementDeclaration decl = (AnnotationTypeElementDeclaration) i.getKey();
            AnnotationValue value = (AnnotationValue) i.getValue();
            String memberName = decl.getSimpleName();

            wasPresent.add(memberName);
            onCheckMember(decl, value, annotation, parentAnnotations, classMember);
            Object grammarOrType = null;

            if ((grammarOrType = _memberGrammars.get(memberName)) != null) {
                AnnotationGrammar childGrammar = (AnnotationGrammar) grammarOrType;

                if (childGrammar != null)   // it will be non-null unless there are other, more basic, errors
                {
                    Object result =
                            childGrammar.check((AnnotationInstance) value.getValue(), parentsIncludingMe, classMember);

                    if (result != null) {
                        checkResults.put(memberName, result);
                    }
                }
            } else if ((grammarOrType = _memberArrayGrammars.get(memberName)) != null) {
                AnnotationGrammar arrayGrammar = (AnnotationGrammar) grammarOrType;

                if (arrayGrammar != null) {
                    List annotations = CompilerUtils.getAnnotationArray(value);

                    for (int j = 0; j < annotations.size(); ++j) {
                        AnnotationInstance ann = (AnnotationInstance) annotations.get(j);
                        arrayGrammar.check(ann, parentsIncludingMe, classMember, j);
                    }
                }
            } else {
                AnnotationMemberType memberType = (AnnotationMemberType) _memberTypes.get(memberName);

                if (memberType != null)   // it will be non-null unless there are other, more basic, errors
                {
                    Object result =
                            memberType.check(decl, value, parentsIncludingMe, classMember, annotationArrayIndex);
                    if (result != null) checkResults.put(memberName, result);
                }
            }
        }

        return endCheck(annotation, parentAnnotations, classMember, wasPresent, checkResults);
    }

    public final boolean beginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                    MemberDeclaration classMember)
            throws FatalCompileTimeException {
        //
        // First check to see if there's a required runtime version.
        //
        if (! _runtimeVersionChecker.checkRuntimeVersion(_requiredRuntimeVersion, annotation, _diagnostics,
                "error.required-runtime-version-annotation", null)) {
            return false;
        }

        return onBeginCheck(annotation, parentAnnotations, classMember);  // for derived classes
    }

    protected void addError(Declaration element, String key) {
        getDiagnostics().addError(element, key, null);
    }

    protected void addError(Declaration element, String key, Object[] args) {
        getDiagnostics().addErrorArrayArgs(element, key, args);
    }

    protected void addError(Declaration element, String key, Object arg) {
        getDiagnostics().addError(element, key, arg);
    }

    protected void addError(Declaration element, String key, Object arg1, Object arg2) {
        getDiagnostics().addError(element, key, arg1, arg2);
    }

    protected void addError(Declaration element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addError(element, key, arg1, arg2, arg3);
    }

    protected void addError(AnnotationValue element, String key) {
        getDiagnostics().addError(element, key, null);
    }

    protected void addError(AnnotationValue element, String key, Object[] args) {
        getDiagnostics().addErrorArrayArgs(element, key, args);
    }

    protected void addError(AnnotationValue element, String key, Object arg1) {
        getDiagnostics().addError(element, key, arg1);
    }

    protected void addError(AnnotationValue element, String key, Object arg1, Object arg2) {
        getDiagnostics().addError(element, key, arg1, arg2);
    }

    protected void addError(AnnotationValue element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addError(element, key, arg1, arg2, arg3);
    }

    protected void addError(AnnotationInstance element, String key) {
        getDiagnostics().addError(element, key, null);
    }

    protected void addError(AnnotationInstance element, String key, Object[] args) {
        getDiagnostics().addErrorArrayArgs(element, key, args);
    }

    protected void addError(AnnotationInstance element, String key, Object arg1) {
        getDiagnostics().addError(element, key, arg1);
    }

    protected void addError(AnnotationInstance element, String key, Object arg1, Object arg2) {
        getDiagnostics().addError(element, key, arg1, arg2);
    }

    protected void addError(AnnotationInstance element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addError(element, key, arg1, arg2, arg3);
    }

    protected void addWarning(Declaration element, String key) {
        getDiagnostics().addWarning(element, key, null);
    }

    protected void addWarning(Declaration element, String key, Object[] args) {
        getDiagnostics().addWarningArrayArgs(element, key, args);
    }

    protected void addWarning(Declaration element, String key, Object arg) {
        getDiagnostics().addWarning(element, key, arg);
    }

    protected void addWarning(Declaration element, String key, Object arg1, Object arg2) {
        getDiagnostics().addWarning(element, key, arg1, arg2);
    }

    protected void addWarning(Declaration element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addWarning(element, key, arg1, arg2, arg3);
    }

    protected void addWarning(AnnotationValue element, String key) {
        getDiagnostics().addWarning(element, key, null);
    }

    protected void addWarning(AnnotationValue element, String key, Object[] args) {
        getDiagnostics().addWarningArrayArgs(element, key, args);
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1) {
        getDiagnostics().addWarning(element, key, arg1);
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1, Object arg2) {
        getDiagnostics().addWarning(element, key, arg1, arg2);
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addWarning(element, key, arg1, arg2, arg3);
    }

    protected void addWarning(AnnotationInstance element, String key) {
        getDiagnostics().addWarning(element, key, null);
    }

    protected void addWarning(AnnotationInstance element, String key, Object[] args) {
        getDiagnostics().addWarningArrayArgs(element, key, args);
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1) {
        getDiagnostics().addWarning(element, key, arg1);
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1, Object arg2) {
        getDiagnostics().addWarning(element, key, arg1, arg2);
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1, Object arg2, Object arg3) {
        getDiagnostics().addWarning(element, key, arg1, arg2, arg3);
    }

    /**
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */
    public final Object endCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                 MemberDeclaration classMember, Set wasPresent, Map checkResults) {
        //
        // Check mutually-exclusive attributes and child annotations.
        //
        String[][] mutuallyExclusiveAttrs = getMutuallyExclusiveAttrs();
        for (int i = 0; mutuallyExclusiveAttrs != null && i < mutuallyExclusiveAttrs.length; ++i) {
            String alreadyFound = null;

            for (int j = 0; j < mutuallyExclusiveAttrs[i].length; ++j) {
                String thisAttr = mutuallyExclusiveAttrs[i][j];

                if (wasPresent.contains(thisAttr)) {
                    if (alreadyFound == null) {
                        alreadyFound = thisAttr;
                    } else {
                        String errorKey = "error.atmost-one-may-exist-" + mutuallyExclusiveAttrs[i].length;
                        getDiagnostics().addErrorArrayArgs(annotation, errorKey, mutuallyExclusiveAttrs[i]);
                    }
                }
            }
        }

        //
        // Check required attributes and child annotations.
        //
        String[][] requiredAttrs = getRequiredAttrs();
        for (int i = 0; requiredAttrs != null && i < requiredAttrs.length; ++i) {
            boolean foundOne = false;

            for (int j = 0; j < requiredAttrs[i].length; ++j) {
                String thisAttr = requiredAttrs[i][j];

                if (wasPresent.contains(thisAttr)) {
                    foundOne = true;
                    break;
                }
            }

            if (! foundOne) {
                String errorKey = "error.atleast-one-must-exist-" + requiredAttrs[i].length;
                getDiagnostics().addErrorArrayArgs(annotation, errorKey, requiredAttrs[i]);
            }
        }

        //
        // Check inter-dependencies for attributes and child annotations.
        //
        String[][] attrDependencies = getAttrDependencies();
        for (int i = 0; attrDependencies != null && i < attrDependencies.length; ++i) {
            String thisAttr = attrDependencies[i][0];

            if (wasPresent.contains(thisAttr)) {
                boolean foundOne = false;

                for (int j = 1; j < attrDependencies[i].length; ++j) {
                    if (wasPresent.contains(attrDependencies[i][j])) {
                        foundOne = true;
                        break;
                    }
                }

                if (! foundOne) {
                    String key = "error.attr-dependency-not-found-" + (attrDependencies[i].length - 1);
                    getDiagnostics().addErrorArrayArgs(annotation, key, attrDependencies[i]);
                }
            }
        }

        return onEndCheck(annotation, parentAnnotations, classMember, checkResults);   // for derived classes
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        return true;
    }

    /**
     * @param checkResults map of member-name (String) -> result-from-checking (Object)
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */
    protected Object onEndCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                MemberDeclaration classMember, Map checkResults) {
        return null;
    }

    protected void onCheckMember(AnnotationTypeElementDeclaration memberDecl, AnnotationValue member,
                                 AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                 MemberDeclaration classMember) {
    }

    /**
     * Each entry in this array (a String[]) lists mutually exclusive attributes.
     */
    public String[][] getMutuallyExclusiveAttrs() {
        return null;
    }

    /**
     * Each entry in this array (a String[]) lists attributes of which one must exist in this tag.
     */
    public String[][] getRequiredAttrs() {
        return null;
    }

    /**
     * Each entry in this array (a String[]) is an array whose first element is an attribute that
     * requires at least one of the subsequent elements to exist as an attribute.
     */
    public String[][] getAttrDependencies() {
        return null;
    }

    protected void addMemberGrammar(String memberName, AnnotationGrammar grammar) {
        _memberGrammars.put(memberName, grammar);
    }

    protected void addMemberArrayGrammar(String memberName, AnnotationGrammar grammar) {
        _memberArrayGrammars.put(memberName, grammar);
    }

    protected void addMemberType(String memberName, AnnotationMemberType type) {
        _memberTypes.put(memberName, type);
    }

    public String getRequiredRuntimeVersion() {
        return _requiredRuntimeVersion;
    }

    public RuntimeVersionChecker getRuntimeVersionChecker() {
        return _runtimeVersionChecker;
    }
}      
