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
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;

import java.util.Iterator;
import java.util.List;

public class TilesDefinitionsConfigsType extends WebappPathType {

    public TilesDefinitionsConfigsType(String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                                       FlowControllerInfo fcInfo) {
        super(false, requiredRuntimeVersion, parentGrammar, fcInfo);
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        List values = (List) value.getValue();

        if (values != null) {
            for (Iterator ii = values.iterator(); ii.hasNext();) {
                AnnotationValue pathValue = (AnnotationValue) ii.next();

                if (pathValue != null) {
                    String filePath = (String) pathValue.getValue();
                    if (filePath == null || filePath.length() == 0 || filePath.charAt(0) != '/') {
                        addError(value, "error.absolute-path-required-for-tiles-def");
                    }
                }

                super.onCheck(valueDecl, pathValue, parentAnnotations, classMember, annotationArrayIndex);
            }
        }

        return null;
    }

    protected boolean checkAnyExtension() {
        return true;
    }
}
