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
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

public class FacesBackingGenerator
        extends BaseGenerator
        implements JpfLanguageConstants {

    public FacesBackingGenerator(AnnotationProcessorEnvironment env, SourceFileInfo sourceFileInfo, Diagnostics diagnostics) {
        super(env, sourceFileInfo, diagnostics);
    }

    public void generate(ClassDeclaration publicClass)
            throws FatalCompileTimeException {
        AnnotationInstance facesBackingAnnotation = CompilerUtils.getAnnotation(publicClass, FACES_BACKING_TAG_NAME);
        assert facesBackingAnnotation != null;  // checker should enforce this
        AnnotationToXML atx = new AnnotationToXML(publicClass);

        // Add the class-level @Jpf.FacesBacking annotation.
        atx.include(publicClass, facesBackingAnnotation);

        // For each method, add the @Jpf.CommandHandler annotation.
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(publicClass, COMMAND_HANDLER_TAG_NAME);
        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            AnnotationInstance commandHandlerAnn = CompilerUtils.getAnnotation(method, COMMAND_HANDLER_TAG_NAME);
            atx.include(method, commandHandlerAnn);
        }

        // Add @Jpf.SharedFlowField, @Jpf.PageFlowField, @Control.
        FlowControllerGenerator.includeFieldAnnotations(atx, publicClass, PAGE_FLOW_FIELD_TAG_NAME);

        // Write the file.
        atx.writeXml(getDiagnostics(), getEnv());
    }
}
