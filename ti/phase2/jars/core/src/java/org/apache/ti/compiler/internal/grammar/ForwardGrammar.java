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

import org.apache.ti.compiler.internal.AnnotationMemberType;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class ForwardGrammar
        extends BaseFlowControllerGrammar {

    private static final String[][] NAVIGATE_TO_VALS = new String[][]
            {
                    {NAVIGATE_TO_CURRENT_PAGE_STR, VERSION_8_SP2_STRING},
                    {NAVIGATE_TO_PREVIOUS_PAGE_STR, VERSION_8_SP2_STRING},
                    {NAVIGATE_TO_PREVIOUS_ACTION_STR, VERSION_8_SP2_STRING},
            };

    private static String[][] MUTUALLY_EXCLUSIVE_ATTRS =
            {
                    {PATH_ATTR, TILES_DEFINITION_ATTR, RETURN_ACTION_ATTR, NAVIGATE_TO_ATTR, ACTION_ATTR},
                    {OUTPUT_FORM_BEAN_TYPE_ATTR, OUTPUT_FORM_BEAN_ATTR},
                    {REDIRECT_ATTR, EXTERNAL_REDIRECT_ATTR}
            };

    private static String[][] REQUIRED_ATTRS =
            {
                    {NAME_ATTR},
                    {PATH_ATTR, TILES_DEFINITION_ATTR, RETURN_ACTION_ATTR, NAVIGATE_TO_ATTR, ACTION_ATTR}
            };

    private static String[][] ATTR_DEPENDENCIES =
            {
                    {REDIRECT_ATTR, PATH_ATTR, NAVIGATE_TO_ATTR, ACTION_ATTR},
                    {EXTERNAL_REDIRECT_ATTR, PATH_ATTR},
                    {RESTORE_QUERY_STRING_ATTR, NAVIGATE_TO_ATTR}
            };


    public ForwardGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, String requiredRuntimeVersion,
                          RuntimeVersionChecker runtimeVersionChecker, FlowControllerInfo fcInfo) {
        super(env, diags, requiredRuntimeVersion, runtimeVersionChecker, fcInfo);

        addMemberType(NAME_ATTR, getNameType());
        addMemberType(OUTPUT_FORM_BEAN_TYPE_ATTR, new TypeNameType(null, false, null, this));
        addMemberType(OUTPUT_FORM_BEAN_ATTR, new MemberFieldType(null, null, this));
        addMemberType(RETURN_ACTION_ATTR, new JavaIdentifierType(null, this, new char[]{'.'}));
        addMemberType(PATH_ATTR, new ExternalPathOrActionType(false, null, this, fcInfo));
        addMemberType(ACTION_ATTR, new ValidActionType(null, this, fcInfo));
        addMemberType(TILES_DEFINITION_ATTR, new AnnotationMemberType(null, this));
        addMemberType(REDIRECT_ATTR, new AnnotationMemberType(null, this));
        addMemberType(EXTERNAL_REDIRECT_ATTR, new AbsolutePathType(null, this));
        addMemberType(NAVIGATE_TO_ATTR, new EnumType(NAVIGATE_TO_VALS, null, null, this));
        addMemberType(RESTORE_QUERY_STRING_ATTR, new AnnotationMemberType(null, this));

        addMemberArrayGrammar(ACTION_OUTPUTS_ATTR, new ActionOutputGrammar(env, diags, runtimeVersionChecker));
    }

    protected AnnotationMemberType getNameType() {
        return new ForwardNameType();
    }

    public String[][] getMutuallyExclusiveAttrs() {
        return MUTUALLY_EXCLUSIVE_ATTRS;
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }

    public String[][] getAttrDependencies() {
        return ATTR_DEPENDENCIES;
    }

    protected void onCheckMember(AnnotationTypeElementDeclaration memberDecl, AnnotationValue value,
                                 AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                 MemberDeclaration classMember) {
        String valueName = memberDecl.getSimpleName();
        boolean isReturnAction = valueName.equals(RETURN_ACTION_ATTR);

        if (isReturnAction) {
            if (! getFlowControllerInfo().isNested()) {
                addError(value, "error.only-valid-in-nested", new Object[]{valueName});
            }
        }

        if (valueName.equals(ACTION_OUTPUTS_ATTR) && ((List) value.getValue()).size() > 0) {
            if (CompilerUtils.getBoolean(annotation, REDIRECT_ATTR, false).booleanValue()) {
                addError(value, "error.action-outputs-with-redirect", REDIRECT_ATTR);
            }

            String path = CompilerUtils.getString(annotation, PATH_ATTR, true);
            if (path != null && CompilerUtils.isAbsoluteURI(path)) {
                addError(value, "error.action-outputs-with-absolute-uri", PATH_ATTR);
            }
        }

        //
        // If this is a return-action, store its info in the FlowControllerInfo (which is eventually provided to tools).
        //
        if (isReturnAction) {
            TypeDeclaration outerType = CompilerUtils.getOuterClass(classMember);
            TypeInstance formBeanType =
                    getFlowControllerInfo().addReturnAction((String) value.getValue(), annotation, outerType);

            if (formBeanType != null && ! (formBeanType instanceof DeclaredType)) {
                addError(annotation, "error.action-invalid-form-bean-type", formBeanType.toString());
            }
        }
    }

    protected class ForwardNameType
            extends UniqueValueType {

        public ForwardNameType() {
            this(FORWARDS_ATTR);
        }

        protected ForwardNameType(String memberGroupName) {
            super(memberGroupName, false, false, null, ForwardGrammar.this);
        }

        /**
         * @return a List of AnnotationInstance
         */
        protected List getAdditionalAnnotationsToCheck(MemberDeclaration classMember) {
            //
            // curEntity will be either the pageflow class or an action method, where we'll look
            // for @Jpf.Catch annotations that refer to exception-handler methods, which also have
            // forwards that get rolled onto this entity.
            //
            List additionalEntities = new ArrayList();

            TypeDeclaration outerType = CompilerUtils.getOuterClass(classMember);

            Collection classLevelCatches =
                    getFlowControllerInfo().getMergedControllerAnnotation().getCatches();
            addAdditionalAnnotationsToCheck(classLevelCatches, outerType, additionalEntities);

            if (classMember instanceof MethodDeclaration) {
                Collection methodLevelCatches =
                        CompilerUtils.getAnnotationArrayValue(classMember, ACTION_TAG_NAME, CATCHES_ATTR, true);
                addAdditionalAnnotationsToCheck(methodLevelCatches, outerType, additionalEntities);
            }

            return additionalEntities;
        }

        private void addAdditionalAnnotationsToCheck(Collection catches,
                                                     TypeDeclaration outerType, List additionalEntities) {
            //
            // For each of the given @Jpf.Catch annotations, find the matching @Jpf.ExceptionHandler method and
            // add all of its @Jpf.Forward annotations to the list.
            //
            if (catches != null) {
                for (Iterator ii = catches.iterator(); ii.hasNext();) {
                    AnnotationInstance catchAnnotation = (AnnotationInstance) ii.next();
                    String methodName = CompilerUtils.getString(catchAnnotation, METHOD_ATTR, false);

                    if (methodName.length() > 0) {
                        MethodDeclaration[] allMethods = CompilerUtils.getClassMethods(outerType, null);

                        for (int i = 0; i < allMethods.length; i++) {
                            MethodDeclaration method = allMethods[i];
                            AnnotationInstance exHandlerAnnotation =
                                    CompilerUtils.getAnnotation(method, EXCEPTION_HANDLER_TAG_NAME);

                            if (exHandlerAnnotation != null && method.getSimpleName().equals(methodName)) {
                                Collection forwardAnnotations =
                                        CompilerUtils.getAnnotationArray(exHandlerAnnotation, FORWARDS_ATTR, false);

                                for (Iterator i3 = forwardAnnotations.iterator(); i3.hasNext();) {
                                    AnnotationInstance forwardAnnotation = (AnnotationInstance) i3.next();
                                    additionalEntities.add(forwardAnnotation);
                                }
                            }
                        }
                    }

                }
            }
        }

        protected String getErrorMessageExtraInfo() {
            return CATCH_TAG_NAME;
        }


        protected boolean allowExactDuplicates() {
            return true;
        }
    }
}
