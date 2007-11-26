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

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import entity.EntityManagerHelper;
import entity.EntityHelper;

/**
 * <p>
 * Default JPA implementation of <code>ProtocolHelper</code>.
 * </p>
 */
@WebService(serviceName = "protocol", endpointInterface = "entity.protocol.ProtocolHelper")
public class ProtocolHelperImpl extends EntityHelper implements ProtocolHelper {

    public int count() throws PersistenceException {
        Long count = (Long) singleResult(Protocol.COUNT, null, null);
        int result = count.intValue();
        return result;
    }

    public Protocol find(String value) {
        Protocol result = (Protocol) readEntity(Protocol.class, value);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Protocol> findAll() {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        List<Protocol> protocols = manager.createNamedQuery(Protocol.FIND_ALL)
                .getResultList();
        if (protocols == null) {
            protocols = new ArrayList<Protocol>();
        }
        return protocols;
    }

    public Map<String, String> findAllAsMap() {
        List<Protocol> items = findAll();
        Map<String, String> map = new LinkedHashMap<String, String>(items
                .size());
        for (Protocol item : items) {
            map.put(String.valueOf(item.getId()), item.getDescription());
        }
        return map;
    }

}
