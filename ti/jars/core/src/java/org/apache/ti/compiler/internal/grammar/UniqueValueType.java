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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class UniqueValueType
        extends AnnotationMemberType {

    private boolean _allowEmptyString;
    private boolean _checkDefaultValues;

    /**
     * the name of the attribute on the parent annotation that is the list of annotations to check for duplicates
     */
    private String _memberGroupName;


    public UniqueValueType(String memberGroupName, boolean allowEmptyString, boolean checkDefaultValues,
                           String requiredRuntimeVersion, AnnotationGrammar parentGrammar) {
        this(memberGroupName, allowEmptyString, checkDefaultValues, requiredRuntimeVersion, parentGrammar, null);
    }

    public UniqueValueType(String memberGroupName, boolean allowEmptyString, boolean checkDefaultValues,
                           String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                           AnnotationMemberType nextInChain) {
        super(requiredRuntimeVersion, parentGrammar, nextInChain);

        _allowEmptyString = allowEmptyString;
        _memberGroupName = memberGroupName;
        _checkDefaultValues = checkDefaultValues;
    }

    /**
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */

    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        String val = value.getValue().toString();

        if (! _allowEmptyString && val.length() == 0) {
            addError(value, "error.empty-string-not-allowed");
            return null;
        }

        if (parentAnnotations.length < 2) return null;    // invalid parents -- will be caught elsewhere
        AnnotationInstance parentElement = parentAnnotations[parentAnnotations.length - 2];
        List memberGroup = CompilerUtils.getAnnotationArray(parentElement, _memberGroupName, true);

        //
        // If memberGroup is null, then this annotation was in the wrong place, and there will be other errors.
        //
        if (memberGroup != null) {
            String valueName = valueDecl.getSimpleName();
            AnnotationInstance parentAnnotation = parentAnnotations[parentAnnotations.length - 1];
            checkForDuplicates(value, valueName, parentAnnotation, classMember, memberGroup, false,
                    annotationArrayIndex);

            //
            // Get a list of additional annotations (presumably not from the this one's parent) to check.
            //
            List additionalAnnsToCheck = getAdditionalAnnotationsToCheck(classMember);

            if (additionalAnnsToCheck != null) {
                // Check this value against the list of additional annotations.
                checkForDuplicates(value, valueName, parentAnnotation, classMember, additionalAnnsToCheck, true, -1);

                // Check for duplicates *within* within the list of additional annotations.
                for (int i = 0; i < additionalAnnsToCheck.size(); ++i) {
                    AnnotationInstance ann = (AnnotationInstance) additionalAnnsToCheck.get(i);
                    AnnotationValue valueToCheck = CompilerUtils.getAnnotationValue(ann, valueName, true);
                    checkForDuplicates(valueToCheck, valueName, ann, classMember, additionalAnnsToCheck, true, -1);
                }
            }
        }

        return null;
    }

    /**
     * Plugin point for derived class -- if there should be no duplicates across another entity too.
     *
     * @return a List of AnnotationInstance
     */
    protected List getAdditionalAnnotationsToCheck(MemberDeclaration classMember) {
        return null;
    }

    protected String getErrorMessageExtraInfo() {
        return null;
    }

    protected void checkForDuplicates(AnnotationValue member, String memberName, AnnotationInstance parentAnnotation,
                                      MemberDeclaration classMember, List annotationsToCheck,
                                      boolean includeEntityInMsg, int annotationArrayIndex) {
        Object memberValue = member.getValue();

        for (int i = 0; i < annotationsToCheck.size(); ++i) {
            AnnotationInstance annotation = (AnnotationInstance) annotationsToCheck.get(i);

            if ((annotationArrayIndex != -1 && annotationArrayIndex != i) ||
                    ! CompilerUtils.annotationsAreEqual(annotation, parentAnnotation, allowExactDuplicates(), getEnv())) {
                AnnotationValue valueToCheck =
                        CompilerUtils.getAnnotationValue(annotation, memberName, _checkDefaultValues);

                if (valueToCheck != null && ! valueToCheck.equals(member)
                        && valueToCheck.getValue().equals(memberValue)) {
                    if (alreadyAddedErrorForValue(classMember, parentAnnotation, memberValue, getEnv())) return;

                    String annotationName =
                            CompilerUtils.getDeclaration(parentAnnotation.getAnnotationType()).getSimpleName();

                    if (includeEntityInMsg) {
                        String extra = getErrorMessageExtraInfo();
                        Object[] args = new Object[]
                                {
                                        annotationName,
                                        memberName,
                                        memberValue,
                                        classMember.getSimpleName(),
                                        extra
                                };
                        addError(member, "error.duplicate-attr2", args);
                    } else {
                        addError(member, "error.duplicate-attr",
                                new Object[]{annotationName, memberName, memberValue});
                    }

                    return;
                }
            }
        }
    }

    static boolean alreadyAddedErrorForValue(MemberDeclaration classMember, AnnotationInstance parentAnn,
                                             Object memberValue, AnnotationProcessorEnvironment env) {
        // Map of String class-member-name ->
        //      [ Map of String annotation name -> Set of values for which errors were added ]
        HashMap errorsAddedRootMap = (HashMap) env.getAttribute("uniqueValueErrors");
        if (errorsAddedRootMap == null) {
            errorsAddedRootMap = new HashMap();
            env.setAttribute("uniqueValueErrors", errorsAddedRootMap);
        }

        String classMemberName = classMember.getSimpleName();
        HashMap errorsAddedByAnnotation = (HashMap) errorsAddedRootMap.get(classMemberName);
        if (errorsAddedByAnnotation == null) {
            errorsAddedByAnnotation = new HashMap();
            errorsAddedRootMap.put(classMemberName, errorsAddedByAnnotation);
        }

        String parentAnnName = parentAnn.getAnnotationType().getAnnotationTypeDeclaration().getQualifiedName();
        HashSet errorsAdded = (HashSet) errorsAddedByAnnotation.get(parentAnnName);
        if (errorsAdded == null) {
            errorsAdded = new HashSet();
            errorsAddedByAnnotation.put(parentAnnName, errorsAdded);
        }

        if (errorsAdded.contains(memberValue)) return true;

        errorsAdded.add(memberValue);
        return false;
    }

    protected boolean allowExactDuplicates() {
        return false;
    }
}
