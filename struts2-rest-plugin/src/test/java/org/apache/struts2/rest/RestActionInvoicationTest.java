package org.apache.struts2.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.rest.RestActionInvocation.MethodMatch;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.XWorkConverter;

import junit.framework.TestCase;

public class RestActionInvoicationTest extends StrutsTestCase {

    RestActionInvocation inv;
    Mock mockActionProxy;
    
    protected void setUp() throws Exception {
        //super.setUp();
        mockActionProxy = new Mock(ActionProxy.class);
        mockActionProxy.matchAndReturn("getActionName", "bob");
        mockActionProxy.matchAndReturn("getNamespace", "foo");
        mockActionProxy.matchAndReturn("getConfig", new ActionConfig());
        
        ObjectFactory objFactory = new ObjectFactory() {
            public Object buildAction(String action, String namespace, ActionConfig config, Map params) {
                return new FooAction();
            }
        };
        inv = new RestActionInvocation(objFactory,null, (ActionProxy) mockActionProxy.proxy(), null);
        inv.setXWorkConverter(XWorkConverter.getInstance());
    }

    public void testFindParamsInName() throws Exception {
        String[] params = new String[] {"foo", "bar"};
        assertEquals(Arrays.asList(params), inv.findParamsInName("indexWithFooAndBar"));
    }
    
    public void testFindParamsInName_oneParam() throws Exception {
        String[] params = new String[] {"foo"};
        assertEquals(Arrays.asList(params), inv.findParamsInName("indexWithFoo"));
    }
    
    public void testFindParamsInName_noParams() throws Exception {
        assertEquals(new ArrayList(), inv.findParamsInName("index"));
    }
    
    public void testFindMethod() throws Exception, NoSuchMethodException {
        
        Map<String,String> params = new HashMap<String,String>() {{
            put("foo", "sd");
            put("bar", "3");
        }};
        
        Method found = inv.findMethod(new FooAction(), "index", params).method;
        Method expected = FooAction.class.getDeclaredMethod("indexWithFooAndBar", new Class[]{String.class, int.class});
        assertEquals(expected, found);
    }
    
    public void testFindMethod_missingParams() throws Exception, NoSuchMethodException {
        
        Map<String,String> params = new HashMap<String,String>() {{
            put("foo", "sd");
        }};
        
        try {
            inv.findMethod(new FooAction(), "update", params);
            fail("Should have thrown exception");
        } catch (NoSuchMethodException ex) {
            // success
        }
    }
    
    public void testFindMethod_noParams() throws Exception, NoSuchMethodException {
        
        Method found = inv.findMethod(new FooAction(), "noarg", new HashMap()).method;
        Method expected = FooAction.class.getDeclaredMethod("noarg", new Class[]{});
        assertEquals(expected, found);
    }
    
    public void testCallAction() throws Exception {
        FooAction action = new FooAction();
        Method method = FooAction.class.getDeclaredMethod("indexWithFooAndBar", new Class[]{String.class, int.class});
        Map reqParams = new HashMap() {{
            put("foo", "bar");
            put("bar", "3");
        }};
        MethodMatch match = new MethodMatch(method, Arrays.asList("foo", "bar"));
        
        String ret = (String) inv.callAction(action, match, reqParams);
        
        assertEquals("index:bar:3", ret);
    }
    
    public void testCallAction_oneParam() throws Exception {
        FooAction action = new FooAction();
        Method method = FooAction.class.getDeclaredMethod("indexWithFoo", new Class[]{String.class});
        Map reqParams = new HashMap() {{
            put("foo", "bar");
            put("bar", "3");
        }};
        MethodMatch match = new MethodMatch(method, Arrays.asList("foo"));
        
        String ret = (String) inv.callAction(action, match, reqParams);
        
        assertEquals("index:bar", ret);
    }
    
    public void testCallAction_noParam() throws Exception {
        FooAction action = new FooAction();
        Method method = FooAction.class.getDeclaredMethod("index", new Class[]{});
        Map reqParams = new HashMap() {{
            put("foo", "bar");
            put("bar", "3");
        }};
        MethodMatch match = new MethodMatch(method, new ArrayList());
        
        String ret = (String) inv.callAction(action, match, reqParams);
        
        assertEquals("index", ret);
    }
    
    public void testCallAction_withBody() throws Exception {
        FooAction action = new FooAction();
        Method method = FooAction.class.getDeclaredMethod("createWithBody", new Class[]{String.class});
        Map reqParams = new HashMap() {{
            put("foo", "bar");
            put("bar", "3");
            put("body", "jim");
        }};
        MethodMatch match = new MethodMatch(method, Collections.singletonList("body"));
        
        String ret = (String) inv.callAction(action, match, reqParams);
        
        assertEquals("create:jim", ret);
    }
    
    @Restful
    static class FooAction {
        public String index() {
            return "index";
        }
        public String indexWithFoo(String foo) {
            return "index:"+foo;
        }
        public String indexWithFooAndBar(String foo, int bar) {
            return "index:"+foo+":"+bar;
        }
        
        public void updateWithFooAndBar(String foo, int bar) {}
        
        public String createWithBody(String data) {
            return "create:"+data;
        }
        
        public void noarg() {}
        
    }

}
