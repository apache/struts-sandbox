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
package action.user.subscription;

import java.util.Map;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

import entity.protocol.ProtocolHelperImpl;
import entity.protocol.ProtocolHelper;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionHelperImpl;
import entity.subscription.SubscriptionHelper;
import entity.user.User;

@Results( {
        @Result(name = Index.CANCEL, value = Index.UPDATE_INPUT, type = ServletActionRedirectResult.class, params = {
                Index.NAMESPACE, Index.NS_USER, Index.USER,
                Index.SUBSCRIPTION_USER_USERNAME }),
        @Result(name = Index.ERROR, value = Index.UPDATE_INPUT, type = ServletActionRedirectResult.class, params = {
                Index.NAMESPACE, Index.NS_USER, Index.USER,
                Index.SUBSCRIPTION_USER_USERNAME }),
        @Result(name = Index.SUCCESS, value = Index.UPDATE_INPUT, type = ServletActionRedirectResult.class, params = {
                Index.NAMESPACE, Index.NS_USER, Index.USER,
                Index.SUBSCRIPTION_USER_USERNAME }) })
@Validation()
public class Index extends action.user.Index implements Preparable {

    protected static final String ERROR_HOST_UNIQUE = "error.host.unique";

    static final String ERROR_CREDENTIALS_MISMATCH = "error.password.mismatch";

    protected SubscriptionHelper manager;
    private ProtocolHelper protocolManager;

    private Map<String, String> protocols;

    public Map<String, String> getProtocols() {
        return protocols;
    }

    public void setProtocols(Map<String, String> value) {
        protocols = value;
    }

    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription value) {
        subscription = value;
    }

    private boolean validateHost(Subscription value) {
        String name = value.getHost();
        Subscription foundValue = manager.findByName(name);
        boolean isNameInUse = (foundValue != null);
        if (isNameInUse)
            addFieldError(Subscription.NAME, getText(ERROR_HOST_UNIQUE));
        return NotErrors();
    }

    private boolean validateUser(Subscription value) {
        boolean needUser = (value.getUser() == null);
        if (needUser) {
            User defaultUser = getUser();
            if (defaultUser == null) {
                addFieldError(Subscription.NAME,
                        getText(ERROR_CREDENTIALS_MISMATCH));
            } else {
                value.setUser(getUser());
            }
        }
        return NotErrors();
    }

    protected void create() throws Exception {
        Subscription value = getSubscription();
        boolean isValid = ((validateHost(value)) && (validateUser(value)));
        if (isValid)
            manager.create(value);
    }

    protected void delete() throws Exception {
        Subscription value = getSubscription();
        User user = value.getUser();
        user.removeSubscription(value);
        manager.delete(value);
    }

    protected void update() throws Exception {
        Subscription result = getSubscription();
        boolean exists = (null != manager.find(result.getId()));
        if (exists) {
            manager.update(result);
        } else {
            addActionError(getText(ERROR_CREDENTIALS_MISMATCH));
        }
    }

    @Validations(requiredStrings = {
            @RequiredStringValidator(fieldName = "subscription.host", key = "error.host.required", message = "", shortCircuit = true),
            @RequiredStringValidator(fieldName = "subscription.username", key = "error.username.required", message = "", shortCircuit = true),
            @RequiredStringValidator(fieldName = "subscription.password", key = "error.password.required", message = "", shortCircuit = true) })
    @RequiredFieldValidator(fieldName = "subscription.protocol", key = "error.type.required", message = "", shortCircuit = true)
    public String execute() throws Exception {

        String input = getInput();

        if (CREATE.equals(input))
            create();

        if (UPDATE.equals(input))
            update();

        if (DELETE.equals(input))
            delete();

        if (hasErrors())
            return INPUT;

        return SUCCESS;

    }

    public void prepare() {
        setProtocols(protocolManager.findAllAsMap());
    }

    /**
     * <p>
     * Instantiate default instance.
     * </p>
     */
    public Index() {
        manager = new SubscriptionHelperImpl();
        protocolManager = new ProtocolHelperImpl();
    }

    /**
     * <p>
     * Instantiate instance using specified <code>ISubscriptionManager</code>
     * and <code>IProtocolManager</code> .
     * </p>
     * 
     * @param manager
     *            IUserManager instance
     */
    public Index(SubscriptionHelper manager, ProtocolHelper protocolManager) {
        this.manager = manager;
        this.protocolManager = protocolManager;
    }

}
