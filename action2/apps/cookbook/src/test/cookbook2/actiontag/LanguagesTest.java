package cookbook2.actiontag;

import cookbook2.Select;
import junit.framework.TestCase;

import java.util.List;

public class LanguagesTest extends TestCase {

    private Languages action;

    public void setUp() {

        action = new Languages();

    }

    public void testContents() throws Exception {

        List list = action.getFavoriteLanguages();
        assertNotNull("List is null!", list);
        assertTrue("List is not empty", list.size() == 0);

        action.execute();

        List list2 = action.getFavoriteLanguages();
        assertNotNull("List is null!", list2);
        assertTrue("List is empty!", list.size() > 0);
        Select.Language entry = (Select.Language) list.get(0);
        assertNotNull("Entry is null", entry);
        assertTrue("Entry is empty", entry.getDescription().length() > 0);
    }

}
