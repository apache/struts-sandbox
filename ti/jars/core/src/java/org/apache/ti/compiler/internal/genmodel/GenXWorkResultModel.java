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

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.model.XWorkResultContainer;
import org.apache.ti.compiler.internal.model.XWorkResultModel;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GenXWorkResultModel
        extends XWorkResultModel
        implements JpfLanguageConstants {

    public GenXWorkResultModel(GenXWorkModuleConfigModel parent, AnnotationInstance annotation, ClassDeclaration jclass,
                               String commentSuffix) {
        super(parent);

        setName(CompilerUtils.getString(annotation, NAME_ATTR, true));
        setRedirect(CompilerUtils.getBoolean(annotation, REDIRECT_ATTR, false).booleanValue());
        if (CompilerUtils.getBoolean(annotation, EXTERNAL_REDIRECT_ATTR, false).booleanValue()) {
            setExternalRedirect(true);
        }

        //
        // outputFormBean/outputFormBeanType
        //
        DeclaredType outputFormType = CompilerUtils.getDeclaredType(annotation, OUTPUT_FORM_BEAN_TYPE_ATTR, true);
        String outputFormMember = CompilerUtils.getString(annotation, OUTPUT_FORM_BEAN_ATTR, true);
        if (outputFormMember != null) {
            FieldDeclaration field = CompilerUtils.getClassField(jclass, outputFormMember, null);
            assert outputFormType == null;  // checker should catch this
            assert field != null;           // checker should catch this
            assert field.getType() instanceof DeclaredType : field.getType().getClass().getName(); // checker enforces
            outputFormType = (DeclaredType) field.getType();
        }
        setOutputFormBeanMember(outputFormMember);
        setOutputFormBeanType(outputFormType != null ? CompilerUtils.getLoadableName(outputFormType) : null);

        //
        // path, tilesDefinition, navigateTo, returnAction (mutually exclusive)
        //
        String returnAction = CompilerUtils.getString(annotation, RETURN_ACTION_ATTR, true);
        String navigateTo = CompilerUtils.getEnumFieldName(annotation, NAVIGATE_TO_ATTR, true);
        String tilesDefinition = CompilerUtils.getString(annotation, TILES_DEFINITION_ATTR, true);
        String path = CompilerUtils.getString(annotation, PATH_ATTR, true);
        String action = CompilerUtils.getString(annotation, ACTION_ATTR, true);

        if (action != null) {
            assert path == null;  // checker should enforce this
            path = action + ACTION_EXTENSION_DOT;
        }

        if (returnAction != null) {
            assert navigateTo == null;
            assert tilesDefinition == null;
            assert path == null;
            setReturnAction(returnAction);
        } else if (navigateTo != null) {
            assert tilesDefinition == null;
            assert path == null;

            if (navigateTo.equals(NAVIGATE_TO_CURRENT_PAGE_STR)) {
                setReturnToPage(0);
            } else if (navigateTo.equals(NAVIGATE_TO_PREVIOUS_PAGE_STR)) {
                setReturnToPage(1);
            } else if (navigateTo.equals(NAVIGATE_TO_PREVIOUS_ACTION_STR)) {
                setReturnToAction();
            } else {
                assert false : "unknown value for navigateTo: \"" + navigateTo + '"';
            }

            boolean restore = CompilerUtils.getBoolean(annotation, RESTORE_QUERY_STRING_ATTR, false).booleanValue();
            setRestoreQueryString(restore);
        } else if (tilesDefinition != null) {
            assert path == null;
            setPath(tilesDefinition);    // set the tilesDefinition as the path -- the runtime expects it there
        } else {
            assert path != null;      // checker should enforce this

            //
            // Translate our relative-path convention (normal) to the Struts convention, which adds a '/'
            // to any module-relative path.
            //
            if (! path.startsWith("/")) {
                //
                // If this annotation came from a base class, and if inheritLocalPaths is true on the Controller
                // annotation, then make the path relative to the base class module.
                //
                TypeDeclaration containingType = annotation.getContainingType();
                if (! CompilerUtils.typesAreEqual(jclass, containingType)
                        && parent.getFlowControllerInfo().getMergedControllerAnnotation().isInheritLocalPaths()) {
                    // TODO: when we no longer support Struts 1.1, we can simply use the 'module' property
                    // to make this forward relative to the base class module.
                    path = CompilerUtils.getPathRelativeToPackage(path, containingType.getPackage());
                    setInheritedPath(true);
                }
            }

            setPath(path);
        }

        addActionOutputs(annotation, jclass);

        if (commentSuffix != null) {
            setComment("forward \"" + getName() + '"' + commentSuffix);  // @TODO I18N the comment
        }
    }

    static void addForwards(AnnotationInstance annotation, XWorkResultContainer container, ClassDeclaration jclass,
                            GenXWorkModuleConfigModel strutsApp, String commentSuffix) {
        List forwards = CompilerUtils.getAnnotationArray(annotation, FORWARDS_ATTR, true);
        addForwards(forwards, container, jclass, strutsApp, commentSuffix);
    }

    static void addForwards(Collection forwards, XWorkResultContainer container,
                            ClassDeclaration jclass, GenXWorkModuleConfigModel strutsApp, String commentSuffix) {
        if (forwards != null) {
            for (Iterator ii = forwards.iterator(); ii.hasNext();) {
                AnnotationInstance ann = (AnnotationInstance) ii.next();
                container.addForward(new GenXWorkResultModel(strutsApp, ann, jclass, commentSuffix));
            }
        }
    }

    protected void addActionOutputs(AnnotationInstance annotation, ClassDeclaration jclass) {
        List actionOutputs =
                CompilerUtils.getAnnotationArray(annotation, ACTION_OUTPUTS_ATTR, true);

        if (actionOutputs != null) {
            for (Iterator ii = actionOutputs.iterator(); ii.hasNext();) {
                AnnotationInstance ann = (AnnotationInstance) ii.next();
                addActionOutput(new GenActionOutputModel(ann, jclass));
            }
        }
    }
}
