package mailreader2;

import com.opensymphony.xwork.Preparable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Subscription extends MailreaderSupport implements Preparable {

    private Map types = null;

    public Map getTypes() {
        return types;
    }

    public void prepare() {
        Map m = new LinkedHashMap();
        m.put("imap", "IMAP Protocol");
        m.put("pop3", "POP3 Protocol");
        types = m;
    }

    public String input() {

        setTask(Constants.CREATE);
        return INPUT;
    }

    public String delete() {

        setTask(Constants.DELETE);
        return INPUT;
    }

    public String edit() {

        setTask(Constants.EDIT);

        org.apache.struts.apps.mailreader.dao.Subscription
                sub = findSubscription();

        if (sub == null) {
            return ERROR;
        }

        setSubscription(sub);

        return INPUT;
    }

    public String execute() throws Exception {

        if (Constants.DELETE.equals(getTask())) {
            removeSubscription();
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
