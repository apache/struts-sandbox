/*
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
package action.user;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;

import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Results( {
        @Result(name = Index.CANCEL, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.NAMESPACE, Index.NONE }),
        @Result(name = Index.SUCCESS, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.USER, Index.USER_USERNAME }) })
@Validation()
public class Create extends Index {

    // FIXME: Can the email validations by applied to getUser?
    @Validations(requiredStrings = {
            @RequiredStringValidator(fieldName = "user.username", key = "error.username.required", message = ""),
            @RequiredStringValidator(fieldName = "user.fullName", key = "error.fullName.required", message = ""),
            @RequiredStringValidator(fieldName = "user.fromAddress", key = "error.fromAddress.required", message = ""),
            @RequiredStringValidator(fieldName = "user.password1", key = "error.password.required", message = ""),
            @RequiredStringValidator(fieldName = "user.password2", key = "error.password2.required", message = "") }, stringLengthFields = { @StringLengthFieldValidator(fieldName = "user.password1", key = "error.password.length", message = "", trim = true, minLength = "4", maxLength = "12") }, emails = {
            @EmailValidator(fieldName = "user.fromAddress", key = Index.ERROR_FROM_ADDRESS_FORMAT, message = Index.NONE),
            @EmailValidator(fieldName = "user.replyTo", key = Index.ERROR_REPLY_TO_ADDRESS_FORMAT, message = Index.NONE) })
    @StringLengthFieldValidator(fieldName = "user.password1", key = "error.password.length", message = "", trim = true, minLength = "4", maxLength = "12")
    @ExpressionValidator(key = "error.password.match", message = "", expression = "password1 eq password2")
    public String execute() throws Exception {
        if (!CREATE.equals(getInput()))
            return INPUT;
        create();
        return (hasErrors()) ? INPUT : SUCCESS;
    }

    public String input() throws Exception {
        setInput(CREATE);
        return super.input();
    }
}
