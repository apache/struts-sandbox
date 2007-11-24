package action.user;

import junit.framework.TestCase;
import entity.user.User;
import entity.user.UserManager;
import entity.user.UserManagerInterface;

public class autenticateTest extends TestCase {

    UserManagerInterface manager;

    public void setUp() throws Exception {
        super.setUp();
        manager = new UserManager();
    }

    private boolean authenticate(String username, String password) {
        User foundValue = manager.findByName(username);
        if ((foundValue != null) && !foundValue.getPassword().equals(password)) {
            foundValue = null;
        }

        return (foundValue != null);
    }

    public void testPass() throws Exception {
        boolean isFound = authenticate("user", "pass");
        assertTrue("Bootstrap credentials did not authenticate", isFound);
    }

    public void testFail() throws Exception {
        boolean isFound = authenticate("username", "password");
        assertFalse("Incorrect credentials authenticated", isFound);
    }

    public void testFail_Password() throws Exception {
        boolean isFound = authenticate("user", "password");
        assertFalse("Incorrect password authenticated", isFound);
    }

    public void testFail_User() throws Exception {
        boolean isFound = authenticate("username", "pass");
        assertFalse("Incorrect username authenticated", isFound);
    }

}
