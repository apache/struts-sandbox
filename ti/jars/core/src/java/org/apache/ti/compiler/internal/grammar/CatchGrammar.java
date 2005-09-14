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
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


public class CatchGrammar
        extends BaseFlowControllerGrammar
        implements JpfLanguageConstants {

    private static String[][] MUTUALLY_EXCLUSIVE_ATTRS = {{PATH_ATTR, METHOD_ATTR}};
    private static String[][] REQUIRED_ATTRS = {{TYPE_ATTR}, {PATH_ATTR, METHOD_ATTR}};

    private String _annotationRootName;


    public CatchGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, String requiredRuntimeVersion,
                        RuntimeVersionChecker runtimeVersionChecker, String annotationRootName,
                        FlowControllerInfo fcInfo) {
        super(env, diags, requiredRuntimeVersion, runtimeVersionChecker, fcInfo);

        _annotationRootName = annotationRootName;   // the parent of the list of @Jpf.Catch annotations.
        addMemberType(METHOD_ATTR, new CatchTagMethodType());
        AnnotationMemberType typeAttrType =
                new UniqueValueType(CATCHES_ATTR, false, false, null, this,
                        new TypeNameType(THROWABLE_CLASS_NAME, false, null, this));
        addMemberType(TYPE_ATTR, typeAttrType);
        addMemberType(PATH_ATTR, new ForwardToExternalPathType(new WebappPathOrActionType(false, null, this, fcInfo), null, this));
        addMemberType(MESSAGE_ATTR, new AnnotationMemberType(null, this));
        addMemberType(MESSAGE_KEY_ATTR, new AnnotationMemberType(null, this));
    }

    public String[][] getMutuallyExclusiveAttrs() {
        return MUTUALLY_EXCLUSIVE_ATTRS;
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }

    /**
     * @param checkResults map of member-name (String) -> result-from-checking (Object)
     * @return a result (any Object) that will be passed back to the parent checker.  May be null</code>.
     */
    protected Object onEndCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                MemberDeclaration classMember, Map checkResults) {
        MethodDeclaration handlerMethod = (MethodDeclaration) checkResults.get(METHOD_ATTR);
        DeclaredType exceptionType = (DeclaredType) checkResults.get(TYPE_ATTR);

        //
        // If either of these are null, then there was another already-reported error (e.g., type was unresolved).
        //
        if (handlerMethod == null || exceptionType == null) {
            return null;
        }

        //
        // Make sure the given handler method can catch the right kind of exception.
        //
        ParameterDeclaration[] parameters = handlerMethod.getParameters();

        //
        // If the method's arguments are wrong in any way, don't worry about it -- the exception-handler checker will
        // report an error.
        //
        if (parameters.length > 0) {
            TypeInstance handledExceptionType = parameters[0].getType();

            if (! CompilerUtils.isAssignableFrom(handledExceptionType, CompilerUtils.getDeclaration(exceptionType))) {
                addError(annotation, "error.incompatible-exception-handler", handlerMethod.getSimpleName(),
                        CompilerUtils.getDeclaration(exceptionType).getQualifiedName());
            }
        }

        return null;
    }

    private class CatchTagMethodType
            extends MemberMethodType {

        public CatchTagMethodType() {
            super(EXCEPTION_HANDLER_TAG_NAME, "error.unresolved-exception-handler", null, CatchGrammar.this);
        }

        /**
         * Derived classes can plug in here to do additional checks.
         */
        protected void checkMethod(MethodDeclaration methodBeingChecked, AnnotationValue value,
                                   AnnotationInstance[] parentAnnotations, MemberDeclaration classMember) {
            //
            // Make sure the current entity (class or action method) doesn't have two @Jpf.Catch annotations
            // that refer to methods with duplicate @Jpf.Forwards.
            //
            Collection catches =
                    CompilerUtils.getAnnotationArrayValue(classMember, _annotationRootName, CATCHES_ATTR, true);
            TypeDeclaration outerType = CompilerUtils.getOuterClass(classMember);

            if (catches == null) {
                return;
            }

            for (Iterator ii = catches.iterator(); ii.hasNext();) {
                AnnotationInstance catchAnnotation = (AnnotationInstance) ii.next();
                //
                // Find the method referred to in this annotation.  If we can't find it, do nothing -- this
                // will get caught elsewhere in the checking.
                //
                String methodName = CompilerUtils.getString(catchAnnotation, METHOD_ATTR, false);

                if (methodName.length() > 0 && ! methodName.equals(methodBeingChecked.getSimpleName())) {
                    MethodDeclaration otherMethod = findMethod(methodName, outerType);

                    if (otherMethod != null) {
                        //
                        // Look through this other method's forwards.  None may have the same name (and different path)
                        // as the current one.
                        //
                        Collection otherForwards =
                                CompilerUtils.getAnnotationArrayValue(otherMethod, EXCEPTION_HANDLER_TAG_NAME,
                                        FORWARDS_ATTR, false);

                        for (Iterator i2 = otherForwards.iterator(); i2.hasNext();) {
                            AnnotationInstance otherForward = (AnnotationInstance) i2.next();
                            String otherForwardName = CompilerUtils.getString(otherForward, NAME_ATTR, true);
                            String otherForwardPath = CompilerUtils.getString(otherForward, PATH_ATTR, true);
                            String otherFwdNavigateTo =
                                    CompilerUtils.getEnumFieldName(otherForward, NAVIGATE_TO_ATTR, true);

                            Collection forwards =
                                    CompilerUtils.getAnnotationArrayValue(methodBeingChecked,
                                            EXCEPTION_HANDLER_TAG_NAME,
                                            FORWARDS_ATTR, false);

                            for (Iterator i3 = forwards.iterator(); i3.hasNext();) {
                                AnnotationInstance forward = (AnnotationInstance) i3.next();
                                String forwardName = CompilerUtils.getString(forward, NAME_ATTR, true);
                                String forwardPath = CompilerUtils.getString(forward, PATH_ATTR, true);
                                String fwdNavigateTo = CompilerUtils.getEnumFieldName(forward, NAVIGATE_TO_ATTR, true);

                                if (forwardName != null && forwardName.equals(otherForwardName)) {
                                    if ((forwardPath == null || ! forwardPath.equals(otherForwardPath))
                                            && (fwdNavigateTo == null || ! fwdNavigateTo.equals(otherFwdNavigateTo))) {
                                        addError(value, "error.duplicate-exception-handler-forwards",
                                                new Object[]{methodBeingChecked.getSimpleName(), methodName, forwardName});
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

