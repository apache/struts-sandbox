package cookbook2.actiontag;

import com.opensymphony.xwork.ActionSupport;
import cookbook2.Select;

import java.util.ArrayList;
import java.util.List;

public class Languages extends ActionSupport {

    List favoriteLanguages = new ArrayList();

    public List getFavoriteLanguages() {
        return favoriteLanguages;
    }

    public String execute() {

        favoriteLanguages.add(new Select.Language("EnglishKey", "English Language"));
        favoriteLanguages.add(new Select.Language("FrenchKey", "French Language"));
        favoriteLanguages.add(new Select.Language("SpanishKey", "Spanish Language"));

        return SUCCESS;
    }

}
