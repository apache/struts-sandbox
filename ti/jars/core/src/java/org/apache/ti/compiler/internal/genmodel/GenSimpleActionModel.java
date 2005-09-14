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
import org.apache.ti.compiler.internal.model.XWorkResultModel;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.Iterator;
import java.util.List;


public class GenSimpleActionModel
        extends GenXWorkActionModel
        implements JpfLanguageConstants {

    public GenSimpleActionModel(AnnotationInstance annotation, GenXWorkModuleConfigModel parentApp, ClassDeclaration jclass) {
        super(CompilerUtils.getString(annotation, NAME_ATTR, true), annotation, parentApp, jclass);

        setSimpleAction(true);
        addForwards(annotation, parentApp, jclass);

        String formMember = getFormBeanMember();
        if (formMember != null) {
            FieldDeclaration field = CompilerUtils.findField(jclass, formMember);
            assert field != null;  // checker should prevent this
            setFormBeanType(addFormBean(field.getType(), parentApp));
        } else {
            setReadonly(true);     // can't modify member state; mark as read-only

            TypeInstance formBeanType = CompilerUtils.getTypeInstance(annotation, USE_FORM_BEAN_TYPE_ATTR, true);

            if (formBeanType != null) {
                setFormBeanType(addFormBean(formBeanType, parentApp));
            }
        }

        StringBuilder comment = new StringBuilder("Generated from @");
        comment.append(ANNOTATION_INTERFACE_PREFIX);
        comment.append(annotation.getAnnotationType().getAnnotationTypeDeclaration().getSimpleName());
        comment.append("(name=");
        comment.append(getName());
        comment.append(")");
        setComment(comment.toString());
    }

    protected String getFormBean(Declaration sourceElement, GenXWorkModuleConfigModel parentApp) {
        return null;
    }

    protected void getForwards(AnnotationInstance annotation, ClassDeclaration jclass, GenXWorkModuleConfigModel parentApp) {
    }

    private void addForwards(AnnotationInstance annotation, GenXWorkModuleConfigModel parentApp, ClassDeclaration jclass) {
        //
        // First add the default forward -- the one that is parsed from the simple action annotation itself.
        // But, if the "forwardRef" attribute was given, simply use the one referenced.
        //
        String forwardRef = CompilerUtils.getString(annotation, FORWARD_REF_ATTR, true);

        if (forwardRef == null) {
            forwardRef = DEFAULT_SIMPLE_ACTION_FORWARD_NAME;
            XWorkResultModel fwd = new SimpleActionXWorkResult(forwardRef, parentApp, annotation, jclass);

            if (fwd.getPath() != null || fwd.isNavigateTo() || fwd.isNestedReturn()) {
                addForward(fwd);
            }
        }

        setDefaultForwardName(forwardRef);

        List conditionalFwdAnnotations =
                CompilerUtils.getAnnotationArray(annotation, CONDITIONAL_FORWARDS_ATTR, true);

        if (conditionalFwdAnnotations != null) {
            int anonCount = 0;

            for (Iterator ii = conditionalFwdAnnotations.iterator(); ii.hasNext();) {
                AnnotationInstance conditionalFwdAnnotation = (AnnotationInstance) ii.next();
                XWorkResultModel conditionalFwd = new SimpleActionXWorkResult(parentApp, conditionalFwdAnnotation, jclass);
                String expression = CompilerUtils.getString(conditionalFwdAnnotation, CONDITION_ATTR, true);
                assert expression != null;

                if (conditionalFwd.getName() == null) conditionalFwd.setName("_anon" + ++anonCount);
                addForward(conditionalFwd);
                addConditionalForward(expression, conditionalFwd.getName());
            }
        }
    }

    private static class SimpleActionXWorkResult extends GenXWorkResultModel {

        public SimpleActionXWorkResult(GenXWorkModuleConfigModel parent, AnnotationInstance annotation, ClassDeclaration jclass) {
            super(parent, annotation, jclass, null);
        }

        public SimpleActionXWorkResult(String name, GenXWorkModuleConfigModel parent, AnnotationInstance annotation, ClassDeclaration jclass) {
            super(parent, annotation, jclass, null);
            setName(name);
        }

        protected void addActionOutputs(AnnotationInstance annotation, ClassDeclaration jclass) {
            // do nothing -- there are no action outputs on simple actions
        }
    }
}

