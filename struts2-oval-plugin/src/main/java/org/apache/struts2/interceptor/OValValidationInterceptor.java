/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import net.sf.oval.Validator;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.context.MethodReturnValueContext;

import java.util.List;
import java.lang.reflect.Method;

import org.apache.struts2.validation.Profiles;
import org.apache.commons.lang.xwork.StringUtils;

/*
 This interceptor provides validation using the OVal validation framework
 */
public class OValValidationInterceptor extends MethodFilterInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(OValValidationInterceptor.class);

    private final static String VALIDATE_PREFIX = "validate";
    private final static String ALT_VALIDATE_PREFIX = "validateDo";

    private boolean alwaysInvokeValidate = true;
    private boolean programmatic = true;

    /**
     * Determines if {@link com.opensymphony.xwork2.Validateable}'s <code>validate()</code> should be called,
     * as well as methods whose name that start with "validate". Defaults to "true".
     *
     * @param programmatic <tt>true</tt> then <code>validate()</code> is invoked.
     */
    public void setProgrammatic(boolean programmatic) {
        this.programmatic = programmatic;
    }

    /**
     * Determines if {@link com.opensymphony.xwork2.Validateable}'s <code>validate()</code> should always
     * be invoked. Default to "true".
     *
     * @param alwaysInvokeValidate <tt>true</tt> then <code>validate()</code> is always invoked.
     */
    public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
        this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
    }

    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        ActionProxy proxy = invocation.getProxy();
        ValueStack valueStack = invocation.getStack();
        String methodName = proxy.getMethod();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Validating [#0/#1] with method [#2]", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName(), methodName);
        }

        //OVal vallidatio (no XML yet)
        performOValValidation(action, valueStack, methodName);

        //Validatable.valiedate() and validateX()
        performProgrammaticValidation(invocation, action);

        return invocation.invoke();
    }

    private void performProgrammaticValidation(ActionInvocation invocation, Object action) throws Exception {
        if (action instanceof Validateable && programmatic) {
            // keep exception that might occured in validateXXX or validateDoXXX
            Exception exception = null;

            Validateable validateable = (Validateable) action;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking validate() on action [#0]", validateable.toString());
            }

            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(
                        invocation,
                        new String[]{VALIDATE_PREFIX, ALT_VALIDATE_PREFIX});
            }
            catch (Exception e) {
                // If any exception occurred while doing reflection, we want
                // validate() to be executed
                LOG.warn("An exception occured while executing the prefix method", e);
                exception = e;
            }


            if (alwaysInvokeValidate) {
                validateable.validate();
            }

            if (exception != null) {
                // rethrow if something is wrong while doing validateXXX / validateDoXXX
                throw exception;
            }
        }
    }

    protected void performOValValidation(Object action, ValueStack valueStack, String methodName) throws NoSuchMethodException {
        Validator validator = new Validator();
        //if the method is annotated with a @Profiles annotation, use those profiles
        Method method = action.getClass().getMethod(methodName, new Class[0]);
        if (method != null) {
            Profiles profiles = method.getAnnotation(Profiles.class);
            if (profiles != null) {
                String[] profileNames = profiles.value();
                if (profileNames != null && profileNames.length > 0) {
                    validator.disableAllProfiles();
                    if (LOG.isDebugEnabled())
                        LOG.debug("Enabling profiles [#0]", StringUtils.join(profileNames, ","));
                    for (String profileName : profileNames)
                        validator.enableProfile(profileName);
                }
            }
        }

        //perform validation
        List<ConstraintViolation> violations = validator.validate(action);
        if (violations != null) {
            ValidatorContext validatorContext = new DelegatingValidatorContext(action);
            for (ConstraintViolation violation : violations) {
                //translate message
                String key = violation.getMessage();

                //push the validator into the stack
                valueStack.push(violation);
                String message = key;
                try {
                    message = validatorContext.getText(key);
                } finally {
                    valueStack.pop();
                }

                if (isActionError(violation))
                    validatorContext.addActionError(message);
                else {
                    String className = action.getClass().getName();
                    //the default OVal message shows the field name as ActionClass.fieldName
                    message = StringUtils.removeStart(message, className + ".");
                    validatorContext.addFieldError(extractFieldName(violation), message);
                }
            }
        }
    }

    /**
     * Get field name, used to add the validation error to fieldErrors
     */
    protected String extractFieldName(ConstraintViolation violation) {
        OValContext context = violation.getContext();
        if (context instanceof FieldContext) {
            return ((FieldContext) context).getField().getName();
        } else if (context instanceof MethodReturnValueContext) {
            String methodName = ((MethodReturnValueContext) context).getMethod().getName();
            if (methodName.startsWith("get")) {
                return StringUtils.uncapitalize(StringUtils.removeStart(methodName, "get"));
            } else if (methodName.startsWith("is")) {
                return StringUtils.uncapitalize(StringUtils.removeStart(methodName, "is"));
            }

            return methodName;
        }

        return violation.getCheckName();
    }

    /**
     * Decide if a violation should be added to the fieldErrors or actionErrors
     */
    protected boolean isActionError(ConstraintViolation violation) {
        return false;
    }
}
