package entity;

import java.util.Random;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

public class EntityTestCase extends TestCase {

    protected EntityManager manager;
    protected boolean rollback = false;
    protected String base;
    protected Random generator;

    protected String nextBase() {
        int r = generator.nextInt();
        return String.valueOf(r);
    }

    protected boolean isNotEmpty(String value) {
        return (value != null) && (value.length() > 0);
    }

    public void setUp() throws Exception {
        super.setUp();
        generator = new Random();
        base = nextBase();
        manager = EntityManagerHelper.getEntityManager();
        EntityManagerHelper.beginTransaction();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        if (rollback)
            EntityManagerHelper.rollback();
        EntityManagerHelper.commit();
        EntityManagerHelper.closeEntityManager();
    }

    public void testTrue() throws Exception {
        assertTrue(true);
    }

}
