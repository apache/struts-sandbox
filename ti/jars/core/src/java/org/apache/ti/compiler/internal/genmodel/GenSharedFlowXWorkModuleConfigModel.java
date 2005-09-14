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
package org.apache.ti.compiler.internal.genmodel;

import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;


public class GenSharedFlowXWorkModuleConfigModel
        extends GenXWorkModuleConfigModel
        implements JpfLanguageConstants {

    public GenSharedFlowXWorkModuleConfigModel(File sourceFile, ClassDeclaration jclass, AnnotationProcessorEnvironment env,
                                               FlowControllerInfo fcInfo, boolean checkOnly, Diagnostics diagnostics)
            throws XmlException, IOException, FatalCompileTimeException {
        super(sourceFile, jclass, env, fcInfo, checkOnly, diagnostics);
        recalculateStrutsConfigFile();
        setSharedFlow(true);
    }

    String getConfigFilePath() {
        return getOutputFilePath(SHARED_FLOW_CONFIG_FILE_BASENAME, getContainingPackage());
    }

    protected String getValidationFilePrefix() {
        return "sharedflow-validation";
    }
}
