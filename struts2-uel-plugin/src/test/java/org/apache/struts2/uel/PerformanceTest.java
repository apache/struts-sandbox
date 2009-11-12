package org.apache.struts2.uel;

import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.util.ValueStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PerformanceTest extends UELBaseTest {
    private final long ITERATIONS = 1000000;
    private ValueStack ognlValueStack;


    protected void setUp() throws Exception {
        super.setUp();

        OgnlValueStackFactory ognlValueStackFactory = new OgnlValueStackFactory();
        container.inject(ognlValueStackFactory);
        this.ognlValueStack = ognlValueStackFactory.createValueStack();
    }

    public void testSuperNested() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj0 = new TestObject("0");

        ognlValueStack.push(obj0);
        root.push(obj0);

        TestObject obj1 = new TestObject("1");
        obj0.setInner(obj1);

        TestObject obj2 = new TestObject("2");
        Map map = new HashMap();
        map.put("key0", obj2);
        obj1.setParameters(map);

        TestObject obj3 = new TestObject("3");
        List list = new ArrayList();
        list.add(obj3);
        obj2.setObject(obj3);

        TestObject obj4 = new TestObject("4");
        TestObject[] array = new TestObject[]{obj4};
        obj3.setObject(array);

        stack.getContext().put("obj", obj0);
        ognlValueStack.getContext().put("obj", obj0);

        String expr = "inner.parameters['key0'].object.object[0].value";

        assertEquals("4", stack.findValue(expr));
        assertEquals("4", ognlValueStack.findValue(expr));
        compare(expr);
    }

    public void testContextReferences() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestObject obj = new TestObject();
        obj.setValue("val");
        obj.setAge(1);
        stack.getContext().put("obj", obj);
        ognlValueStack.getContext().put("obj", obj);

        //more expressions
        TestObject obj2 = new TestObject();
        obj2.setValue("val2");
        obj2.setAge(2);
        stack.getContext().put("obj2", obj2);
        ognlValueStack.getContext().put("obj2", obj2);

        //addition
        String expr = "#obj.age + #obj2.age";
        assertEquals(3L, stack.findValue(expr));
        assertEquals(3, ognlValueStack.findValue(expr));

        compare(expr);
    }

    public void testArithmetics() {
        Map uelContext =  stack.getContext();
        Map ognlContext =  ognlValueStack.getContext();

        uelContext.put("x", 1);
        uelContext.put("y", 2);
        uelContext.put("z", 3);

        ognlContext.put("x", 1);
        ognlContext.put("y", 2);
        ognlContext.put("z", 3);

        String expr = "#x + #y * #z";

        assertEquals(7L, stack.findValue(expr, true));
        assertEquals(7, ognlValueStack.findValue(expr));
        
        compare(expr);
    }

    protected void compare(String expr) {
        System.out.println("Eval: [" + expr + "] on " + ITERATIONS + " iterations");

        long ognl = evaluate(ognlValueStack, expr);
        System.out.println("OGNL: " + ognl + " ms");

        long juel = evaluate(stack, expr);
        System.out.println("JUEL: " + juel + " ms");
    }

    protected long evaluate(ValueStack valueStack, String expr) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            valueStack.findValue(expr);
        }

        return System.currentTimeMillis() - start;
    }
}
