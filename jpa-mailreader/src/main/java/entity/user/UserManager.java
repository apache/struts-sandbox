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

import entity.EntityManagerSuperclass;

/**
 * <p>
 * Custom CRUD operations involving the <code>User</code> object.
 * <p>
 * 
 */
public class UserManager extends EntityManagerSuperclass implements
        UserManagerInterface {

    // --- METHODS ----

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#create(entity.User)
     */
    public User create(User value) {
        User result = (User) createEntity(value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#find(java.lang.String)
     */
    public User find(String value) {
        User result = (User) findEntity(User.class, value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#findByName(java.lang.String)
     */
    public User findByName(String value) {
        User result = (User) findEntityByName(User.FIND_BY_NAME, User.NAME,
                value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#hasId(entity.User)
     */
    public boolean hasId(User value) {
        return entityHasId(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#update(entity.User)
     */
    public void update(User value) throws Exception {
        updateEntity(value);
    }
}
