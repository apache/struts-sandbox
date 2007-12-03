package entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import entity.protocol.Protocol;
import entity.protocol.ProtocolService;
import entity.protocol.ProtocolServiceImpl;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionImpl;
import entity.user.User;
import entity.user.UserImpl;
import entity.user.UserService;
import entity.user.UserServiceImpl;

public class UserServiceTest extends EntityTestCase {

    protected UserService helper;
    int beforeCount = 0;

    public void setUp() throws Exception {
        super.setUp();
        helper = new UserServiceImpl();
        beforeCount = helper.count();
    }

    private UserImpl newUser() {
        return new UserImpl("user_" + base, "pass_" + base);
    }

    public void testCount() throws Exception {
        EntityManager manager = EntityManagerHelper.getEntityManager();
        Object result = null;
        Query query = manager.createNamedQuery(UserImpl.COUNT);
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }
        assertNotNull(result);
        Long count = (Long) result;
        assertTrue(0 < count.intValue());
    }

    public void testCreate() throws Exception {
        UserImpl user = newUser();
        String before = user.getId();
        assertTrue("ID not assigned on New", isNotEmpty(before));
        helper.create(user);
        String after = user.getId();
        assertTrue("Initial ID changed on Create", before.equals(after));
        int afterCount = helper.count();
        assertTrue("Expected count to increase", afterCount > beforeCount);
    }

    private List<Subscription> getSubscriptions(User user) {
        ProtocolService protocolManager = new ProtocolServiceImpl();
        boolean autoConnect = false;
        List<Protocol> protocols = protocolManager.findAll();
        int protocolMax = protocols.size();
        List<Subscription> subscriptions = new ArrayList<Subscription>();

        for (int i = 0; i < 3; i++) {
            String base = String.valueOf(i + 1);
            autoConnect = !autoConnect;
            int protocolIndex = (i % protocolMax);
            SubscriptionImpl sub = new SubscriptionImpl("host_" + base, user,
                    "user_" + base, "pass_" + base, protocols
                            .get(protocolIndex), autoConnect);
            subscriptions.add(sub);
        }
        return subscriptions;
    }

    public void testCreateWithSubscriptions() throws Exception {
        UserImpl user = newUser();
        helper.create(user);
        List<Subscription> subscriptions = getSubscriptions(user);
        user.addSubscriptions(subscriptions);
        helper.update(user);
        assertTrue("Expected ID", user.getId() != null);
        assertTrue(user.getSubscriptions() != null);
    }

    public void testCreateDelete() throws Exception {
        // rollback = true;
        User user = new UserImpl();
        user.setUsername("user_" + base);
        user.setPassword("pass_" + base);
        helper.create(user);
        // throw new PersistenceException();
        helper.delete(user);
        int afterCount = helper.count();
        assertTrue(beforeCount == afterCount);
        // rollback = false;
    }

}
