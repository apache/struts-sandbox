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

import org.apache.ti.compiler.internal.genmodel.GenValidationModel;
import org.apache.ti.compiler.internal.genmodel.GenXWorkModuleConfigModel;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;

abstract class FlowControllerGenerator
        extends BaseGenerator {
    private static long _compilerJarTimestamp = -1;
    private static final boolean ALWAYS_GENERATE = true; // TODO: this turns stale checking off.  Do we need it?
    private static final String CONTROL_ANNOTATION = JpfLanguageConstants.TI_PACKAGE + ".controls.api.bean.Control";

    protected FlowControllerGenerator(AnnotationProcessorEnvironment env, FlowControllerInfo fcInfo, Diagnostics diagnostics) {
        super(env, fcInfo, diagnostics);
    }

    protected abstract GenXWorkModuleConfigModel createStrutsApp(ClassDeclaration cl)
            throws IOException, FatalCompileTimeException;

    public void generate(ClassDeclaration publicClass) {
        GenXWorkModuleConfigModel app = null;
        getFCSourceFileInfo().startBuild(getEnv(), publicClass);

        try {
            // Write the Struts config XML, and the Validator config XML if appropriate.
            app = createStrutsApp(publicClass);

            GenValidationModel validationModel = new GenValidationModel(publicClass, app, getEnv());

            if (!validationModel.isEmpty()) {
                app.setValidationModel(validationModel);
                validationModel.writeToFile();
            }

            generateStrutsConfig(app, publicClass);

            // First, write out XML for any fields annotated with @Jpf.SharedFlowField or @Control.
            writeFieldAnnotations(publicClass, app);
        } catch (FatalCompileTimeException e) {
            e.printDiagnostic(getDiagnostics());
        } catch (Exception e) {
            e.printStackTrace(); // @TODO log
            assert e instanceof IOException : e.getClass().getName();
            getDiagnostics().addError(publicClass, "error.could-not-generate", (app != null) ? app.getStrutsConfigFile() : null,
                                      e.getMessage());
        } finally {
            getFCSourceFileInfo().endBuild();
        }
    }

    private void writeFieldAnnotations(ClassDeclaration classDecl, GenXWorkModuleConfigModel app)
            throws FatalCompileTimeException {
        try {
            AnnotationToXML atx = new AnnotationToXML(classDecl);

            if (includeFieldAnnotations(atx, classDecl, null)) {
                atx.writeXml(getDiagnostics(), getEnv());
            }
        } catch (Exception e) {
            getDiagnostics().addError(classDecl, "error.could-not-generate", AnnotationToXML.getFilePath(classDecl),
                                      e.getMessage());
            e.printStackTrace(); // TODO: log instead
        }
    }

    static boolean includeFieldAnnotations(AnnotationToXML atx, TypeDeclaration typeDecl, String additionalAnnotation) {
        Collection fields = CompilerUtils.getClassFields(typeDecl);
        boolean hasFieldAnnotations = false;

        if (fields.size() > 0) {
            for (Iterator i = fields.iterator(); i.hasNext();) {
                FieldDeclaration field = (FieldDeclaration) i.next();
                AnnotationInstance fieldAnnotation = CompilerUtils.getAnnotation(field,
                                                                                 JpfLanguageConstants.SHARED_FLOW_FIELD_TAG_NAME);

                if (fieldAnnotation == null) {
                    fieldAnnotation = CompilerUtils.getAnnotationFullyQualified(field, CONTROL_ANNOTATION);
                }

                if ((fieldAnnotation == null) && (additionalAnnotation != null)) {
                    fieldAnnotation = CompilerUtils.getAnnotation(field, additionalAnnotation);
                }

                if (fieldAnnotation != null) {
                    atx.include(field, fieldAnnotation);
                    hasFieldAnnotations = true;
                }
            }
        }

        return hasFieldAnnotations;
    }

    protected void generateStrutsConfig(GenXWorkModuleConfigModel app, ClassDeclaration publicClass) {
        File strutsConfigFile = null;

        try {
            strutsConfigFile = app.getStrutsConfigFile();

            if (ALWAYS_GENERATE || app.isStale()) {
                // @TODO logger.info( "Writing Struts module: " + _strutsConfig.getStrutsConfigFile() );
                app.writeToFile();
            } else if (_compilerJarTimestamp > strutsConfigFile.lastModified()) {
                // @TODO logger.info( _compilerJarName + " has been updated; writing Struts module "
                //          + _strutsConfig.getStrutsConfigFile() );
                app.writeToFile();
            } else {
                // @TODO logger.info( "Struts module " + _strutsConfig.getStrutsConfigFile() + " is up-to-date." );
            }
        } catch (FatalCompileTimeException e) {
            e.printDiagnostic(getDiagnostics());
        } catch (Exception e) {
            e.printStackTrace(); // @TODO get rid of this
            assert e instanceof IOException : e.getClass().getName();

            getDiagnostics().addError(publicClass, "error.could-not-generate",
                                      (strutsConfigFile != null) ? strutsConfigFile.getPath() : null, e.getMessage());
        }
    }

    protected FlowControllerInfo getFCSourceFileInfo() {
        return (FlowControllerInfo) super.getSourceFileInfo();
    }
}
