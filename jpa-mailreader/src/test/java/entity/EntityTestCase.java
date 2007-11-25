package entity;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

public class EntityTestCase extends TestCase {

    protected EntityManager manager;

    public void setUp() throws Exception {
        super.setUp();
        manager = EntityManagerHelper.getEntityManager();
        EntityManagerHelper.beginTransaction();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        EntityManagerHelper.commit();
        EntityManagerHelper.closeEntityManager();
    }

    public void testTrue() throws Exception {
        assertTrue(true);
    }

}
