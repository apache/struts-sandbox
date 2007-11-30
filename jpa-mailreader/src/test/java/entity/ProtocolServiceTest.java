package entity;

import junit.framework.TestCase;
import entity.protocol.ProtocolService;
import entity.protocol.ProtocolServiceImpl;

public class ProtocolServiceTest extends TestCase {

    protected ProtocolService helper;
    int beforeCount = 0;

    public void setUp() throws Exception {
        super.setUp();
        helper = new ProtocolServiceImpl();
        beforeCount = helper.count();
    }

}
