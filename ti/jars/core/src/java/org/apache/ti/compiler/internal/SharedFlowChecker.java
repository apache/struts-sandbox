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

import org.apache.ti.compiler.internal.genmodel.GenSharedFlowXWorkModuleConfigModel;
import org.apache.ti.compiler.internal.genmodel.GenXWorkModuleConfigModel;
import org.apache.ti.compiler.internal.grammar.ControllerGrammar;
import org.apache.ti.compiler.internal.grammar.InvalidAttributeType;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;


public class SharedFlowChecker
        extends FlowControllerChecker {

    public SharedFlowChecker(AnnotationProcessorEnvironment env, FlowControllerInfo fcInfo, Diagnostics diagnostics) {
        super(env, fcInfo, diagnostics);
    }

    protected void doAdditionalClassChecks(ClassDeclaration jclass) {
        // Make sure there are no other shared flows in this package/directory.
        checkForOverlappingClasses(jclass, SHARED_FLOW_BASE_CLASS, SHARED_FLOW_FILE_EXTENSION_DOT,
                "error.overlapping-sharedflows");
    }

    protected String getDesiredBaseClass(ClassDeclaration jclass) {
        File sourceFile = CompilerUtils.getSourceFile(jclass, true);
        if (sourceFile.getName().endsWith(SHARED_FLOW_FILE_EXTENSION_DOT)) return SHARED_FLOW_BASE_CLASS;
        return null;
    }

    protected GenXWorkModuleConfigModel createStrutsApp(ClassDeclaration jclass)
            throws XmlException, IOException, FatalCompileTimeException {
        File sourceFile = CompilerUtils.getSourceFile(jclass, true);
        return new GenSharedFlowXWorkModuleConfigModel(sourceFile, jclass, getEnv(), getFCSourceFileInfo(), true, getDiagnostics());
    }

    protected AnnotationGrammar getControllerGrammar() {
        return new SharedFlowControllerGrammar();
    }

    private class SharedFlowControllerGrammar
            extends ControllerGrammar {

        public SharedFlowControllerGrammar() {
            super(SharedFlowChecker.this.getEnv(), SharedFlowChecker.this.getDiagnostics(),
                    SharedFlowChecker.this.getRuntimeVersionChecker(), SharedFlowChecker.this.getFCSourceFileInfo());
            InvalidAttributeType type = new InvalidAttributeType(null, this, "error.only-valid-on-pageflow",
                    new Object[]{NESTED_ATTR, JPF_BASE_CLASS});
            addMemberType(NESTED_ATTR, type);
            type = new InvalidAttributeType(null, this, "error.only-valid-on-pageflow",
                    new Object[]{LONGLIVED_ATTR, JPF_BASE_CLASS});
            addMemberType(LONGLIVED_ATTR, type);
        }
    }
}
