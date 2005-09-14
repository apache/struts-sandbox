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

import org.apache.ti.compiler.internal.genmodel.GenXWorkModuleConfigModel;
import org.apache.ti.compiler.internal.grammar.ControllerGrammar;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


public class PageFlowChecker
        extends FlowControllerChecker
        implements JpfLanguageConstants {

    public PageFlowChecker(AnnotationProcessorEnvironment env, Diagnostics diagnostics, FlowControllerInfo fcInfo) {
        super(env, fcInfo, diagnostics);
    }

    protected void checkField(FieldDeclaration field, TypeDeclaration jclass) {
        //
        // Check to make sure that if this is a Shared Flow field, its type matches up with the type declared
        // for the shared flow of that name.
        //
        AnnotationInstance sfFieldAnn = CompilerUtils.getAnnotation(field, SHARED_FLOW_FIELD_TAG_NAME);

        if (sfFieldAnn != null) {
            String sharedFlowName = CompilerUtils.getString(sfFieldAnn, NAME_ATTR, true);
            assert sharedFlowName != null;

            Collection sharedFlowRefs =
                    getFCSourceFileInfo().getMergedControllerAnnotation().getSharedFlowRefs();

            boolean foundOne = false;

            if (sharedFlowRefs != null) {
                for (Iterator ii = sharedFlowRefs.iterator(); ii.hasNext();) {
                    AnnotationInstance sharedFlowRef = (AnnotationInstance) ii.next();
                    if (sharedFlowName.equals(CompilerUtils.getString(sharedFlowRef, NAME_ATTR, true))) {
                        foundOne = true;

                        TypeInstance sfType = CompilerUtils.getTypeInstance(sharedFlowRef, TYPE_ATTR, true);
                        TypeInstance ft = field.getType();

                        if (! (sfType instanceof DeclaredType)
                                || ! CompilerUtils.isAssignableFrom(ft, ((DeclaredType) sfType).getDeclaration())) {
                            getDiagnostics().addError(
                                    field, "error.field-not-assignable",
                                    CompilerUtils.getDeclaration((DeclaredType) sfType).getQualifiedName());
                        }
                    }
                }
            }

            if (! foundOne) {
                getDiagnostics().addError(sfFieldAnn, "error.no-matching-shared-flow-declared",
                        SHARED_FLOW_REF_TAG_NAME, sharedFlowName);
            }
        } else if (CompilerUtils.isAssignableFrom(SHARED_FLOW_BASE_CLASS, field.getType(), getEnv())) {
            // Output a warning if the field type extends SharedFlowController but there's no @Jpf.SharedFlowField
            // annotation (in which case the field won't get auto-initialized at runtime.
            getDiagnostics().addWarning(field, "warning.shared-flow-field-no-annotation",
                    field.getSimpleName(), SHARED_FLOW_BASE_CLASS,
                    ANNOTATION_INTERFACE_PREFIX + SHARED_FLOW_FIELD_TAG_NAME);
        }

        super.checkField(field, jclass);
    }

    protected void doAdditionalClassChecks(ClassDeclaration jpfClass) {
        // Make sure there are no other page flows in this package/directory.
        checkForOverlappingClasses(jpfClass, JPF_BASE_CLASS, JPF_FILE_EXTENSION_DOT, "error.overlapping-pageflows");

        PackageDeclaration pkg = jpfClass.getPackage();
        File jpfFile = CompilerUtils.getSourceFile(jpfClass, true);
        File parentDir = jpfFile.getParentFile();

        //
        // Check the package name.
        //
        String jpfPackageName = pkg.getQualifiedName();

        if (jpfPackageName != null && jpfPackageName.length() > 0) {
            String expectedPackage = parentDir.getAbsolutePath().replace('\\', '/').replace('/', '.');

            if (! expectedPackage.endsWith(jpfPackageName)) {
                getDiagnostics().addError(jpfClass, "error.wrong-package-for-directory", parentDir.getPath());
            }
        }

        //
        // Issue a warning if the class name is the same as the parent package name.
        // This causes ambiguity when resolving inner classes.
        //
        if (jpfClass.getSimpleName().equals(pkg.getQualifiedName())) {
            getDiagnostics().addWarning(jpfClass, "warning.classname-same-as-package");
        }

        //
        // Make sure every .jpf has a begin action if the class isn't abstract.
        //
        boolean isAbstract = jpfClass.hasModifier(Modifier.ABSTRACT);
        FlowControllerInfo fcInfo = getFCSourceFileInfo();

        // TODO - rich - We're inferring a begin action if there isn't one.  Not sure how I feel about this yet. :)
        /*
        if ( ! WebappPathOrActionType.actionExists( BEGIN_ACTION_NAME, jpfClass, null, getEnv(), fcInfo, true )
             && ! isAbstract )
        {
            getDiagnostics().addError( jpfClass, "error.no-begin-action" );
        }
        */

        //
        // Make sure every nested pageflow has a returnAction.  Return actions are added by ForwardGrammar, but
        // here we also need to add them for inherited Forwards and SimpleActions.
        //
        if (fcInfo.isNested()) {
            MergedControllerAnnotation mca = fcInfo.getMergedControllerAnnotation();
            addReturnActions(mca.getSimpleActions(), fcInfo, jpfClass, CONDITIONAL_FORWARDS_ATTR);
            addReturnActions(mca.getForwards(), fcInfo, jpfClass, null);

            if (! isAbstract && fcInfo.countReturnActions() == 0) {
                getDiagnostics().addError(jpfClass, "error.no-return-action",
                        ANNOTATION_INTERFACE_PREFIX + FORWARD_TAG_NAME,
                        RETURN_ACTION_ATTR);
            }
        }
    }

    private void addReturnActions(Collection forwardAnnotations, FlowControllerInfo fcInfo,
                                  TypeDeclaration outerType, String childArrayAttr) {
        for (Iterator ii = forwardAnnotations.iterator(); ii.hasNext();) {
            AnnotationInstance ann = (AnnotationInstance) ii.next();
            String returnAction = CompilerUtils.getString(ann, RETURN_ACTION_ATTR, true);
            if (returnAction != null) fcInfo.addReturnAction(returnAction, ann, outerType);

            if (childArrayAttr != null) {
                Collection children = CompilerUtils.getAnnotationArray(ann, childArrayAttr, true);
                if (children != null) addReturnActions(children, fcInfo, outerType, null);
            }
        }
    }

    protected String getDesiredBaseClass(ClassDeclaration jclass) {
        return JPF_BASE_CLASS;
    }

    protected GenXWorkModuleConfigModel createStrutsApp(ClassDeclaration jclass)
            throws XmlException, IOException, FatalCompileTimeException {
        File sourceFile = CompilerUtils.getSourceFile(jclass, true);
        return new GenXWorkModuleConfigModel(sourceFile, jclass, getEnv(), getFCSourceFileInfo(), true, getDiagnostics());
    }

    protected AnnotationGrammar getControllerGrammar() {
        return new JpfControllerGrammar();
    }

    private class JpfControllerGrammar
            extends ControllerGrammar {

        public JpfControllerGrammar() {
            super(PageFlowChecker.this.getEnv(), PageFlowChecker.this.getDiagnostics(),
                    PageFlowChecker.this.getRuntimeVersionChecker(), PageFlowChecker.this.getFCSourceFileInfo());
            addMemberType(NESTED_ATTR, new AnnotationMemberType(null, this));
            addMemberType(LONGLIVED_ATTR, new AnnotationMemberType(null, this));
        }
    }
}
