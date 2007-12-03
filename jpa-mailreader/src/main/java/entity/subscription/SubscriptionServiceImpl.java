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
package entity.subscription;

import javax.persistence.PersistenceException;

import entity.EntityService;

/**
 * <p>
 * Default JPA implementation of <code>SubscriptionService</code>.
 * </p>
 */
// @WebService(serviceName = "subscription", endpointInterface =
// "entity.subscription.SubscriptionService")
public class SubscriptionServiceImpl extends EntityService implements
        SubscriptionService {

    public int count() throws PersistenceException {
        Long count = (Long) singleResult(SubscriptionImpl.COUNT, null, null);
        int result = count.intValue();
        return result;
    }

    public Subscription create(Subscription value) {
        return (Subscription) createEntity(value);
    }

    public Subscription delete(Subscription value) throws Exception {
        updateEntity(value);
        value.getUser().removeSubscription(value);
        return (Subscription) deleteEntity(value);
    }

    public Subscription find(String value) {
        Subscription result = (Subscription) readEntity(SubscriptionImpl.class,
                value);
        return result;
    }

    public Subscription findByName(String value) {
        Subscription result = (Subscription) singleResult(
                SubscriptionImpl.FIND_BY_NAME, SubscriptionImpl.NAME, value);
        return result;
    }

    public Subscription update(Subscription value) throws Exception {
        return (Subscription) updateEntity(value);
    }
}
