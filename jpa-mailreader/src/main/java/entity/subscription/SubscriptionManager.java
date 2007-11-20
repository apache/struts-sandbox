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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import entity.EntityManagerHelper;
import entity.EntityManagerSuperclass;

/**
 * <p>
 * Custom persistence operations involving the <code>Subscription</code>
 * object.
 * <p>
 */
public class SubscriptionManager extends EntityManagerSuperclass implements
        SubscriptionManagerInterface {

    // ---- METHODS ----

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#create(entity.Subscription)
     */
    public Subscription create(Subscription value) {
        Subscription result = (Subscription) createEntity(value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#delete(entity.Subscription)
     */
    public void delete(Subscription value) throws Exception {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(value);
            value.getUser().removeSubscription(value);
            manager.remove(value);
            transaction.commit();
        } catch (Exception e) {
            EntityManagerHelper.log(DELETE_ERROR, e);
            throw new PersistenceException(e);
        } finally {
            if ((transaction != null) && transaction.isActive()) {
                transaction.rollback();
            }
            manager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#find(java.lang.String)
     */
    public Subscription find(String value) {
        Subscription result = (Subscription) findEntity(Subscription.class,
                value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#findByName(java.lang.String)
     */
    public Subscription findByName(String value) {
        Subscription result = (Subscription) findEntityByName(
                Subscription.FIND_BY_HOST, Subscription.HOST, value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#hasId(entity.Subscription)
     */
    public boolean hasId(Subscription value) {
        return entityHasId(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.ISubscriptionManager#update(entity.Subscription)
     */
    public void update(Subscription value) throws Exception {
        updateEntity(value);
    }
}
