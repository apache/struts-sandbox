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
package entity.user;

import javax.persistence.PersistenceException;
import javax.jws.WebService;

/**
 * <p>
 * Facade providing custom operations involving the <code>User</code> object.
 * </p>
 * 
 */
@WebService
public interface UserHelper {

    /**
     * <p>
     * Provide the current count of
     * <p>
     * User
     * </p>
     * objects.
     * </p>
     * 
     * @return current count of
     *         <p>
     *         User
     *         </p>
     *         objects
     * @throws PersistenceException
     */
    int count() throws PersistenceException;

    /**
     * <p>
     * Insert the specified <code>User</code> into the persistent database.
     * </p>
     * 
     * @param value
     *            User to insert
     */
    void create(User value);

    /**
     * <p>
     * Merge changes to the specified User object into the persistance database.
     * </p>
     * 
     * @param value
     *            User instance to delete
     */
    void delete(User value) throws Exception;

    /**
     * <p>
     * Retrieve the <code>User</code> matching the specified user ID, if any;
     * otherwise, return <code>null</code>.
     * </p>
     * 
     * @param value
     *            ID to match
     */
    User find(String value);

    /**
     * <p>
     * Retrieve the <code>User</code> matching the specified username, if any;
     * otherwise, return <code>null</code>.
     * </p>
     * 
     * @param value
     *            Username to match
     */
    User findByName(String value);

    /**
     * <p>
     * Merge changes to the specified User object into the persistance database.
     * </p>
     * 
     * @param user
     *            Copy of User instance to match and update
     */
    void update(User value) throws Exception;

}