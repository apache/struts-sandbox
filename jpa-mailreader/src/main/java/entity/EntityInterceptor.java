package entity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class EntityInterceptor extends AbstractInterceptor {

    private String entityInvoke(ActionInvocation invocation) throws Exception {
        String result = null;
        EntityAware myAction;
        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityManagerHelper.beginTransaction();
        myAction = (EntityAware) invocation.getAction();
        myAction.setManager(manager);
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