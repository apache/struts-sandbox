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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.UUID;

/**
 * <p>
 * Custom CRUD operations involving the <code>User</code> object.
 * <p>
 * 
 */
public class EntityManagerSuperclass {

    // --- STATICS ----

    /**
     * <p>
     * Error message to post when create fails.
     * </p>
     */
    public static final String CREATE_ERROR = "Exception in create()";

    /**
     * <p>
     * Error message to post when delete fails.
     * </p>
     */
    public static final String DELETE_ERROR = "Exception in delete()";

    /**
     * <p>
     * Error message to post when update fails.
     * </p>
     */
    public static final String UPDATE_ERROR = "Exception in update()";

    // --- METHODS ----

    public Object createEntity(EntitySuperclass value) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            String id = UUID.randomUUID().toString();
            value.setId(id);
            manager.persist(value);
            transaction.commit();
        } catch (Exception e) {
            EntityManagerHelper.log(CREATE_ERROR, e);
            throw new PersistenceException(e);
        } finally {
            if ((transaction != null) && transaction.isActive()) {
                transaction.rollback();
            }
            manager.close();
        }
        return value;
    }

    public void delete(Object value) throws Exception {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(value);
            manager.remove(value);
            transaction.commit();
        } catch (Exception e) {
            EntityManagerHelper.log(DELETE_ERROR, e);
            throw new PersistenceException(e);
        } finally {
            if ((transaction != null) && transaction.isActive()) {
                transaction.rollback();
            }
            manager.close();
        }
    }

    @SuppressWarnings("unchecked")
    public Object findEntity(Class entity, String id) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        Object result = null;
        try {
            result = manager.find(entity, id);
            return result;
        } catch (NoResultException e) {
            return null;
        } finally {
            manager.close();
        }
    }

    public Object findEntityByName(String namedQuery, String parameterName,
            String value) {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        Object result = null;
        try {
            Query query = manager.createNamedQuery(namedQuery);
            query.setParameter(parameterName, value);
            result = query.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        } finally {
            manager.close();
        }
    }

    public boolean entityHasId(EntitySuperclass value) {
        if (value == null)
            return false;
        String id = value.getId();
        boolean result = ((id != null) && (id.length() > 0));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see entity.IUserManager#update(entity.User)
     */
    public void updateEntity(Object value) throws Exception {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(value);
            transaction.commit();
        } catch (Exception e) {
            EntityManagerHelper.log(UPDATE_ERROR, e);
            throw new PersistenceException(e);
        } finally {
            if ((transaction != null) && transaction.isActive()) {
                transaction.rollback();
            }
            manager.close();
        }
    }
}
