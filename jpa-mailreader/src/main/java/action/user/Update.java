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
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Results( {
        @Result(name = Index.CANCEL, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.USER, Index.USER_USERNAME }),
        @Result(name = Index.SUCCESS, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.USER, Index.USER_USERNAME }) })
@Validation()
public class Update extends Index {

    @Validations(requiredStrings = {
            @RequiredStringValidator(fieldName = "user.username", key = "error.username.required", message = "", shortCircuit = true),
            @RequiredStringValidator(fieldName = "user.fullName", key = "error.fullname.required", message = "", shortCircuit = true),
            @RequiredStringValidator(fieldName = "user.fromAddress", key = "error.fromaddress.required", message = "", shortCircuit = true) }, emails = {
            @EmailValidator(fieldName = "user.fromAddress", key = Index.ERROR_REPLY_TO_ADDRESS_FORMAT, message = Index.NONE),
            @EmailValidator(fieldName = "user.replyTo", key = Index.ERROR_FROM_ADDRESS_FORMAT, message = Index.NONE) })
    public String execute() throws Exception {

        if (!UPDATE.equals(getInput()))
            return INPUT;

        if (!validatePasswordChange())
            return INPUT;

        update();
        return (hasErrors()) ? INPUT : SUCCESS;
    }

    public String input() throws Exception {
        setInput(UPDATE);
        return super.input();
    }
}
