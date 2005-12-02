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
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.Collection;
import java.util.Iterator;


public class SimpleActionGrammar
        extends ActionGrammar {

    private static String[][] REQUIRED_ATTRS =
            {
                    {NAME_ATTR},
                    {PATH_ATTR, TILES_DEFINITION_ATTR, RETURN_ACTION_ATTR, NAVIGATE_TO_ATTR, FORWARD_REF_ATTR, ACTION_ATTR}
            };

    private static String[][] MUTUALLY_EXCLUSIVE_ATTRS =
            {
                    {USE_FORM_BEAN_ATTR, USE_FORM_BEAN_TYPE_ATTR}
            };


    private ForwardGrammar _forwardGrammar;


    public SimpleActionGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, RuntimeVersionChecker rvc,
                               FlowControllerInfo fcInfo) {
        super(env, diags, rvc, fcInfo);

        addMemberType(FORWARD_REF_ATTR, new ForwardRefType());
        addMemberArrayGrammar(CONDITIONAL_FORWARDS_ATTR,
                new SimpleActionForwardGrammar(env, diags, null, rvc, fcInfo));

        //
        // The rest of the attributes are checked by ForwardGrammar.
        //
        _forwardGrammar = new SimpleActionGrammarPart2();
    }

    public String[][] getMutuallyExclusiveAttrs() {
        return MUTUALLY_EXCLUSIVE_ATTRS;
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        String name = CompilerUtils.getString(annotation, NAME_ATTR, false);

        TypeDeclaration outerClass = CompilerUtils.getOuterClass(classMember);
        if (WebappPathOrActionType.actionExists(name, outerClass, annotation, getEnv(), getFlowControllerInfo(), false)) {
            if (! UniqueValueType.alreadyAddedErrorForValue(classMember, annotation, name, getEnv())) {
                addError(annotation, "error.duplicate-action", new Object[]{name});
            }
        }

        //
        // A simple action is really like a combination of an action and a forward.  We extend ActionGrammar and
        // then delegate to a ForwardGrammar here.
        //
        _forwardGrammar.check(annotation, parentAnnotations, classMember);

        return super.onBeginCheck(annotation, parentAnnotations, classMember);
    }

    protected String getActionName(AnnotationInstance annotation, MemberDeclaration classMember) {
        return CompilerUtils.getString(annotation, NAME_ATTR, false);
    }

    protected TypeInstance getFormBeanType(AnnotationInstance annotation, MemberDeclaration classMember) {
        // for a SimpleAction, the form bean type is wholly defined by the useFormBean attribute.
        return getUseFormBeanType(annotation, classMember);
    }

    private static class SimpleActionForwardGrammar
            extends ForwardGrammar {

        private static String[][] REQUIRED_SIMPLEACTION_ATTRS =
                {
                        {CONDITION_ATTR},
                        {PATH_ATTR, TILES_DEFINITION_ATTR, RETURN_ACTION_ATTR, NAVIGATE_TO_ATTR, ACTION_ATTR}
                };

        public SimpleActionForwardGrammar(AnnotationProcessorEnvironment env, Diagnostics diags,
                                          String requiredRuntimeVersion, RuntimeVersionChecker runtimeVersionChecker,
                                          FlowControllerInfo fcInfo) {
            super(env, diags, requiredRuntimeVersion, runtimeVersionChecker, fcInfo);
        }

        public String[][] getRequiredAttrs() {
            return REQUIRED_SIMPLEACTION_ATTRS;
        }

        protected AnnotationMemberType getNameType() {
            return new SimpleActionForwardNameType();
        }

        private class SimpleActionForwardNameType
                extends ForwardNameType {

            public SimpleActionForwardNameType() {
                super(CONDITIONAL_FORWARDS_ATTR);
            }
        }
    }

    private class ForwardRefType
            extends AnnotationMemberType {

        public ForwardRefType() {
            super(null, SimpleActionGrammar.this);
        }


        public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                              AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                              int annotationArrayIndex) {
            Collection forwards =
                    getFlowControllerInfo().getMergedControllerAnnotation().getForwards();
            String forwardName = (String) member.getValue();

            for (Iterator ii = forwards.iterator(); ii.hasNext();) {
                AnnotationInstance forwardAnn = (AnnotationInstance) ii.next();
                if (forwardName.equals(CompilerUtils.getString(forwardAnn, NAME_ATTR, true))) return null;
            }

            // TODO: comment
            if (forwardName.equals("_auto")) return null;

            addError(member, "error.unresolvable-global-forward", forwardName);
            return null;
        }
    }

    private class SimpleActionGrammarPart2
            extends ForwardGrammar {

        public SimpleActionGrammarPart2() {
            super(SimpleActionGrammar.this.getEnv(), SimpleActionGrammar.this.getDiagnostics(), null,
                    SimpleActionGrammar.this.getRuntimeVersionChecker(),
                    SimpleActionGrammar.this.getFlowControllerInfo());
        }

        protected AnnotationMemberType getNameType() {
            // We don't want to use the normal name type, which checks for duplicate names among *forwards*.
            return new AnnotationMemberType(null, SimpleActionGrammar.this);
        }

        public String[][] getRequiredAttrs() {
            return REQUIRED_ATTRS;
        }
    }
}
