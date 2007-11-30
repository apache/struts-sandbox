package hello;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.TestCase;
import entity.EntityManagerHelper;

public class MessageTest extends TestCase {

    protected EntityManager manager;

    protected void setUp() throws Exception {
        super.setUp();
        manager = EntityManagerHelper.getEntityManager();
        EntityManagerHelper.beginTransaction();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        EntityManagerHelper.commit();
        EntityManagerHelper.closeEntityManager();
    }

    public int count() throws Exception {
        final String COUNT_QUERY = "SELECT COUNT(*) FROM APP_MESSAGE";
        Query query = manager.createQuery(COUNT_QUERY);
        Long count = (Long) query.getSingleResult();
        int result = count.intValue();
        return result;
    }

    public void testCreateMessage() throws Exception {
        final String MESSAGE = "Hello World!";

        Message message = new Message(MESSAGE);

        String text = message.getText();
        assertEquals(MESSAGE, text);

        manager.persist(message);
    }

    public void testCreateWithNextMessage() throws Exception {
        final String MESSAGE = "Greeting Earthling!";
        final String NEXT_MESSAGE = "Take me to your leader.";

        Message message = new Message(MESSAGE);
        Message next_message = new Message(NEXT_MESSAGE);
        message.setNextMessage(next_message);

        String text = message.getText();
        assertEquals(MESSAGE, text);
        String next_text = message.getNextMessage().getText();
        assertEquals(NEXT_MESSAGE, next_text);

        int beginCount = count();
        manager.persist(message);
        int endCount = count();
        assertTrue("Expected count to increase!", endCount > beginCount);
    }

    public void testUpdate() throws Exception {
        final String MESSAGE = "Update me!";
        final String UPDATE = "Hey! I've been updated!";
        
        Message message = new Message(MESSAGE);
        manager.persist(message);
        manager.flush();        

        Long id = message.getId(); 
        Message update = manager.find(Message.class, id);        
        assertSame("Expected same instance",message,update);
        
        update.setText(UPDATE);
        manager.flush();
        
        Message result = manager.find(Message.class, id);
        assertEquals(UPDATE,result.getText());        
    }
    
    public void testCreateDelete() throws Exception {
        final String MESSAGE = "Delete me!";
        Message message = new Message(MESSAGE);
        int beginCount = count();
        manager.persist(message);
        manager.flush();
        int endCount = count();
        assertTrue(endCount > beginCount);
        manager.remove(message);
        manager.flush();
        int finalCount = count();
        assertTrue(finalCount == beginCount);
    }

}
