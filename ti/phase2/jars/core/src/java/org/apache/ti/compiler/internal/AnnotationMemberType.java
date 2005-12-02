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


public class AnnotationMemberType
        implements JpfLanguageConstants {

    /**
     * set if this entire attribute type requires a particular runtime version.
     */
    private String _requiredRuntimeVersion = null;
    private AnnotationGrammar _parentGrammar;
    private AnnotationMemberType _nextInChain;


    public AnnotationMemberType(String requiredRuntimeVersion, AnnotationGrammar parentGrammar) {
        _requiredRuntimeVersion = requiredRuntimeVersion;
        _parentGrammar = parentGrammar;
    }

    public AnnotationMemberType(String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                                AnnotationMemberType nextInChain) {
        this(requiredRuntimeVersion, parentGrammar);
        _nextInChain = nextInChain;
    }

    /**
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */
    public final Object check(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                              AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                              int annotationArrayIndex)
            throws FatalCompileTimeException {
        //
        // First check to see if this attribute requires a particular runtime version.
        //
        String valueName = valueDecl.getSimpleName();
        Diagnostics diags = _parentGrammar.getDiagnostics();
        _parentGrammar.getRuntimeVersionChecker().checkRuntimeVersion(
                _requiredRuntimeVersion, value, diags, "error.required-runtime-version-attribute", null);

        // for derived classes
        Object retVal = onCheck(valueDecl, value, parentAnnotations, classMember, annotationArrayIndex);
        if (_nextInChain != null) {
            return _nextInChain.check(valueDecl, value, parentAnnotations, classMember, annotationArrayIndex);
        }
        return retVal;
    }

    /**
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */
    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        return null;
    }

    protected void addError(Declaration element, String key) {
        _parentGrammar.addError(element, key, null);
    }

    protected void addError(Declaration element, String key, Object[] args) {
        _parentGrammar.addError(element, key, args);
    }

    protected void addError(Declaration element, String key, Object arg) {
        _parentGrammar.addError(element, key, new Object[]{arg});
    }

    protected void addError(Declaration element, String key, Object arg1, Object arg2) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2});
    }

    protected void addError(Declaration element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected void addError(AnnotationValue element, String key) {
        _parentGrammar.addError(element, key, null);
    }

    protected void addError(AnnotationValue element, String key, Object[] args) {
        _parentGrammar.addError(element, key, args);
    }

    protected void addError(AnnotationValue element, String key, Object arg1) {
        _parentGrammar.addError(element, key, new Object[]{arg1});
    }

    protected void addError(AnnotationValue element, String key, Object arg1, Object arg2) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2});
    }

    protected void addError(AnnotationValue element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected void addError(AnnotationInstance element, String key) {
        _parentGrammar.addError(element, key, null);
    }

    protected void addError(AnnotationInstance element, String key, Object[] args) {
        _parentGrammar.addError(element, key, args);
    }

    protected void addError(AnnotationInstance element, String key, Object arg1) {
        _parentGrammar.addError(element, key, new Object[]{arg1});
    }

    protected void addError(AnnotationInstance element, String key, Object arg1, Object arg2) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2});
    }

    protected void addError(AnnotationInstance element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addError(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected void addWarning(Declaration element, String key) {
        _parentGrammar.addWarning(element, key, null);
    }

    protected void addWarning(Declaration element, String key, Object[] args) {
        _parentGrammar.addWarning(element, key, args);
    }

    protected void addWarning(Declaration element, String key, Object arg) {
        _parentGrammar.addWarning(element, key, new Object[]{arg});
    }

    protected void addWarning(Declaration element, String key, Object arg1, Object arg2) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2});
    }

    protected void addWarning(Declaration element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected void addWarning(AnnotationValue element, String key) {
        _parentGrammar.addWarning(element, key, null);
    }

    protected void addWarning(AnnotationValue element, String key, Object[] args) {
        _parentGrammar.addWarning(element, key, args);
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1});
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1, Object arg2) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2});
    }

    protected void addWarning(AnnotationValue element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected void addWarning(AnnotationInstance element, String key) {
        _parentGrammar.addWarning(element, key, null);
    }

    protected void addWarning(AnnotationInstance element, String key, Object[] args) {
        _parentGrammar.addWarning(element, key, args);
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1});
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1, Object arg2) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2});
    }

    protected void addWarning(AnnotationInstance element, String key, Object arg1, Object arg2, Object arg3) {
        _parentGrammar.addWarning(element, key, new Object[]{arg1, arg2, arg3});
    }

    protected AnnotationGrammar getParentGrammar() {
        return _parentGrammar;
    }

    protected final AnnotationProcessorEnvironment getEnv() {
        return _parentGrammar.getEnv();
    }

    protected final Diagnostics getDiagnostics() {
        return _parentGrammar.getDiagnostics();
    }
}
