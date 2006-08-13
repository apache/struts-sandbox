import com.opensymphony.xwork2.Action;
import org.apache.struts.apps.mailreader.dao.User;

import java.util.HashMap;

public class RegisterTest extends RetainTest {

    Register action = null;

    public void setUp() throws Exception {
        super.setUp();
        action = new Register();
        action.setApplication(new HashMap());
        action.setDatabase(database);
    }

    public void testExecute() throws Exception {

        action.setUsername("user");
        action.setPassword("pass");
        action.setFullName("John Q. User");
        action.setFromAddress("John.User@somewhere.com");

        String success = action.execute();
        assertTrue("Expected SUCCESS", Action.SUCCESS.equals(success));

        User user = database.findUser("user");
        assertNotNull("Expected user", user);

        String input = action.execute();
        assertTrue("Expected INPUT", Action.INPUT.equals(input));
    }

}
