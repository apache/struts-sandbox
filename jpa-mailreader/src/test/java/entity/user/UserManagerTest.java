package entity.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.EntityTestCase;
import entity.protocol.Protocol;
import entity.protocol.ProtocolManager;
import entity.protocol.ProtocolManagerInterface;
import entity.subscription.Subscription;

public class UserManagerTest extends EntityTestCase {

    UserManagerInterface manager;
    Random generator;
    String base;

    private String nextBase() {
        int r = generator.nextInt();
        return String.valueOf(r);
    }

    public void setUp() throws Exception {
        super.setUp();
        manager = new UserManager();
        generator = new Random();
        base = nextBase();
    }

    private boolean isNotEmpty(String value) {
        return (value != null) && (value.length() > 0);
    }

    private User newUser() {
        return new User("user_" + base, "pass_" + base);
    }

    public void testCreate() throws Exception {
        User user = newUser();
        String before = user.getId();
        assertTrue("ID not assigned on New", isNotEmpty(before));
        manager.create(user);
        String after = user.getId();
        assertTrue("Initial ID changed on Create", before.equals(after));
    }

    private List<Subscription> getSubscriptions(User user) {
        ProtocolManagerInterface protocolManager = new ProtocolManager();
        boolean autoConnect = false;
        List<Protocol> protocols = protocolManager.findAll();
        int protocolMax = protocols.size();
        List<Subscription> subscriptions = new ArrayList<Subscription>();

        for (int i = 0; i < 3; i++) {
            String base = String.valueOf(i + 1);
            autoConnect = !autoConnect;
            int protocolIndex = (i % protocolMax);
            Subscription sub = new Subscription("host_" + base, user, "user_"
                    + base, "pass_" + base, protocols.get(protocolIndex),
                    autoConnect);
            subscriptions.add(sub);
        }
        return subscriptions;
    }

    public void testCreateWithSubscriptions() {
        User user = newUser();
        manager.create(user);
        List<Subscription> subscriptions = getSubscriptions(user);
        user.addSubscriptions(subscriptions);
        manager.update(user);
        assertTrue("Expected ID", user.getId() != null);
        assertTrue(user.getSubscriptions() != null);
    }

    public void testCreateDelete() throws Exception {
        User user = new User();
        user.setUsername("user_" + base);
        user.setPassword("pass_" + base);
        manager.create(user);
        manager.delete(user);
    }

}
