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

import entity.EntityService;

/**
 * <p>
 * Default JPA implementation of <code>UserHelper</code>.
 * </p>
 */
// @WebService(serviceName = "user", endpointInterface = "entity.user.UserSession")
public class UserServiceImpl extends EntityService implements UserService {

    public int count() throws PersistenceException {
        Long count = (Long) singleResult(User.COUNT, null, null);
        int result = count.intValue();
        return result;
    }

    public User create(User value) throws PersistenceException {
        return (User) createEntity(value);
    }

    public User delete(User value) throws PersistenceException {
        return (User) deleteEntity(value);
    }

    public User find(String value) {
        User result = (User) readEntity(User.class, value);
        return result;
    }

    public User findByName(String value) {
        User result = (User) singleResult(User.FIND_BY_NAME, User.NAME, value);
        return result;
    }

    public User update(User value) throws PersistenceException {
        return (User) updateEntity(value);
    }
}
