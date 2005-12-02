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
import org.apache.ti.compiler.internal.AnnotationMemberType;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;

import java.util.HashMap;
import java.util.Map;


/**
 * Supports deprecated values and values that require particular runtime versions.
 */
public class EnumType
        extends AnnotationMemberType {

    /**
     * map of enum-val (String) -> required-runtime-version (String, may be null)
     */
    private Map _enumRequiredRuntimeVersions = null;

    /**
     * Map of deprecated-value (String) -> error message key (String) *
     */
    private Map _deprecatedValues;


    public EnumType(String[][] enumValues, String[][] deprecatedValues, String requiredRuntimeVersion,
                    AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);

        if (deprecatedValues != null) {
            _deprecatedValues = new HashMap();

            for (int i = 0; i < deprecatedValues.length; ++i) {
                String[] valueAndDiagnostic = deprecatedValues[i];
                assert valueAndDiagnostic.length == 2;
                _deprecatedValues.put(valueAndDiagnostic[0], valueAndDiagnostic[1]);
            }
        }

        if (enumValues != null) {
            _enumRequiredRuntimeVersions = new HashMap();

            for (int i = 0; i < enumValues.length; i++) {
                String[] valueAndRequiredRuntimeVersion = enumValues[i];
                assert valueAndRequiredRuntimeVersion.length == 2;
                String enumValue = valueAndRequiredRuntimeVersion[0];
                String enumValRequiredRuntimeVersion = valueAndRequiredRuntimeVersion[1];

                if (enumValRequiredRuntimeVersion != null) {
                    _enumRequiredRuntimeVersions.put(enumValue, enumValRequiredRuntimeVersion);
                }
            }
        }
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        //
        // Check deprecated values.
        //
        String val = CompilerUtils.getEnumFieldName(member);
        String errorKey = _deprecatedValues != null ? (String) _deprecatedValues.get(val) : null;

        if (errorKey != null) {
            addWarning(member, errorKey, val);
        }

        //
        // Check required runtime version for enum values.
        //
        String ver = _enumRequiredRuntimeVersions != null ? (String) _enumRequiredRuntimeVersions.get(val) : null;

        if (ver != null) {
            getParentGrammar().getRuntimeVersionChecker().checkRuntimeVersion(
                    ver, member, getParentGrammar().getDiagnostics(), "error.required-runtime-version-enumval", null);
        }

        return super.onCheck(valueDecl, member, parentAnnotations, classMember, annotationArrayIndex);
    }

}
