package entity;

import javax.persistence.EntityManager;
import junit.framework.TestCase;

public class EntityManagerHelperTest extends TestCase {

    public void testGetEntityManager() {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        assertNotNull(manager);
        EntityManager manager2 = EntityManagerHelper.getEntityManager();
        assertEquals(manager, manager2);
    }

}
