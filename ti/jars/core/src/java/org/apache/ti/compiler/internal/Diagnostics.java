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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

public abstract class Diagnostics {

    private AnnotationProcessorEnvironment _env;
    private boolean _hasErrors = false;

    protected Diagnostics(AnnotationProcessorEnvironment env) {
        _env = env;
    }

    public void addError(Declaration decl, String messageKey) {
        addErrorArrayArgs(decl, messageKey, null);
    }

    public void addError(Declaration decl, String messageKey, Object arg) {
        addErrorArrayArgs(decl, messageKey, new Object[]{arg});
    }

    public void addError(Declaration decl, String messageKey, Object arg1, Object arg2) {
        addErrorArrayArgs(decl, messageKey, new Object[]{arg1, arg2});
    }

    public void addError(Declaration decl, String messageKey, Object arg1, Object arg2, Object arg3) {
        addErrorArrayArgs(decl, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addErrorArrayArgs(Declaration decl, String messageKey, Object[] args) {
        _env.getMessager().printError(decl.getPosition(), getResourceString(messageKey, args));
        _hasErrors = true;
    }

    public void addErrorNoPosition(String messageKey, Object[] args) {
        _env.getMessager().printError(getResourceString(messageKey, args));
    }

    public void addError(AnnotationInstance ann, String messageKey) {
        addErrorArrayArgs(ann, messageKey, null);
    }

    public void addError(AnnotationInstance ann, String messageKey, Object arg) {
        addErrorArrayArgs(ann, messageKey, new Object[]{arg});
    }

    public void addError(AnnotationInstance ann, String messageKey, Object arg1, Object arg2) {
        addErrorArrayArgs(ann, messageKey, new Object[]{arg1, arg2});
    }

    public void addError(AnnotationInstance ann, String messageKey, Object arg1, Object arg2, Object arg3) {
        addErrorArrayArgs(ann, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addErrorArrayArgs(AnnotationInstance ann, String messageKey, Object[] args) {
        _env.getMessager().printError(ann.getPosition(), getResourceString(messageKey, args));
        _hasErrors = true;
    }

    public void addError(AnnotationValue value, String messageKey) {
        addErrorArrayArgs(value, messageKey, null);
    }

    public void addError(AnnotationValue value, String messageKey, Object arg) {
        addErrorArrayArgs(value, messageKey, new Object[]{arg});
    }

    public void addError(AnnotationValue value, String messageKey, Object arg1, Object arg2) {
        addErrorArrayArgs(value, messageKey, new Object[]{arg1, arg2});
    }

    public void addError(AnnotationValue value, String messageKey, Object arg1, Object arg2, Object arg3) {
        addErrorArrayArgs(value, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addErrorArrayArgs(AnnotationValue value, String messageKey, Object[] args) {
        _env.getMessager().printError(value.getPosition(), getResourceString(messageKey, args));
        _hasErrors = true;
    }

    public void addWarning(Declaration decl, String messageKey) {
        addWarningArrayArgs(decl, messageKey, null);
    }

    public void addWarning(Declaration decl, String messageKey, Object arg) {
        addWarningArrayArgs(decl, messageKey, new Object[]{arg});
    }

    public void addWarning(Declaration decl, String messageKey, Object arg1, Object arg2) {
        addWarningArrayArgs(decl, messageKey, new Object[]{arg1, arg2});
    }

    public void addWarning(Declaration decl, String messageKey, Object arg1, Object arg2, Object arg3) {
        addWarningArrayArgs(decl, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addWarningArrayArgs(Declaration decl, String messageKey, Object[] args) {
        _env.getMessager().printWarning(decl.getPosition(), getResourceString(messageKey, args));
    }

    public void addWarning(AnnotationInstance ann, String messageKey) {
        addWarningArrayArgs(ann, messageKey, null);
    }

    public void addWarning(AnnotationInstance ann, String messageKey, Object arg) {
        addWarningArrayArgs(ann, messageKey, new Object[]{arg});
    }

    public void addWarning(AnnotationInstance ann, String messageKey, Object arg1, Object arg2) {
        addWarningArrayArgs(ann, messageKey, new Object[]{arg1, arg2});
    }

    public void addWarning(AnnotationInstance ann, String messageKey, Object arg1, Object arg2, Object arg3) {
        addWarningArrayArgs(ann, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addWarningArrayArgs(AnnotationInstance ann, String messageKey, Object[] args) {
        _env.getMessager().printWarning(ann.getPosition(), getResourceString(messageKey, args));
    }

    public void addWarning(AnnotationValue value, String messageKey) {
        addWarningArrayArgs(value, messageKey, null);
    }

    public void addWarning(AnnotationValue value, String messageKey, Object arg) {
        addWarningArrayArgs(value, messageKey, new Object[]{arg});
    }

    public void addWarning(AnnotationValue value, String messageKey, Object arg1, Object arg2) {
        addWarningArrayArgs(value, messageKey, new Object[]{arg1, arg2});
    }

    public void addWarning(AnnotationValue value, String messageKey, Object arg1, Object arg2, Object arg3) {
        addWarningArrayArgs(value, messageKey, new Object[]{arg1, arg2, arg3});
    }

    public void addWarningArrayArgs(AnnotationValue value, String messageKey, Object[] args) {
        _env.getMessager().printWarning(value.getPosition(), getResourceString(messageKey, args));
    }

    protected abstract String getResourceString(String key, Object[] args);

    public boolean hasErrors() {
        return _hasErrors;
    }

    protected void setHasErrors(boolean hadErrors) {
        _hasErrors = hadErrors;
    }

    protected AnnotationProcessorEnvironment getAnnotationProcessorEnvironment() {
        return _env;
    }
}
