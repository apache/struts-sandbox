package cookbook2.actiontag;

import com.opensymphony.xwork.ActionSupport;

public class Result extends ActionSupport {

    String favoriteLanguage;

    public void setFavoriteLanguage(String favouriteLanguage) {
        this.favoriteLanguage = favouriteLanguage;
    }

    public String getFavoriteLanguage() {
        return favoriteLanguage;
    }

    String favoriteColor;

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

}
