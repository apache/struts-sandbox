package mailreader2;

/**
 * <p> Workaround class. Submitting to an alias doesn't seem to work. </p>
 */
public final class SubscriptionSave extends Subscription {

    public String execute() throws Exception {
        return save();
    }

}
