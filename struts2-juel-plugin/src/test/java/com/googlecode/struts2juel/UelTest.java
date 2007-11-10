package com.googlecode.struts2juel;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;

public class UelTest extends XWorkTestCase {
	private ExpressionFactory factory = ExpressionFactory.newInstance();
	private XWorkConverter converter;
	private DateFormat format = DateFormat.getDateInstance();

	private class DateConverter extends StrutsTypeConverter {

		@Override
		public Object convertFromString(Map context, String[] values,
				Class toClass) {
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
	}

	public void testBasicFind() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		root.add(obj);
		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		stack.setValue("${value}", "Hello World");
		String value = stack.findString("${value}");
		assertEquals("Hello World", value);

		stack.setValue("${age}", "56");
		String age = stack.findString("${age}");
		assertEquals("56", age);
	}

	public void testSetStringArray() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		root.add(obj);
		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		stack.setValue("${value}", new String[] { "Hello World" });
		String value = stack.findString("${value}");
		assertEquals("Hello World", value);

		stack.setValue("${age}", new String[] { "67" });
		assertEquals(new Integer(67), stack.findValue("${age}"));
	}

	public void testDeferredFind() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		root.add(obj);

		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		stack.setValue("#{value}", "Hello World");
		String value = stack.findString("#{value}");
		assertEquals("Hello World", value);

		stack.setValue("#{age}", "56");
		String age = stack.findString("#{age}");
		assertEquals("56", age);

		stack.setValue("#{date}", new Date());
		assertEquals(stack.findString("#{date}"), format.format(obj.getDate()));
	}

	public void testMap() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		HashMap map = new HashMap();
		map.put("nameValue", "Musachy");
		TestObject obj = new TestObject();
		obj.setParameters(map);
		root.add(obj);

		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		String value = (String) stack.findValue("parameters.nameValue",
				String.class);
		assertEquals("Musachy", value);
	}

	public void test2LevelSet() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		TestObject nestedObj = new TestObject();
		obj.setInner(nestedObj);
		root.add(obj);
		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		stack.setValue("${inner.age}", "66");
		assertEquals(66, obj.getInner().getAge());
	}

	public void testTypeConversion() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		TestObject inner = new TestObject();
		obj.setInner(inner);
		root.add(obj);
		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);

		stack.setValue("${age}", "22");
		assertEquals(stack.findValue("${age}"), obj.getAge());

		stack.setValue("${inner.value}", "George");
		assertEquals(stack.findValue("${inner.value}"), obj.getInner()
				.getValue());

		stack.setValue("${inner.age}", "44");
		assertEquals(stack.findValue("${inner.age}"), obj.getInner().getAge());

		stack.setValue("${date}", new Date());
		assertEquals(stack.findString("${date}"), format.format(obj.getDate()));
	}

	public void testNotFound() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		CompoundRoot root = new CompoundRoot();
		TestObject obj = new TestObject();
		root.add(obj);
		UelValueStack stack = new UelValueStack(factory, converter);
		stack.setRoot(root);
		stack.setValue("${value}", "Hello World");
		String value = stack.findString("${VALUENOTHERE}");
		assertNull(value);

		value = stack.findString("VALUENOTHERE");
		assertNull(value);
	}

	public class TestObject {
		private String value;
		private int age;
		private Date date;
		private TestObject inner;
		private Map parameters;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public TestObject getInner() {
			return inner;
		}

		public void setInner(TestObject inner) {
			this.inner = inner;
		}

		public Map getParameters() {
			return parameters;
		}

		public void setParameters(Map parameters) {
			this.parameters = parameters;
		}
	}
}
