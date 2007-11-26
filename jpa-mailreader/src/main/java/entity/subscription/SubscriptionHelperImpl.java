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

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import entity.EntityHelper;
import entity.EntityManagerHelper;

/**
 * <p>
 * Default JPA implementation of <code>UserHelper</code>.
 * </p>
 */
@WebService(serviceName = "subscription", endpointInterface = "entity.subscription.SubscriptionHelper")
public class SubscriptionHelperImpl extends EntityHelper implements
        SubscriptionHelper {

    public int count() throws PersistenceException {
        Long count = (Long) singleResult(Subscription.COUNT, null, null);
        int result = count.intValue();
        return result;
    }

    public void create(Subscription value) {
        createEntity(value);
    }

    public void delete(Subscription value) throws Exception {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        manager.merge(value);
        value.getUser().removeSubscription(value);
        manager.remove(value);
    }

    public Subscription find(String value) {
        Subscription result = (Subscription) readEntity(Subscription.class,
                value);
        return result;
    }

    public Subscription findByName(String value) {
        Subscription result = (Subscription) singleResult(
                Subscription.FIND_BY_NAME, Subscription.NAME, value);
        return result;
    }

    public void update(Subscription value) throws Exception {
        updateEntity(value);
    }
}
