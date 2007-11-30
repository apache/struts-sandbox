package entity;

import junit.framework.TestCase;

/**
 * <p>
 * Exercise base <code>EntitySuperclass</p> method to test implementation.
 * </p>
 *
 */
public class EntityServiceTest extends TestCase {

    UuidEntity entity;
    UuidEntity entity2;

    public void setUp() throws Exception {
        super.setUp();
        entity = new UuidEntity();
        entity2 = new UuidEntity();
    }

    public void testId() {
        String id = entity.getId();
        assertNotNull(id);
        assertTrue(id.length() > 0);
        assertFalse(id.equals(entity2.getId()));
    }

    public void testEquals() {
        boolean equalsSelf = entity.equals(entity);
        assertTrue(equalsSelf);
    }

    public void testEqualsNot() {
        boolean equalsNot = entity.equals(entity2);
        assertFalse(equalsNot);
    }

    public void testHashcode() {
        int hashcode = entity.hashCode();
        int hashcode2 = entity2.hashCode();
        boolean test = hashcode != hashcode2;
        assertTrue("Expected different hashcodes", test);
    }

    public void testToString() {
        UuidEntity entity3 = new UuidEntity();
        entity3.setId("55ba338a-97fa-44ce-bdad-80236d9404d0");
        String value = entity3.toString();
        String TO_STRING = "entity.UuidEntity[id=55ba338a-97fa-44ce-bdad-80236d9404d0]";
        assertEquals(TO_STRING, value);
    }

}
