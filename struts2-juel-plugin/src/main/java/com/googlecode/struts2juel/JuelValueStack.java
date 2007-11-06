package com.googlecode.struts2juel;

import java.util.Map;
import java.util.TreeMap;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * A ValueStack that uses Juel as the underlying Expression Language.
 */
public class JuelValueStack implements ValueStack {
	private CompoundRoot root = new CompoundRoot();
	private transient Map context;
	private Class defaultType;
	private Map overrides;

	private ExpressionFactory factory;

	private ELContext elContext;

	public JuelValueStack(ExpressionFactory factory) {
		this.factory = factory;
		setRoot(new CompoundRoot());
	}

	public JuelValueStack(ExpressionFactory factory, ValueStack vs) {
		this.factory = factory;
		setRoot(new CompoundRoot(vs.getRoot()));
	}

	public String findString(String expr) {
		return (String) findValue(expr, String.class);
	}

	public Object findValue(String expr) {
		return findValue(expr, Object.class);
	}

	public Object findValue(String expr, Class asType) {
		try {
			if (expr != null && expr.startsWith("#")) {
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
				expr = "$" + expr.substring(1);
			}
			if (expr != null && !expr.startsWith("${")) {
				expr = "${" + expr + "}";
			}
			// parse our expression
			ValueExpression valueExpr = factory.createValueExpression(
					elContext, expr, asType);
			Object retVal = valueExpr.getValue(elContext);
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
			if (expr != null && !expr.startsWith("${")) {
				expr = "${" + expr + "}";
			}
			// hack to allow parameters to be set back
			// juel doesn't support setting String[] values on String properties
			if (value != null && value instanceof String[]
					&& ((String[]) value).length == 1) {
				value = ((String[]) value)[0];
			}
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
		this.elContext = new CompoundRootELContext(root);
	}
}
