package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.uelplugin.UelServletContextListener;
import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.mock.web.MockServletContext;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContextEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UelTest extends XWorkTestCase {
    private ExpressionFactory factory = ExpressionFactory.newInstance();
    private XWorkConverter converter;
    private DateFormat format = DateFormat.getDateInstance();
    private CompoundRoot root;
    private UelValueStack stack;

    private class DateConverter extends StrutsTypeConverter {

        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            try {
                return format.parseObject(values[0]);
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public String convertToString(Map context, Object o) {
            return format.format(o);
        }

    }

    protected void setUp() throws Exception {
        super.setUp();

        converter = container.getInstance(XWorkConverter.class);
        converter.registerConverter("java.util.Date", new DateConverter());
        this.root = new CompoundRoot();
        this.stack = new UelValueStack(converter);
        stack.setRoot(root);
        stack.getContext().put(ActionContext.CONTAINER, container);

        MockServletContext servletContext = new MockServletContext();
        ActionContext context = new ActionContext(stack.getContext());
        ActionContext.setContext(context);
        ServletActionContext.setServletContext(servletContext);

        //simulate start up
        UelServletContextListener listener = new UelServletContextListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
    }


    public void testContextReferencesWithSameObjectInStack() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //if there as object in the stack with the property "X" and there is an object in the
        //stack context under the key "X" then:
        //${X} : should return the value of X from object in stack
        //${#X} : should return object from the stack context

        TestObject obj = new TestObject();
        obj.setValue("ref");
        stack.push(obj);

        TestObject obj2 = new TestObject();
        stack.getContext().put("value", obj2);

        //simple
        assertEquals("ref", stack.findValue("value"));
        assertSame(obj2, stack.findValue("#value"));

    }

    public void testContextReferences() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("val");
        obj.setAge(1);
        stack.getContext().put("obj", obj);

        //simple
        assertSame(obj, stack.findValue("#obj"));
        assertEquals("val", stack.findValue("#obj.value"));

        //more expressions
        TestObject obj2 = new TestObject();
        obj2.setValue("val2");
        obj2.setAge(2);
        stack.getContext().put("obj2", obj2);

        //addition
        assertSame(3L, stack.findValue("#obj.age + #obj2.age"));

        //string addition
        assertEquals("valval2", stack.findValue("#obj.value + #obj2.value"));
        assertEquals("1val2", stack.findValue("#obj.age + #obj2.value"));
    }

    public void testBasicFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);
        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${value}");
        assertEquals("Hello World", value);

        stack.setValue("${age}", "56");
        Integer age = (Integer) stack.findValue("${age}");
        assertEquals(56, (int) age);
    }

    public void testNestedFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject obj2 = new TestObject();
        obj2.setAge(100);
        obj.setInner(obj2);
        root.add(obj);

        assertSame(obj2, stack.findValue("${inner}"));
        assertEquals(100, stack.findValue("${inner.age}"));
    }

    public void testSetStringArray() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("${value}", new String[]{"Hello World"});
        String value = stack.findString("${value}");
        assertEquals("Hello World", value);

        stack.setValue("${age}", new String[]{"67"});
        assertEquals(new Integer(67), stack.findValue("${age}"));
    }

    public void testDeferredFind() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("#{value}", "Hello World");
        String value = stack.findString("#{value}");
        assertEquals("Hello World", value);

        stack.setValue("#{age}", "56");
        String age = stack.findString("#{age}");
        assertEquals("56", age);

        stack.setValue("#{date}", new Date());
        assertEquals(stack.findString("#{date}"), format.format(obj.getDate()));
    }

    public void testMap() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        HashMap map = new HashMap();
        map.put("nameValue", "Lex");
        TestObject obj = new TestObject();
        obj.setParameters(map);
        root.add(obj);

        String value = (String) stack.findValue("parameters.nameValue", String.class);
        assertEquals("Lex", value);
    }

    public void test2LevelSet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject nestedObj = new TestObject();
        obj.setInner(nestedObj);
        root.add(obj);

        stack.setValue("${inner.age}", "66");
        assertEquals(66, obj.getInner().getAge());
    }

    public void testTypeConversion() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        TestObject inner = new TestObject();
        obj.setInner(inner);
        root.add(obj);

        stack.setValue("${age}", "22");
        assertEquals(stack.findValue("${age}"), obj.getAge());

        stack.setValue("${inner.value}", "George");
        assertEquals(stack.findValue("${inner.value}"), obj.getInner().getValue());

        stack.setValue("${inner.age}", "44");
        assertEquals(stack.findValue("${inner.age}"), obj.getInner().getAge());

        stack.setValue("${date}", new Date());
        assertEquals(stack.findString("${date}"), format.format(obj.getDate()));
    }

    public void testNotFound() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        root.add(obj);

        stack.setValue("${value}", "Hello World");
        String value = stack.findString("${VALUENOTHERE}");
        assertNull(value);

        value = stack.findString("VALUENOTHERE");
        assertNull(value);
    }
}
