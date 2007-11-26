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
package entity.protocol;

import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

/**
 * <p>
 * Custom operations involving the <code>Protocol</code> object.
 * <p>
 */
public interface ProtocolHelper {

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
     * Retrieve the <code>Protocol</code> with the specified id, if any;
     * otherwise, return <code>null</code>.
     * </p>
     * 
     * @param id
     *            Protocol id to look up
     */
    Protocol find(String value);

    /**
     * <p>
     * Retrieve a <code>List</code> of the valid <code>Protocol</code>s for
     * this application. If no valid protocols are defined, a zero length list
     * will be returned.
     * </p>
     */
    List<Protocol> findAll();

    /**
     * <p>
     * Retrieve a <code>Map</code> of the valid <code>Protocol</code>s for
     * this application. If no valid protocols are defined, an empty map will be
     * returned.
     * </p>
     */
    Map<String, String> findAllAsMap();

}