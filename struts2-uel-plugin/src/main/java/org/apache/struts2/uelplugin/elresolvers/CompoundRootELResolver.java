package org.apache.struts2.uelplugin.elresolvers;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;

/**
 * An ELResolver that is capable of resolving properties against the
 * CompoundRoot if available in the ELContext.
 */
public class CompoundRootELResolver extends ELResolver {

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		if (base == null) {
			return null;
		}

		return String.class;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object base) {
		// only resolve at the root of the context
		if (base != null) {
			return null;
		}

		CompoundRoot root = (CompoundRoot) context
				.getContext(CompoundRoot.class);
		if (root == null) {
			return null;
		}

		ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
		if (root.size() > 0) {
			FeatureDescriptor descriptor = new FeatureDescriptor();
			descriptor.setValue("type", root.get(0).getClass());
			descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
			list.add(descriptor);
		}

		for (Object bean : root) {
			BeanInfo info = null;
			try {
				info = Introspector.getBeanInfo(base.getClass());
			} catch (Exception ex) {
			}
			if (info != null) {
				for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
					pd.setValue("type", pd.getPropertyType());
					pd.setValue("resolvableAtDesignTime", Boolean.FALSE);
					list.add(pd);
				}
			}
		}
		return list.iterator();
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		// only resolve at the root of the context
		if (base != null) {
			return null;
		}

		CompoundRoot root = (CompoundRoot) context
				.getContext(CompoundRoot.class);
		if (root == null) {
			return null;
		}
		String propertyName = (String) property;
		Object bean = findObjectForProperty(root, propertyName);
		if (bean == null) {
			return null;
		}
		try {
			Class type = determineType(bean, propertyName);
			context.setPropertyResolved(true);
			return type;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException();
		}
		// only resolve at the root of the context
		if (base != null) {
			return null;
		}

		CompoundRoot root = (CompoundRoot) context
				.getContext(CompoundRoot.class);
		if (root == null) {
			return null;
		}
		String propertyName = (String) property;
		if ("top".equals(propertyName) && root.size() > 0) {
			return root.get(0);
		}
		try {
			Object bean = findObjectForProperty(root, propertyName);
			if (bean != null) {
				Object retVal = PropertyUtils.getProperty(bean, propertyName); 
				context.setPropertyResolved(true);
				return retVal;
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException();
		}

		return false;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		if (context == null) {
			throw new NullPointerException();
		}
		// only resolve at the root of the context
		if (base != null) {
			return;
		}

		CompoundRoot root = (CompoundRoot) context
				.getContext(CompoundRoot.class);
		String propertyName = (String) property;
		try {
			if (base == null && property != null && root != null) {
				Object bean = findObjectForProperty(root, propertyName);
				if (bean != null) {
					XWorkConverter converter = (XWorkConverter) context
							.getContext(XWorkConverter.class);
					if (converter != null && root != null) {
						Class propType = determineType(bean, propertyName);
						value = converter.convertValue(null, value, propType);
					}
					BeanUtils.setProperty(bean, propertyName, value);
					context.setPropertyResolved(true);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	protected Class<?> determineType(Object bean, String property)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return PropertyUtils.getPropertyType(bean, property);
	}

	protected Object findObjectForProperty(CompoundRoot root, String propertyName) {
		if ("top".equals(propertyName) && root.size() > 0) {
			return root.get(0);
		}
		for (int i = 0; i < root.size(); i++) {
			if (PropertyUtils.isReadable(root.get(i), propertyName)
					|| PropertyUtils.isWriteable(root.get(i), propertyName)) {
				return root.get(i);
			}
		}
		return null;
	}
}
