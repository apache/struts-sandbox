package cookbook2.actiontag;

import com.opensymphony.xwork.ActionSupport;

public class Result extends ActionSupport {

    String favoriteLanguage;

    public void setFavoriteLanguage(String value) {
        favoriteLanguage = value;
    }

    public String getFavoriteLanguage() {
        return favoriteLanguage;
    }

    String favoriteColor;

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String value) {
        favoriteColor = value;
    }

}
