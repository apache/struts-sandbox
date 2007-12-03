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
import javax.persistence.PersistenceException;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * <p>
 * "Single transaction per view" Interceptor for a JPA application.
 * </p>
 * <p>
 * This class creates an EntityManager for each thread, and begins a transation.
 * Other JPA components can utilize the EntityManager for persistence
 * operations, without opening or committing transactions. At the end of the
 * Action's lifecyle, this interceptor will commit the transaction, log any
 * errors, and attempt a rollback, if needed.
 * </p>
 * <p>
 * Exceptions are logged and rethrown. An application utilizing this
 * interceptors should also utilize declarative exception handling, being sure
 * to catch PersistenceExceptions.
 * </p>
 */
public class EntityInterceptor extends AbstractInterceptor {

    private String entityInvoke(ActionInvocation invocation) throws Exception {
        String result = null;
        EntityAware myAction;
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityManagerHelper.beginTransaction();
        myAction = (EntityAware) invocation.getAction();
        myAction.setEntityManager(manager);
        try {
            result = invocation.invoke();
            EntityManagerHelper.commit();
        } catch (PersistenceException e) {
            EntityManagerHelper.logError("PersistenceException in Action: "
                    + myAction.toString(), e);
            try {
                EntityManagerHelper.rollback();
            } catch (Throwable t) {
                EntityManagerHelper.logError("Exception during rollback", t);
            }
            throw e;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
        return result;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;
        Object action = invocation.getAction();
        boolean isEntityAware = (action instanceof EntityAware);
        if (!isEntityAware) {
            result = invocation.invoke();
        } else {
            result = entityInvoke(invocation);
        }
        return result;
    }
}