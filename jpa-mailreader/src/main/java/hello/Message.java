package hello;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity(name = "APP_MESSAGE")
@NamedQueries( { @NamedQuery(name = Message.COUNT, query = Message.COUNT_QUERY) })
public class Message {

    public static final String COUNT = "Message.COUNT";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM APP_MESSAGE";

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    void setId(Long value) {
        id = value;
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    @OneToOne(cascade = CascadeType.ALL)
    private Message message;

    public Message getNextMessage() {
        return message;
    }

    public void setNextMessage(Message value) {
        message = value;
    }

    Message() {
    }

    public Message(String value) {
        text = value;
    }

}