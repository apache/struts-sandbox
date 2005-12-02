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

import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

public abstract class BaseGenerator {

    private AnnotationProcessorEnvironment _env;
    private Diagnostics _diagnostics;
    private SourceFileInfo _sourceFileInfo;


    protected BaseGenerator(AnnotationProcessorEnvironment env, SourceFileInfo sourceFileInfo, Diagnostics diagnostics) {
        _env = env;
        _diagnostics = diagnostics;
        _sourceFileInfo = sourceFileInfo;
    }

    public abstract void generate(ClassDeclaration publicClass)
            throws FatalCompileTimeException;

    protected AnnotationProcessorEnvironment getEnv() {
        return _env;
    }

    protected Diagnostics getDiagnostics() {
        return _diagnostics;
    }

    protected SourceFileInfo getSourceFileInfo() {
        return _sourceFileInfo;
    }
}
