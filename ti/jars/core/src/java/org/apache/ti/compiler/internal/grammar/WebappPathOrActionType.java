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
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;

import java.util.Collection;
import java.util.Iterator;

public class WebappPathOrActionType
        extends WebappPathType {

    public WebappPathOrActionType(boolean pathMustBeRelative, String requiredRuntimeVersion,
                                  AnnotationGrammar parentGrammar, FlowControllerInfo fcInfo) {
        super(pathMustBeRelative, requiredRuntimeVersion, parentGrammar, fcInfo);
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        String stringValue = (String) value.getValue();
        checkAction(stringValue, value, classMember);
        return super.onCheck(valueDecl, value, parentAnnotations, classMember, annotationArrayIndex);
    }

    protected void checkAction(String stringValue, AnnotationValue annValue, MemberDeclaration classMember) {
        if (stringValue.endsWith(ACTION_EXTENSION_DOT) && stringValue.indexOf('/') == -1) {
            TypeDeclaration outerType = CompilerUtils.getOuterClass(classMember);

            if (outerType != null)    // null in some error conditions
            {
                int extensionPos = stringValue.lastIndexOf(ACTION_EXTENSION_DOT);
                String actionMethodName = stringValue.substring(0, extensionPos);
                FlowControllerInfo fcInfo = getFlowControllerInfo();
                boolean foundIt = actionExists(actionMethodName, outerType, null, getEnv(), fcInfo, true);

                if (! foundIt && actionMethodName.length() > 0) {
                    //
                    // Check for a Shared Flow action reference of the form <shared-flow-name>..
                    //
                    int dot = actionMethodName.indexOf('.');

                    if (dot != -1 && dot < actionMethodName.length() - 1) {
                        String sharedFlowName = actionMethodName.substring(0, dot);
                        TypeDeclaration sfTypeDecl = (TypeDeclaration) getFlowControllerInfo().getSharedFlowTypes().get(sharedFlowName);

                        if (sfTypeDecl != null) {
                            actionMethodName = actionMethodName.substring(dot + 1);
                            foundIt = actionExists(actionMethodName, sfTypeDecl, null, getEnv(), fcInfo, true);
                        }
                    }
                }

                if (! foundIt) {
                    if (doFatalError()) {
                        addError(annValue, "error.action-not-found", actionMethodName);
                    } else {
                        addWarning(annValue, "warning.action-not-found", actionMethodName);
                    }
                }
            }
        }
    }

    public static boolean actionExists(String actionName, TypeDeclaration type, AnnotationInstance annotationToIgnore,
                                       AnnotationProcessorEnvironment env, FlowControllerInfo fcInfo,
                                       boolean checkInheritedActions) {
        if (! (type instanceof ClassDeclaration)) {
            return false;
        }

        ClassDeclaration classDecl = (ClassDeclaration) type;

        do {
            //
            // First look through the action methods.
            //
            MethodDeclaration[] methods = classDecl.getMethods();

            for (int i = 0; i < methods.length; i++) {
                MethodDeclaration method = methods[i];
                if (method.getSimpleName().equals(actionName)
                        && CompilerUtils.getAnnotation(method, ACTION_TAG_NAME) != null) {
                    return true;
                }
            }

            //
            // Next, look through the simple actions (annotations).
            //
            Collection simpleActionAnnotations =
                    CompilerUtils.getAnnotationArrayValue(classDecl, CONTROLLER_TAG_NAME, SIMPLE_ACTIONS_ATTR, true);

            if (simpleActionAnnotations != null) {
                for (Iterator i = simpleActionAnnotations.iterator(); i.hasNext();) {
                    AnnotationInstance ann = (AnnotationInstance) i.next();
                    String name = CompilerUtils.getString(ann, NAME_ATTR, false);

                    if (actionName.equals(name)
                            && ! CompilerUtils.annotationsAreEqual(ann, annotationToIgnore, false, env)) {
                        return true;
                    }
                }
            }

            ClassType superType = classDecl.getSuperclass();
            classDecl = superType != null ? superType.getClassTypeDeclaration() : null;
        } while (checkInheritedActions && classDecl != null);


        return false;
    }
}
