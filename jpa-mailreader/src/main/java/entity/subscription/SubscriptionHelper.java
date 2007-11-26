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
import javax.persistence.PersistenceException;

/**
 * <p>
 * Facade providing custom operations involving the <code>Subscription</code>
 * object.
 * </p>
 */
@WebService
public interface SubscriptionHelper {

    /**
     * <p>
     * Provide the current count of
     * <p>
     * Subscription
     * </p>
     * objects for all Users.
     * </p>
     * 
     * @return current count of
     *         <p>
     *         Subscription
     *         </p>
     *         objects for all Users
     * @throws PersistenceException
     */
    int count() throws PersistenceException;

    /**
     * <p>
     * Insert the specified <code>Subscription</code> into the persistent
     * database.
     * </p>
     * 
     * @param value
     *            Subscription to insert
     */
    Subscription create(Subscription value);

    /**
     * <p>
     * Merge changes to the specified Subscription object into the persistance
     * database.
     * </p>
     * 
     * @param value
     *            Subscription instance to delete
     */
    Subscription delete(Subscription value) throws Exception;

    /**
     * <p>
     * Retrieve the <code>Subscription</code> matching the specified host, if
     * any; otherwise, return <code>null</code>.
     * </p>
     * 
     * @param value
     *            Host to match
     */
    Subscription find(String value);

    /**
     * <p>
     * Retrieve the <code>Subscription</code> matching the specified host id,
     * if any; otherwise, return <code>null</code>.
     * </p>
     * 
     * @param id
     *            Subscription id to match
     */
    Subscription findByName(String value);

    /**
     * <p>
     * Merge changes to the specified Subscription object into the persistance
     * database.
     * </p>
     * 
     * @param user
     *            Copy of Subscription instance to match and update
     */
    Subscription update(Subscription value) throws Exception;

}