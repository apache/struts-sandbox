package entity;

import junit.framework.TestCase;
import entity.subscription.SubscriptionService;
import entity.subscription.SubscriptionServiceImpl;

public class SubscriptionServiceTest extends TestCase {

    protected SubscriptionService helper;
    int beforeCount = 0;

    public void setUp() throws Exception {
        super.setUp();
        helper = new SubscriptionServiceImpl();
        beforeCount = helper.count();
    }

}
