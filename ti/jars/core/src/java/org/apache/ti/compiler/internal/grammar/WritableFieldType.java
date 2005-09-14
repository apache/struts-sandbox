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
package org.apache.ti.compiler.internal.grammar;

import org.apache.ti.compiler.internal.AnnotationGrammar;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;

public class WritableFieldType
        extends MemberFieldType
        implements JpfLanguageConstants {

    private String _attrName;


    public WritableFieldType(String requiredSuperclassName, String attrName, String requiredRuntimeVersion,
                             AnnotationGrammar parentGrammar) {
        super(requiredSuperclassName, requiredRuntimeVersion, parentGrammar);
        _attrName = attrName;
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        //
        // Look for the "readOnly" attribute on the current action method or the current class.  If it's there,
        // then this attribute cannot refer to the given field.
        //
        Boolean readonlyValue = null;

        if (classMember instanceof MethodDeclaration) {
            readonlyValue = CompilerUtils.getBooleanValue(classMember, ACTION_TAG_NAME, READONLY_ATTR, true);
        }

        if (readonlyValue == null) {
            BaseFlowControllerGrammar fcGrammar = (BaseFlowControllerGrammar) getParentGrammar();
            readonlyValue = Boolean.valueOf(fcGrammar.getFlowControllerInfo().getMergedControllerAnnotation().isReadOnly());
        }

        if (readonlyValue != null && readonlyValue.booleanValue()) {
            addError(value, "error.readonly-writable-field-value", _attrName);
        }

        return super.onCheck(valueDecl, value, parentAnnotations, classMember, annotationArrayIndex);
    }
}
