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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import entity.EntityManagerHelper;
import entity.EntityManagerSuperclass;

/**
 * <p>
 * Custom persistence operations involving the <code>Protocol</code> object.
 * <p>
 */
public class ProtocolManager extends EntityManagerSuperclass implements
        ProtocolManagerInterface {

    /*
     * (non-Javadoc)
     * 
     * @see entity.IProtocolManager#find(java.lang.String)
     */
    public Protocol find(String value) {
        Protocol result = (Protocol) findEntity(Protocol.class, value);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IProtocolManager#findAll()
     */
    public List<Protocol> findAll() {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            List<Protocol> protocols = em.createNamedQuery(Protocol.FIND_ALL)
                    .getResultList();
            if (protocols == null) {
                protocols = new ArrayList<Protocol>();
            }
            return protocols;
        } finally {
            em.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IProtocolManager#findAllAsMap()
     */
    public Map findAllAsMap() {
        List<Protocol> items = findAll();
        Map map = new LinkedHashMap(items.size());
        for (Protocol item : items) {
            map.put(String.valueOf(item.getId()), item.getDescription());
        }
        return map;
    }

}
