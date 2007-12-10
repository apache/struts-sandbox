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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A generic facade that provides easy access to a JPA persistence unit using
 * static methods.
 * </p>
 * <p>
 * This static class is designed so that it can be used with any JPA
 * application.
 * </p>
 */
public class EntityManagerHelper {

    /**
     * <p>
     * Declare the persistence unit for this EntityManagerHelper ("entity").
     * </p>
     * <p>
     * This is the only setting that might need to be changed between
     * applications. Otherwise, this class can be dropped into any JPA
     * application.
     * </p>
     */
    static final String PERSISTENCE_UNIT = "entity";

    private static final EntityManagerFactory factory;
    private static final ThreadLocal<EntityManager> threadLocal;
    private static final Log logger;

    static {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        threadLocal = new ThreadLocal<EntityManager>();
        logger = LogFactory.getLog(EntityManagerHelper.class);
    }

    /**
     * <p>
     * Provide a per-thread EntityManager "singleton" instance.
     * </p>
     * <p>
     * This method can be called as many times as needed per thread, and it will
     * return the same EntityManager instance, until the manager is closed.
     * </p>
     * 
     * @return EntityManager singleton for this thread
     */
    public static EntityManager getEntityManager() {
        EntityManager manager = threadLocal.get();
        if (manager == null || !manager.isOpen()) {
            manager = factory.createEntityManager();
            threadLocal.set(manager);
        }
        return manager;
    }

    /**
     * <p>
     * Close the EntityManager and set the thread's instance to null.
     * </p>
     */
    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        threadLocal.set(null);
        if (em != null)
            em.close();
    }

    /**
     * <p>
     * Initiate a transaction for the EntityManager on this thread.
     * </p>
     * <p>
     * The Transaction will remain open until commit or closeEntityManager is
     * called.
     * </p>
     */
    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    /**
     * <p>
     * Submit the changes to the persistance layer.
     * </p>
     * <p>
     * Until commit is called, rollback can be used to undo the transaction.
     * </p>
     */
    public static void commit() {
        getEntityManager().getTransaction().commit();
    }

    /**
     * <p>
     * Create a query for the EntityManager on this thread.
     * </p>
     */
    public static Query createQuery(String query) {
        return getEntityManager().createQuery(query);
    }

    /**
     * <p>
     * Flush the EntityManager state on this thread.
     * </p>
     */
    public static void flush() {
        getEntityManager().flush();
    }

    /**
     * <p>
     * Write an error message to the logging system.
     * </p>
     */
    public static void logError(String info, Throwable ex) {
        logger.error(info, ex);
    }

    /**
     * <p>
     * Undo an uncommitted transaction, in the event of an error or other
     * problem.
     * </p>
     */
    public static void rollback() {
        getEntityManager().getTransaction().rollback();
    }

}
