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
package entity;

/**
 * <p>
 * Signal that a password has expired and needs to be changed before principal
 * can be authorized for any other operation.
 * </p>
 */
public class ExpiredPasswordException extends Exception {

    /**
     * <p>
     * Instantiate a new <code>ExpiredPasswordException</code>, utilizing the
     * specified username.
     * </p>
     * 
     * @param username
     *            Username whose password has expired
     */
    public ExpiredPasswordException(String username) {
        super("Password for " + username + " has expired.");
    }

    /**
     * <p>
     * Instantiate a new <code>ExpiredPasswordException</code>.
     * </p>
     * 
     */
    public ExpiredPasswordException() {
        super("Your password has expired.");
    }

}
