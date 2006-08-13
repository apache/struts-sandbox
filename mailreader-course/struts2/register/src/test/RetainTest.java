import junit.framework.TestCase;
import org.apache.struts.apps.mailreader.dao.Subscription;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemoryUserDatabase;

public class RetainTest extends TestCase {

    protected UserDatabase database;
    protected String testName = "test-database.xml";

    public void setUp() throws Exception {
        super.setUp();
        MemoryUserDatabase memoryUserDatabase = new MemoryUserDatabase();
        memoryUserDatabase.setPathname(testName);
        database = memoryUserDatabase;
    }

    public void testDatabaseClean() throws Exception {
        User user = database.findUser("user");
        assertNull("Expected user to be missing", user);
    }

    //  Add your test methods here.
    //  The setUp method will run between each test.

    public void testCreateUser() throws Exception {
        User user = database.createUser("user");
        assertNotNull(user);
    }

    public void testFindUser() throws Exception {
        User user1 = database.createUser("user");
        User user2 = database.findUser("user");
        assertSame("Expected users to match", user1, user2);
    }

    public void testCreateSubscription() throws Exception {
        Subscription sub = database.createUser("user").createSubscription("mail.yahoo.com");
        assertNotNull("Expected subscription to be created", sub);
    }

    public void testFindSubscription() throws Exception {
        Subscription sub1 = database.createUser("user").createSubscription("mail.yahoo.com");
        Subscription sub2 = database.findUser("user").findSubscription("mail.yahoo.com");
        assertSame("Expected Subscriptions to match", sub1, sub2);
    }

    public void testDatabsaeSave() throws Exception {

        User user = database.createUser("user");
        user.setPassword("pass");
        user.setFullName("John Q. User");
        user.setFromAddress("John.User@somewhere.com");

        Subscription sub1 = user.createSubscription("mail.hotmail.com");
        sub1.setUsername("user1234");
        sub1.setAutoConnect(false);
        sub1.setType("pop3");
        sub1.setPassword("bar");

        Subscription sub2 = user.createSubscription("mail.yahoo.com");
        sub2.setUsername("jquser");
        sub2.setAutoConnect(false);
        sub2.setType("imap");
        sub2.setPassword("foo");

        database.save();

    }

    // Extra credit

    public void testFinderUserBogus() throws Exception {
        User user1 = database.findUser("bogus");
        assertNull("Expected bogus user to be not found", user1);
        User user2 = database.createUser("user");
        assertNotNull("Expected user to be created", user2);
        user1 = database.findUser("bogus");
        assertNull("Expected bogus user to be not found", user1);
        user1 = database.findUser("user");
        assertNotNull("Expected user to be found", user1);
    }

    public void testFindSubBogus() throws Exception {
        User user = database.createUser("user");
        Subscription sub = user.findSubscription("mail.bogus.com");
        assertNull(sub);
        sub = database.findUser("user").createSubscription("mail.hotmail.com");
        assertNotNull(sub);
        Subscription sub2 = user.findSubscription("mail.bogus.com");
        assertNull(sub2);
        Subscription sub3 = user.findSubscription("mail.hotmail.com");
        assertSame(sub, sub3);
    }

}