package entity.user;

import junit.framework.TestCase;
import java.util.Random;

public class UserManagerTest extends TestCase {

    UserManagerInterface manager;
    String base;

    public void setUp() throws Exception {
        super.setUp();
        manager = new UserManager();
        Random generator = new Random();
        int r = generator.nextInt();
        base = String.valueOf(r);
    }

    private boolean isNotEmpty(String value) {
        return (value != null) && (value.length() > 0);
    }

    public void testCreate() throws Exception {
        User user = new User();
        String before = user.getId();
        assertFalse("User ID not empty on New", isNotEmpty(before));
        user.setUsername("user_" + base);
        user.setPassword("pass_" + base);
        manager.create(user);
        String after = user.getId();
        assertTrue("User ID not assigned on Create", isNotEmpty(after));
    }

}
