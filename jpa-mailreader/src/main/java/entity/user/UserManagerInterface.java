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

public interface UserManagerInterface {

    /**
     * <p>
     * Insert the specified <code>User</code> (and any associated child
     * <code>Subscription</code>s) into the persistent database.
     * </p>
     * 
     * @param value
     *            User instance to be added
     */
    public abstract User create(User value);

    /**
     * <p>
     * Retrieve the <code>User</code> matching the specified username, if any;
     * otherwise, return <code>null</code>.
     * </p>
     * 
     * @param value
     *            Username to match
     */
    public abstract User find(String value);

    /**
     * <p>
     * Retrieve the <code>User</code> matching the specified username, if any;
     * otherwise, return <code>null</code>.
     * </p>
     * 
     * @param value
     *            Username to match
     */
    public abstract User findByName(String value);

    /**
     * <p>
     * Determine if the <code>User</code> object has been assigned an ID
     * value.
     * 
     * @param value
     *            User object to examine
     * @return True if the User object has an ID value
     */
    public abstract boolean hasId(User value);

    /**
     * <p>
     * Persist changes to the specified User object into the persistance
     * database.
     * </p>
     * 
     * @param value
     *            Copy of User instance to match and update
     */
    public abstract void update(User value) throws Exception;

}