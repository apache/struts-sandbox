package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.HashMap;

public class ReflectionProviderTest extends XWorkTestCase {
    private ReflectionProvider reflectionProvider;

    public void testGetSimple() {
        TestObject obj = new TestObject();
        TestObject obj2 = new TestObject();
        obj2.setAge(100);
        obj.setInner(obj2);

        assertSame(obj2, reflectionProvider.getValue("inner", new HashMap(), obj));
        assertEquals(100, reflectionProvider.getValue("inner.age", new HashMap(), obj));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        reflectionProvider = new UelReflectionProvider();
        container.inject(reflectionProvider);
    }
}
