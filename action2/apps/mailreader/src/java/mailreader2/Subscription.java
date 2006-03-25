package mailreader2;

import com.opensymphony.xwork.Preparable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Subscription extends MailreaderSupport implements Preparable {

    private Map types = null;

    public Map getTypes() {
        return types;
    }

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String value) {
        host = value;
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
        setTask(Constants.CREATE);
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

        setTask(Constants.DELETE);
        return find();
    }

    public String edit() {

        setTask(Constants.EDIT);
        return find();
    }

    public String execute() throws Exception {

        if (Constants.DELETE.equals(getTask())) {
            removeSubscription();
        }

        if (Constants.CREATE.equals(getTask())) {
            copySubscription(getHost());
        }

        saveUser();
        return SUCCESS;
    }

    public static class KeyValue {
        String key;
        String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            if (! (obj instanceof KeyValue)) {
                return false;
            } else {
                return key.equals(((KeyValue) obj).getKey());
            }
        }

        public int hashCode() {
            return key.hashCode();
        }
    }

}
