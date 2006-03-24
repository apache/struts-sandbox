package mailreader2;

import java.util.ArrayList;
import java.util.List;

public final class Subscription extends MailreaderSupport {

    List types = new ArrayList();

    List getTypes() {
        return types;
    }

    public String input() {

        types.add(new KeyValue("imap", "IMAP Protocol"));
        types.add(new KeyValue("pop3", "POP3 Protocol"));

        return INPUT;
    }

    public String execute() {


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
