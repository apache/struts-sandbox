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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * <p>
 * A set of generic CRUD operations that can operate on any entity in the
 * default persistence unit.
 * </p>
 * <p>
 * If an application needs only basic CRUD operations, this class (and its
 * companion the <code>EntityManagerHelper</code>) may be the only "data
 * access object" the applications needs.
 * </p>
 * <p>
 * This implementation delegates transaction management and exception handling
 * to another component, such as an Interceptor or Filter, or the setUp and
 * tearDown methods of a TestCase. See <code>EntityInterceptor</code> for an
 * example.
 * </p>
 * 
 */
public class EntityHelper {

    public Object createEntity(Object value) throws PersistenceException {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        manager.persist(value);
        return value;
    }

    public Object deleteEntity(Object value) throws PersistenceException {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        manager.merge(value);
        manager.remove(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    public List resultList(String namedQuery, String parameterName, String value) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        List result = null;
        Query query = manager.createNamedQuery(namedQuery);
        if (parameterName != null)
            query.setParameter(parameterName, value);
        try {
            result = query.getResultList();
        } catch (NoResultException e) {
            result = null;
        }
        return result;
    }

    public Object singleResult(String namedQuery, String parameterName,
            String value) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        Object result = null;
        Query query = manager.createNamedQuery(namedQuery);
        if (parameterName != null)
            query.setParameter(parameterName, value);
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Object readEntity(Class entity, String id) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        Object result = null;
        try {
            result = manager.find(entity, id);
        } catch (NoResultException e) {
            result = null;
        }
        return result;
    }

    public Object updateEntity(Object value) throws PersistenceException {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        manager.merge(value);
        return value;
    }
}
