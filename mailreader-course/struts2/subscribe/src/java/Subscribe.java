import com.opensymphony.xwork2.Preparable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Subscribe extends MailReaderSupport
        implements Preparable {

    private Map types = null;

    public Map getTypes() {
        return types;
    }

    public void prepare() {

        Map m = new LinkedHashMap();
        m.put("imap", "IMAP Protocol");
        m.put("pop3", "POP3 Protocol");
        types = m;

        setHost(getSubscriptionHost());
    }

    public String input() {
        createInputSubscription();
        setTask(CREATE);
        return INPUT;
    }

    public String find() {

        org.apache.struts.apps.mailreader.dao.Subscription
                sub = findSubscription();

        if (sub == null) {
            return ERROR;
        }

        setSubscription(sub);

        return INPUT;

    }

    public String delete() {

        setTask(DELETE);
        return find();
    }

    public String edit() {

        setTask(EDIT);
        return find();
    }

    public String save() throws Exception {

        if (DELETE.equals(getTask())) {
            removeSubscription();
        }

        if (CREATE.equals(getTask())) {
            copySubscription(getHost());
        }

        if (hasErrors()) return INPUT;

        saveUser();
        return SUCCESS;
    }

}
