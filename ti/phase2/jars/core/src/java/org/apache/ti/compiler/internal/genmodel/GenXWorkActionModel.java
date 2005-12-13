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
import org.apache.ti.compiler.internal.model.XWorkActionModel;
import org.apache.ti.compiler.internal.model.XWorkResultModel;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class GenXWorkActionModel
        extends XWorkActionModel
        implements JpfLanguageConstants {

    public GenXWorkActionModel(Declaration sourceElement, GenXWorkModuleConfigModel parentApp, ClassDeclaration jclass) {
        super(parentApp);

        MethodDeclaration methodDecl = (MethodDeclaration) sourceElement;
        AnnotationInstance actionAnnotation = CompilerUtils.getActionAnnotation(methodDecl, jclass, parentApp.getEnv());
        init(getActionName(sourceElement), actionAnnotation, parentApp, jclass);

        // Get the form class from the method argument.
        setFormBeanType(getFormBean(sourceElement, parentApp));

        setComment("Generated from action method " + sourceElement.getSimpleName());
    }

    protected GenXWorkActionModel(String actionName, AnnotationInstance ann, GenXWorkModuleConfigModel parentApp, ClassDeclaration jclass) {
        super(parentApp);
        init(actionName, ann, parentApp, jclass);
    }

    private void init(String actionName, AnnotationInstance annotation, GenXWorkModuleConfigModel parentApp, ClassDeclaration jclass) {
        setName(actionName);

        //
        // loginRequired
        //
        Boolean loginRequired = CompilerUtils.getBoolean(annotation, LOGIN_REQUIRED_ATTR, true);
        if (loginRequired == null) {
            loginRequired = parentApp.getFlowControllerInfo().getMergedControllerAnnotation().isLoginRequired();
        }
        if (loginRequired != null) setLoginRequired(loginRequired.booleanValue());

        //
        // prevent-double-submit
        //
        Boolean preventDoubleSubmit = CompilerUtils.getBoolean(annotation, PREVENT_DOUBLE_SUBMIT_ATTR, false);
        setPreventDoubleSubmit(preventDoubleSubmit.booleanValue());

        //
        // readOnly
        //
        Boolean readOnly = CompilerUtils.getBoolean(annotation, READONLY_ATTR, true);
        if (readOnly == null) {
            readOnly = Boolean.valueOf(parentApp.getFlowControllerInfo().getMergedControllerAnnotation().isReadOnly());
        }
        setReadonly(readOnly.booleanValue());

        //
        // rolesAllowed -- avoid setting this if loginRequired is explicitly false.
        //
        if (loginRequired == null || loginRequired.booleanValue()) setRolesAllowed(annotation, jclass, parentApp);

        //
        // type (delegating Action class, with the FlowController as parameter)
        //
        setType(FLOW_CONTROLLER_ACTION_CLASS);

        //
        // form bean member -- the page-flow-scoped form referenced by the action (a member variable)
        //
        setFormBeanMember(CompilerUtils.getString(annotation, USE_FORM_BEAN_ATTR, true));

        //
        // forwards
        //
        getForwards(annotation, jclass, parentApp);

        //
        // validationErrorForward -- the forward used when validation fails
        //
        AnnotationInstance validateErrFwd = CompilerUtils.getAnnotation(annotation, VALIDATION_ERROR_FORWARD_ATTR, true);
        boolean doValidation = false;
        if (validateErrFwd != null) {
            XWorkResultModel fwd = new GenXWorkResultModel(parentApp, validateErrFwd, jclass, " (validationErrorForward)");
            addForward(fwd);
            setValidationErrorForward(fwd.getName());
            doValidation = true;
        }

        //
        // validate
        //
        Boolean explicitDoValidation = CompilerUtils.getBoolean(annotation, DO_VALIDATION_ATTR, true);
        setValidate(explicitDoValidation != null ? explicitDoValidation.booleanValue() : doValidation);

        //
        // exception-catches
        //
        GenXWorkExceptionHandlerModel.addCatches(annotation, this, jclass, parentApp);
    }

    private void setRolesAllowed(AnnotationInstance annotation, ClassDeclaration jclass, GenXWorkModuleConfigModel parentApp) {
        List rolesAllowed = CompilerUtils.getStringArray(annotation, ROLES_ALLOWED_ATTR, true);
        List classLevelRA = parentApp.getFlowControllerInfo().getMergedControllerAnnotation().getRolesAllowed();
        Iterator it = null;

        if (rolesAllowed != null && classLevelRA != null) {
            HashSet merged = new HashSet();
            for (Iterator ii = rolesAllowed.iterator(); ii.hasNext();) {
                String role = (String) ii.next();
                merged.add(role);
            }
            for (Iterator ii = classLevelRA.iterator(); ii.hasNext();) {
                String classLevelRole = (String) ii.next();
                merged.add(classLevelRole);
            }
            it = merged.iterator();
        } else if (rolesAllowed != null) {
            it = rolesAllowed.iterator();
        } else if (classLevelRA != null) {
            it = classLevelRA.iterator();
        }

        if (it != null && it.hasNext()) {
            StringBuffer rolesAllowedStr = new StringBuffer((String) it.next());

            while (it.hasNext()) {
                rolesAllowedStr.append(',').append(((String) it.next()).trim());
            }

            setRoles(rolesAllowedStr.toString());
        }
    }

    protected static String getActionName(Declaration sourceElement) {
        return sourceElement.getSimpleName();
    }

    /**
     * @return the Struts name of the form bean.
     */
    protected String getFormBean(Declaration sourceElement, GenXWorkModuleConfigModel parentApp) {
        assert sourceElement instanceof MethodDeclaration : sourceElement.getClass().getName();
        ParameterDeclaration[] params = ((MethodDeclaration) sourceElement).getParameters();
        String formBeanName = null;

        if (params.length > 0) {
            assert params.length == 1 : params.length;  // checker should catch this
            TypeInstance paramType = CompilerUtils.getGenericBoundsType(params[0].getType());
            formBeanName = addFormBean(paramType, parentApp);
        }

        return formBeanName;
    }

    protected String addFormBean(TypeInstance paramType, GenXWorkModuleConfigModel parentApp) {
        paramType = CompilerUtils.getGenericBoundsType(paramType);
        assert paramType instanceof DeclaredType : paramType.getClass().getName();  // checker should enforce this
        TypeDeclaration decl = CompilerUtils.getDeclaration((DeclaredType) paramType);
        return parentApp.addFormBean(decl, this);
    }

    protected void getForwards(AnnotationInstance annotation, ClassDeclaration jclass, GenXWorkModuleConfigModel parentApp) {
        GenXWorkResultModel.addForwards(annotation, this, jclass, parentApp, null);

        // Infer a "success" forward if there wasn't one specified.
        XWorkResultModel successFwd = getForward("success");
        if (successFwd == null) {
            String defaultFileExt = parentApp.getDefaultFileExtension();
            successFwd = new XWorkResultModel("success", getName() + defaultFileExt, parentApp);
            successFwd.setComment("(implicit)");
            addForward(successFwd);
        }
    }
}
