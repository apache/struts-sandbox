package cookbook2;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.Validateable;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select extends ActionSupport implements Validateable {

    String name;
    Date birthday;
    String bio;
    String favoriteColor;
    List friends;
    boolean legalAge;
    String state;
    String region;
    File picture;
    String pictureContentType;
    String pictureFileName;
    String favouriteLanguage;
    String favouriteVehicalType = "MotorcycleKey";
    String favouriteVehicalSpecific = "YamahaKey";

    List leftSideCartoonCharacters;
    List rightSideCartoonCharacters;

    List favouriteLanguages = new ArrayList();
    List vehicalTypeList = new ArrayList();
    Map vehicalSpecificMap = new HashMap();

    String thoughts;

    /**
     * The constructor pre-populates the controsl for us.
     */
    public Select() {
        favouriteLanguages.add(new Language("EnglishKey", "English Language"));
        favouriteLanguages.add(new Language("FrenchKey", "French Language"));
        favouriteLanguages.add(new Language("SpanishKey", "Spanish Language"));

        VehicalType car = new VehicalType("CarKey", "Car");
        VehicalType motorcycle = new VehicalType("MotorcycleKey", "Motorcycle");
        vehicalTypeList.add(car);
        vehicalTypeList.add(motorcycle);

        List cars = new ArrayList();
        cars.add(new VehicalSpecific("MercedesKey", "Mercedes"));
        cars.add(new VehicalSpecific("HondaKey", "Honda"));
        cars.add(new VehicalSpecific("FordKey", "Ford"));

        List motorcycles = new ArrayList();
        motorcycles.add(new VehicalSpecific("SuzukiKey", "Suzuki"));
        motorcycles.add(new VehicalSpecific("YamahaKey", "Yamaha"));

        vehicalSpecificMap.put(car, cars);
        vehicalSpecificMap.put(motorcycle, motorcycles);
    }

    /**
     * Prepare form for display.
     *
     * <p> In this example, all the work is done in the constructor. Other cases may need to consult data access logic
     * to populate the controls. </p>
     *
     * @return Result
     */
    public String setup() {
        return SUCCESS;
    }

    /**
     * Execute the "Select" action.
     *
     * <p> In this example, we are simply displaying the input. Other cases may need to consult business or data access
     * logic. </p>
     *
     * @return Result
     * @throws Exception on any error
     */
    public String execute() throws Exception {
        return SUCCESS;
    }

    // -- Methods that populate controls from data prepared in the constructor --

    public List getLeftSideCartoonCharacters() {
        return leftSideCartoonCharacters;
    }

    public void setLeftSideCartoonCharacters(List leftSideCartoonCharacters) {
        this.leftSideCartoonCharacters = leftSideCartoonCharacters;
    }


    public List getRightSideCartoonCharacters() {
        return rightSideCartoonCharacters;
    }

    public void setRightSideCartoonCharacters(List rightSideCartoonCharacters) {
        this.rightSideCartoonCharacters = rightSideCartoonCharacters;
    }


    public String getFavouriteVehicalType() {
        return favouriteVehicalType;
    }

    public void setFavouriteVehicalType(String favouriteVehicalType) {
        this.favouriteVehicalType = favouriteVehicalType;
    }

    public String getFavouriteVehicalSpecific() {
        return favouriteVehicalSpecific;
    }

    public void setFavouriteVehicalSpecific(String favouriteVehicalSpecific) {
        this.favouriteVehicalSpecific = favouriteVehicalSpecific;
    }


    public List getVehicalTypeList() {
        return vehicalTypeList;
    }

    public List getVehicalSpecificList() {
        OgnlValueStack stack = ServletActionContext.getValueStack(ServletActionContext.getRequest());
        VehicalType vehicalType = (VehicalType) stack.findValue("top");
        System.out.println("vehicalType.getKey()" + vehicalType.getKey());
        return (List) vehicalSpecificMap.get(vehicalType);
    }

    public List getFavouriteLanguages() {
        return favouriteLanguages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    public List getFriends() {
        return friends;
    }

    public void setFriends(List friends) {
        this.friends = friends;
    }

    public boolean isLegalAge() {
        return legalAge;
    }

    public void setLegalAge(boolean legalAge) {
        this.legalAge = legalAge;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setPicture(File picture) {
        this.picture = picture;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }

    public void setFavouriteLanguage(String favouriteLanguage) {
        this.favouriteLanguage = favouriteLanguage;
    }

    public String getFavouriteLanguage() {
        return favouriteLanguage;
    }


    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getThoughts() {
        return this.thoughts;
    }

    // -- inner classes --

    public static class Language {
        String description;
        String key;

        public Language(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }

    }

    public static class VehicalType {
        String key;
        String description;

        public VehicalType(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return this.key;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean equals(Object obj) {
            if (! (obj instanceof VehicalType)) {
                return false;
            } else {
                return key.equals(((VehicalType) obj).getKey());
            }
        }

        public int hashCode() {
            return key.hashCode();
        }
    }


    public static class VehicalSpecific {
        String key;
        String description;

        public VehicalSpecific(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return this.key;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean equals(Object obj) {
            if (! (obj instanceof VehicalSpecific)) {
                return false;
            } else {
                return key.equals(((VehicalSpecific) obj).getKey());
            }
        }

        public int hashCode() {
            return key.hashCode();
        }
    }
}
