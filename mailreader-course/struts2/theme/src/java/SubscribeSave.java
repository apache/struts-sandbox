public final class SubscribeSave extends Subscribe {

    public void prepare() {
        super.prepare();
        // checkbox workaround
        getSubscription().setAutoConnect(false);
    }

    public String execute() throws Exception {
        return save();
    }
}
