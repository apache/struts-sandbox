package cookbook2.pojo;

import java.util.Date;

public class DirectoryEntry {

    private String firstname;

    private String lastname;

    private String extension;

    private String username;

    private Date hired;

    private Integer hours;

    private Boolean editor;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String value) {
        firstname = value;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String value) {
        lastname = value;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String value) {
        extension = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        username = value;
    }

    public Date getHired() {
        return hired;
    }

    public void setHired(Date value) {
        hired = value;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer value) {
        hours = value;
    }

    public Boolean getEditor() {
        return editor;
    }

    public void setEditor(Boolean value) {
        editor = value;
    }

}
