package com.googlecode.struts2juel;

import java.util.Map;
import java.util.TreeMap;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * A ValueStack that uses Unified EL as the underlying Expression Language.
 */
public class UelValueStack implements ValueStack {
	private CompoundRoot root = new CompoundRoot();
	private transient Map context;
	private Class defaultType;
	private Map overrides;
	private XWorkConverter xworkConverter;

	private ExpressionFactory factory;

	private ELContext elContext;

	public UelValueStack(ExpressionFactory factory,
			XWorkConverter xworkConverter) {
		this(factory, xworkConverter, new CompoundRoot());
	}

	public UelValueStack(ExpressionFactory factory,
			XWorkConverter xworkConverter, ValueStack vs) {
		this(factory, xworkConverter, new CompoundRoot(vs.getRoot()));
	}

	public UelValueStack(ExpressionFactory factory,
			XWorkConverter xworkConverter, CompoundRoot root) {
		this.xworkConverter = xworkConverter;
		this.factory = factory;
		setRoot(new CompoundRoot());
	}

	public String findString(String expr) {
		return (String) findValue(expr, String.class);
	}

	public Object findValue(String expr) {
		return findValue(expr, Object.class);
	}

	public Object findValue(String expr, Class asType) {
		try {
			if (expr != null && expr.startsWith("#") && !expr.startsWith("#{")) {
				int firstDot = expr.indexOf('.');
				String key = expr.substring(1, firstDot);
				String value = expr.substring(firstDot + 1);
				Map map = (Map) context.get(key);
				return map.get(value);
			}
			if ((overrides != null) && overrides.containsKey(expr)) {
				expr = (String) overrides.get(expr);
			}
			if (expr != null && expr.startsWith("%{")) {
				// replace %{ with ${
				expr = "#" + expr.substring(1);
			}
			if (expr != null && !expr.startsWith("${")
					&& !expr.startsWith("#{")) {
				expr = "#{" + expr + "}";
			}
	        elContext.putContext(XWorkConverter.class, xworkConverter);
	        elContext.putContext(CompoundRoot.class, root);
			// parse our expression
			ValueExpression valueExpr = factory.createValueExpression(
					elContext, expr, Object.class);
			Object retVal = valueExpr.getValue(elContext);
			if (!Object.class.equals(asType)) {
				retVal = xworkConverter.convertValue(null, retVal, asType);
			}
			return retVal;
		} catch (PropertyNotFoundException e) {
			// property not found
			return null;
		} catch (ELException e) {
			// fail silently so we don't mess things up
			return null;
		}
	}

	public Map getContext() {
		return context;
	}

	public Map getExprOverrides() {
		return overrides;
	}

	public CompoundRoot getRoot() {
		return root;
	}

	public Object peek() {
		return root.peek();
	}

	public Object pop() {
		return root.pop();
	}

	public void push(Object o) {
		root.push(o);
	}

	public void setDefaultType(Class defaultType) {
		this.defaultType = defaultType;
	}

	public void setExprOverrides(Map overrides) {
		if (this.overrides == null) {
			this.overrides = overrides;
		} else {
			this.overrides.putAll(overrides);
		}
	}

	public void set(String key, Object o) {
		overrides.put(key, o);
	}

	public void setValue(String expr, Object value) {
		setValue(expr, value, false);
	}

	public void setValue(String expr, Object value,
			boolean throwExceptionOnFailure) {
		try {
			if (expr != null && !expr.startsWith("${")
					&& !expr.startsWith("#{")) {
				expr = "#{" + expr + "}";
			}
	        elContext.putContext(XWorkConverter.class, xworkConverter);
	        elContext.putContext(CompoundRoot.class, root);
			// parse our expression
			ValueExpression valueExpr = factory.createValueExpression(
					elContext, expr, Object.class);
			valueExpr.setValue(elContext, value);
		} catch (ELException e) {
			if (throwExceptionOnFailure) {
				throw e;
			}
		}
	}

	public int size() {
		return root.size();
	}

	protected void setRoot(CompoundRoot root) {
		this.context = new TreeMap();
		context.put(VALUE_STACK, this);
		this.root = root;
		elContext = new CompoundRootELContext();
	}
}
