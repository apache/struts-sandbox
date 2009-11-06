package org.apache.struts2.uelplugin.reflection;


import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;


/**
 * Taken from OGNL
 */
public class GenericReflectionProvider implements ReflectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(GenericReflectionProvider.class);

    static final ClassCache _fieldCache = new ClassCacheImpl();
    static final List _superclasses = new ArrayList();
    static final ClassCache[] _declaredMethods = new ClassCache[]{new ClassCacheImpl(), new ClassCacheImpl()};
    static final Map _primitiveTypes = new HashMap(101);
    static final ClassCache _primitiveDefaults = new ClassCacheImpl();
    static final Map _methodParameterTypesCache = new HashMap(101);
    static final Map _genericMethodParameterTypesCache = new HashMap(101);
    static final Map _ctorParameterTypesCache = new HashMap(101);
    static final ClassCache _propertyDescriptorCache = new ClassCacheImpl();
    static final ClassCache _staticMethodCache = new ClassCacheImpl();
    static final ClassCache _instanceMethodCache = new ClassCacheImpl();
    static SecurityManager _securityManager = System.getSecurityManager();
    public static final MemberAccess DEFAULT_MEMBER_ACCESS = new DefaultMemberAccess(false);

    private MemberAccess _memberAccess = DEFAULT_MEMBER_ACCESS;

    public static final Object NotFound = new Object();
    public static final List NotFoundList = new ArrayList();
    public static final Map NotFoundMap = new HashMap();
    public static final Object[] NoArguments = new Object[]{};
    public static final Class[] NoArgumentTypes = new Class[]{};

    private static final String SET_PREFIX = "set";
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    private static boolean _jdk15 = false;
    private static boolean _jdkChecked = false;
    private static boolean devMode;
    private XWorkConverter xworkConverter;

    public static int INDEXED_PROPERTY_NONE = 0;
    public static int INDEXED_PROPERTY_INT = 1;
    public static int INDEXED_PROPERTY_OBJECT = 2;
    private static final Object[] EMPTY_PROPERTY_DESCRIPTORS_ARRAY = new PropertyDescriptor[0];

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }


    public Field getField(Class inClass, String name) {
        Field result = null;

        synchronized (_fieldCache) {
            Object o = getFields(inClass).get(name);

            if (o == null) {
                _superclasses.clear();
                for (Class sc = inClass; (sc != null); sc = sc.getSuperclass()) {
                    if ((o = getFields(sc).get(name)) == NotFound)
                        break;

                    _superclasses.add(sc);

                    if ((result = (Field) o) != null)
                        break;
                }
                /*
                 * Bubble the found value (either cache miss or actual field) to all supeclasses
                 * that we saw for quicker access next time.
                 */
                for (int i = 0, icount = _superclasses.size(); i < icount; i++) {
                    getFields((Class) _superclasses.get(i)).put(name, (result == null) ? NotFound : result);
                }
            } else {
                if (o instanceof Field) {
                    result = (Field) o;
                } else {
                    if (result == NotFound)
                        result = null;
                }
            }
        }
        return result;
    }

    public static Map getFields(Class targetClass) {
        Map result;

        synchronized (_fieldCache) {
            if ((result = (Map) _fieldCache.get(targetClass)) == null) {
                Field fa[];

                result = new HashMap(23);
                fa = targetClass.getDeclaredFields();
                for (int i = 0; i < fa.length; i++) {
                    result.put(fa[i].getName(), fa[i]);
                }
                _fieldCache.put(targetClass, result);
            }
        }
        return result;
    }

    public Method getGetMethod(Class targetClass, String propertyName) {
        Method result = null;


        List methods = getDeclaredMethods(targetClass, propertyName, false /* find 'get' methods */);

        if (methods != null) {
            for (int i = 0, icount = methods.size(); i < icount; i++) {
                Method m = (Method) methods.get(i);
                Class[] mParameterTypes = findParameterTypes(targetClass, m); //getParameterTypes(m);

                if (mParameterTypes.length == 0) {
                    result = m;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Finds the appropriate parameter types for the given {@link Method} and
     * {@link Class} instance of the type the method is associated with.  Correctly
     * finds generic types if running in >= 1.5 jre as well.
     *
     * @param type The class type the method is being executed against.
     * @param m    The method to find types for.
     * @return Array of parameter types for the given method.
     */
    public static Class[] findParameterTypes(Class type, Method m) {
        if (type == null) {
            return getParameterTypes(m);
        }

        if (!isJdk15()
                || type.getGenericSuperclass() == null
                || !ParameterizedType.class.isInstance(type.getGenericSuperclass())
                || m.getDeclaringClass().getTypeParameters() == null) {
            return getParameterTypes(m);
        }

        synchronized (_genericMethodParameterTypesCache) {
            Class[] types;

            if ((types = (Class[]) _genericMethodParameterTypesCache.get(m)) != null) {
                return types;
            }

            ParameterizedType param = (ParameterizedType) type.getGenericSuperclass();
            Type[] genTypes = m.getGenericParameterTypes();
            TypeVariable[] declaredTypes = m.getDeclaringClass().getTypeParameters();

            types = new Class[genTypes.length];

            typeSearch:
            for (int i = 0; i < genTypes.length; i++) {
                TypeVariable paramType = null;

                if (TypeVariable.class.isInstance(genTypes[i])) {
                    paramType = (TypeVariable) genTypes[i];
                } else if (GenericArrayType.class.isInstance(genTypes[i])) {
                    paramType = (TypeVariable) ((GenericArrayType) genTypes[i]).getGenericComponentType();
                } else if (Class.class.isInstance(genTypes[i])) {
                    types[i] = (Class) genTypes[i];
                    continue;
                }

                Class resolved = resolveType(param, paramType, declaredTypes);

                if (resolved != null) {
                    if (GenericArrayType.class.isInstance(genTypes[i])) {
                        resolved = Array.newInstance(resolved, 0).getClass();
                    }

                    types[i] = resolved;
                    continue;
                }

                types[i] = m.getParameterTypes()[i];
            }

            _genericMethodParameterTypesCache.put(m, types);

            return types;
        }
    }

    static Class resolveType(ParameterizedType param, TypeVariable var, TypeVariable[] declaredTypes) {
        if (param.getActualTypeArguments().length < 1)
            return null;

        for (int i = 0; i < declaredTypes.length; i++) {
            if (!TypeVariable.class.isInstance(param.getActualTypeArguments()[i])
                    && declaredTypes[i].getName().equals(var.getName())) {
                return (Class) param.getActualTypeArguments()[i];
            }
        }

        return null;
    }


    /**
     * Returns the parameter types of the given method.
     */
    public static Class[] getParameterTypes(Method m) {
        synchronized (_methodParameterTypesCache) {
            Class[] result;

            if ((result = (Class[]) _methodParameterTypesCache.get(m)) == null) {
                _methodParameterTypesCache.put(m, result = m.getParameterTypes());
            }
            return result;
        }
    }

    public static List getDeclaredMethods(Class targetClass, String propertyName, boolean findSets) {
        List result = null;
        ClassCache cache = _declaredMethods[findSets ? 0 : 1];

        synchronized (cache) {
            Map propertyCache = (Map) cache.get(targetClass);

            if ((propertyCache == null) || ((result = (List) propertyCache.get(propertyName)) == null)) {

                String baseName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

                for (Class c = targetClass; c != null; c = c.getSuperclass()) {
                    Method[] methods = c.getDeclaredMethods();

                    for (int i = 0; i < methods.length; i++) {

                        if (!isMethodCallable(methods[i]))
                            continue;

                        String ms = methods[i].getName();

                        if (ms.endsWith(baseName)) {
                            boolean isSet = false, isIs = false;

                            if ((isSet = ms.startsWith(SET_PREFIX)) || ms.startsWith(GET_PREFIX)
                                    || (isIs = ms.startsWith(IS_PREFIX))) {
                                int prefixLength = (isIs ? 2 : 3);

                                if (isSet == findSets) {
                                    if (baseName.length() == (ms.length() - prefixLength)) {
                                        if (result == null) {
                                            result = new ArrayList();
                                        }
                                        result.add(methods[i]);
                                    }
                                }
                            }
                        }
                    }
                }
                if (propertyCache == null) {
                    cache.put(targetClass, propertyCache = new HashMap(101));
                }

                propertyCache.put(propertyName, (result == null) ? NotFoundList : result);
            }
            return (result == NotFoundList) ? null : result;
        }
    }

    static boolean isMethodCallable(Method m) {
        if ((isJdk15() && m.isSynthetic()) || Modifier.isVolatile(m.getModifiers()))
            return false;

        return true;
    }

    public static boolean isJdk15() {
        if (_jdkChecked)
            return _jdk15;

        try {
            Class.forName("java.lang.annotation.Annotation");
            _jdk15 = true;
        } catch (Exception e) { /* ignore */ }

        _jdkChecked = true;

        return _jdk15;
    }

    public Method getSetMethod(Class targetClass, String propertyName) {
        Method result = null;

        List methods = getDeclaredMethods(targetClass, propertyName, true /* find 'set' methods */);

        if (methods != null) {
            for (int i = 0, icount = methods.size(); i < icount; i++) {
                Method m = (Method) methods.get(i);
                Class[] mParameterTypes = findParameterTypes(targetClass, m); //getParameterTypes(m);

                if (mParameterTypes.length == 1) {
                    result = m;
                    break;
                }
            }
        }

        return result;
    }

    public void setProperties(Map<String, String> props, Object o, Map<String, Object> context) {
        setProperties(props, o, context, false);
    }

    public void setProperties(Map<String, String> props, Object o, Map<String, Object> context,
                              boolean throwPropertyExceptions) throws ReflectionException {
        for (Map.Entry<String, ?> entry : props.entrySet()) {
            String expression = entry.getKey();
            setProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
        }
    }

    public void setProperties(Map<String, String> properties, Object o) {
        setProperties(properties, o, null);
    }

    public PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        if (targetClass == null)
            return null;

        return (PropertyDescriptor) internalGetPropertyDescriptors(targetClass).get(propertyName);
    }

    public void copy(Object from, Object to, Map<String, Object> context,
                     Collection<String> exclusions, Collection<String> inclusions) {
        if (from == null || to == null) {
            LOG.warn("Attempting to copy from or to a null source. This is illegal and is bein skipped. This may be due to an error in an OGNL expression, action chaining, or some other event.");

            return;
        }

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = getPropertyDescriptors(from);
            toPds = getPropertyDescriptors(to);
        } catch (IntrospectionException e) {
            LOG.error("An error occured", e);

            return;
        }

        Map<String, PropertyDescriptor> toPdHash = new HashMap<String, PropertyDescriptor>();

        for (PropertyDescriptor toPd : toPds) {
            toPdHash.put(toPd.getName(), toPd);
        }

        for (PropertyDescriptor fromPd : fromPds) {
            if (fromPd.getReadMethod() != null) {
                boolean copy = true;
                if (exclusions != null && exclusions.contains(fromPd.getName())) {
                    copy = false;
                } else if (inclusions != null && !inclusions.contains(fromPd.getName())) {
                    copy = false;
                }

                if (copy == true) {
                    PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            String expr = fromPd.getName();
                            Object value = getValue(expr, context, from);
                            setValue(expr, context, to, value);
                        } catch (Exception e) {
                            // ignore, this is OK
                        }
                    }
                }
            }
        }
    }

    /**
     * Looks for the real target with the specified property given a root Object which may be a
     * CompoundRoot.
     *
     * @return the real target or null if no object can be found with the specified property
     */
    public Object getRealTarget(String property, Map<String, Object> context, Object root) {
        //special keyword, they must be cutting the stack
        if ("top".equals(property)) {
            return root;
        }

        if (root instanceof CompoundRoot) {
            // find real target
            CompoundRoot cr = (CompoundRoot) root;

            try {
                for (Object target : cr) {
                    if (
                            hasSetProperty(context, target, property)
                                    ||
                                    hasGetProperty(context, target, property)
                                    ||
                                    getIndexedPropertyType(context, target.getClass(), property) != INDEXED_PROPERTY_NONE
                            ) {
                        return target;
                    }
                }
            } catch (IntrospectionException ex) {
                throw new ReflectionException("Cannot figure out real target class", ex);
            }

            return null;
        }

        return root;
    }

    /**
     * Determines the index property type, if any. Returns <code>INDEXED_PROPERTY_NONE</code> if
     * the property is not index-accessible as determined by OGNL or JavaBeans. If it is indexable
     * then this will return whether it is a JavaBeans indexed property, conforming to the indexed
     * property patterns (returns <code>INDEXED_PROPERTY_INT</code>) or if it conforms to the
     * OGNL arbitrary object indexable (returns <code>INDEXED_PROPERTY_OBJECT</code>).
     */
    public int getIndexedPropertyType(Map<String, Object> context, Class sourceClass, String name) {
        int result = INDEXED_PROPERTY_NONE;

        try {
            PropertyDescriptor pd = getPropertyDescriptor(sourceClass, name);
            if (pd != null) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    result = INDEXED_PROPERTY_INT;
                } else {
                    if (pd instanceof ognl.ObjectIndexedPropertyDescriptor) {
                        result = INDEXED_PROPERTY_OBJECT;
                    }
                }
            }
        } catch (Exception ex) {
            throw new ReflectionException("problem determining if '" + name + "' is an indexed property", ex);
        }
        return result;
    }

    public final boolean hasSetProperty(Map<String, Object> context, Object target, Object oname)
            throws IntrospectionException {
        Class targetClass = (target == null) ? null : target.getClass();
        String name = oname.toString();

        return hasSetMethod(context, target, targetClass, name) || hasField(context, target, targetClass, name);
    }

    public final boolean hasSetMethod(Map<String, Object> context, Object target, Class targetClass, String propertyName)
            throws IntrospectionException {
        return isMethodAccessible(context, target, getSetMethod(targetClass, propertyName), propertyName);
    }

    public boolean isMethodAccessible(Map<String, Object> context, Object target, Method method, String propertyName) {
        return (method != null) && _memberAccess.isAccessible(context, target, method, propertyName);
    }

    public final boolean hasGetProperty(Map<String, Object> context, Object target, Object oname)
            throws IntrospectionException {
        Class targetClass = (target == null) ? null : target.getClass();
        String name = oname.toString();

        return hasGetMethod(context, target, targetClass, name) || hasField(context, target, targetClass, name);
    }

    public boolean hasGetMethod(Map<String, Object> context, Object target, Class targetClass, String propertyName)
            throws IntrospectionException {
        return isMethodAccessible(context, target, getGetMethod(targetClass, propertyName), propertyName);
    }

    public boolean hasField(Map<String, Object> context, Object target, Class inClass, String propertyName) {
        Field f = getField(inClass, propertyName);

        return (f != null) && isFieldAccessible(context, target, f, propertyName);
    }

    public boolean isFieldAccessible(Map<String, Object> context, Object target, Class inClass, String propertyName) {
        return isFieldAccessible(context, target, getField(inClass, propertyName), propertyName);
    }

    public boolean isFieldAccessible(Map<String, Object> context, Object target, Field field, String propertyName) {
        return _memberAccess.isAccessible(context, target, field, propertyName);
    }


    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        setProperty(name, value, o, context, false);
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        try {
            setValue(name, context, o, value);
        } catch (Exception e) {
            String msg = "Caught an Exception while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";

            if (throwPropertyExceptions) {
                throw new ReflectionException(msg, e);
            } else {
                if (devMode) {
                    LOG.warn(msg, e);
                }
            }
        }
    }

    /**
     * Creates a Map with read properties for the given source object.
     * <p/>
     * If the source object does not have a read property (i.e. write-only) then
     * the property is added to the map with the value <code>here is no read method for property-name</code>.
     *
     * @param source the source object.
     * @return a Map with (key = read property name, value = value of read property).
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public Map getBeanMap(Object source) throws IntrospectionException {
        Map beanMap = new HashMap();
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                Object value = getValue(propertyName, Collections.EMPTY_MAP, source);
                beanMap.put(propertyName, value);
            } else {
                beanMap.put(propertyName, "There is no read method for " + propertyName);
            }
        }
        return beanMap;
    }

    public Object getValue(String expression, Map<String, Object> context, Object root)
            throws ReflectionException {
        Method method = getGetMethod(root.getClass(), expression);

        if (method != null) {
            try {
                return method.invoke(root, NoArguments);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ReflectionException("Unable to find get method for [" + expression + "]");
        }

    }

    public void setValue(String expression, Map<String, Object> context, Object root,
                         Object value) throws ReflectionException {
        Method method = getSetMethod(root.getClass(), expression);
        if (method != null) {
            if (value != null) {
                //convert type if needed
                Class expectedType = method.getParameterTypes()[0];
                Class valueType = value.getClass();
                if (!expectedType.isAssignableFrom(valueType)) {
                    value = xworkConverter.convertValue(context, value, expectedType);
                }
            }

            try {
                method.invoke(root, value);
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        } else {
            throw new ReflectionException("Unable to find set method for [" + expression + "]");
        }
    }

    public Map internalGetPropertyDescriptors(Class targetClass) throws IntrospectionException {
        Map result;

        synchronized (_propertyDescriptorCache) {
            if ((result = (Map) _propertyDescriptorCache.get(targetClass)) == null) {
                PropertyDescriptor[] pda = Introspector.getBeanInfo(targetClass).getPropertyDescriptors();

                result = new HashMap(101);
                for (int i = 0, icount = pda.length; i < icount; i++) {
                    // workaround for Introspector bug 6528714 (bugs.sun.com)
                    if (pda[i].getReadMethod() != null && !isMethodCallable(pda[i].getReadMethod())) {
                        pda[i].setReadMethod(findClosestMatchingMethod(targetClass, pda[i].getReadMethod(), pda[i].getName(),
                                pda[i].getPropertyType(), true));
                    }
                    if (pda[i].getWriteMethod() != null && !isMethodCallable(pda[i].getWriteMethod())) {
                        pda[i].setWriteMethod(findClosestMatchingMethod(targetClass, pda[i].getWriteMethod(), pda[i].getName(),
                                pda[i].getPropertyType(), false));
                    }

                    result.put(pda[i].getName(), pda[i]);
                }

                findObjectIndexedPropertyDescriptors(targetClass, result);
                _propertyDescriptorCache.put(targetClass, result);
            }
        }

        return result;
    }

    static Method findClosestMatchingMethod(Class targetClass, Method m, String propertyName,
                                            Class propertyType, boolean isReadMethod) {
        List methods = getDeclaredMethods(targetClass, propertyName, !isReadMethod);

        for (int i = 0; i < methods.size(); i++) {
            Method method = (Method) methods.get(i);

            if (method.getName().equals(m.getName())
                    && m.getReturnType().isAssignableFrom(m.getReturnType())
                    && method.getReturnType() == propertyType
                    && method.getParameterTypes().length == m.getParameterTypes().length) {
                return method;
            }
        }

        return m;
    }

    static void findObjectIndexedPropertyDescriptors(Class targetClass, Map intoMap)
            throws ReflectionException {
        Map allMethods = getMethods(targetClass, false);
        Map pairs = new HashMap(101);

        for (Iterator it = allMethods.keySet().iterator(); it.hasNext();) {
            String methodName = (String) it.next();
            List methods = (List) allMethods.get(methodName);

            /*
             * Only process set/get where there is exactly one implementation of the method per
             * class and those implementations are all the same
             */
            if (indexMethodCheck(methods)) {
                boolean isGet = false, isSet = false;
                Method m = (Method) methods.get(0);

                if (((isSet = methodName.startsWith(SET_PREFIX)) || (isGet = methodName.startsWith(GET_PREFIX)))
                        && (methodName.length() > 3)) {
                    String propertyName = Introspector.decapitalize(methodName.substring(3));
                    Class[] parameterTypes = getParameterTypes(m);
                    int parameterCount = parameterTypes.length;

                    if (isGet && (parameterCount == 1) && (m.getReturnType() != Void.TYPE)) {
                        List pair = (List) pairs.get(propertyName);

                        if (pair == null) {
                            pairs.put(propertyName, pair = new ArrayList());
                        }
                        pair.add(m);
                    }
                    if (isSet && (parameterCount == 2) && (m.getReturnType() == Void.TYPE)) {
                        List pair = (List) pairs.get(propertyName);

                        if (pair == null) {
                            pairs.put(propertyName, pair = new ArrayList());
                        }
                        pair.add(m);
                    }
                }
            }
        }

        for (Iterator it = pairs.keySet().iterator(); it.hasNext();) {
            String propertyName = (String) it.next();
            List methods = (List) pairs.get(propertyName);

            if (methods.size() == 2) {
                Method method1 = (Method) methods.get(0), method2 = (Method) methods.get(1), setMethod = (method1
                        .getParameterTypes().length == 2) ? method1 : method2, getMethod = (setMethod == method1) ? method2
                        : method1;
                Class keyType = getMethod.getParameterTypes()[0], propertyType = getMethod.getReturnType();

                if (keyType == setMethod.getParameterTypes()[0]) {
                    if (propertyType == setMethod.getParameterTypes()[1]) {
                        ObjectIndexedPropertyDescriptor propertyDescriptor;

                        try {
                            propertyDescriptor = new ObjectIndexedPropertyDescriptor(propertyName, propertyType,
                                    getMethod, setMethod);
                        } catch (Exception ex) {
                            throw new ReflectionException("creating object indexed property descriptor for '" + propertyName
                                    + "' in " + targetClass, ex);
                        }
                        intoMap.put(propertyName, propertyDescriptor);
                    }
                }

            }
        }
    }

    public static Map getMethods(Class targetClass, boolean staticMethods) {
        ClassCache cache = (staticMethods ? _staticMethodCache : _instanceMethodCache);
        Map result;

        synchronized (cache) {
            if ((result = (Map) cache.get(targetClass)) == null) {
                cache.put(targetClass, result = new HashMap(23));

                for (Class c = targetClass; c != null; c = c.getSuperclass()) {
                    Method[] ma = c.getDeclaredMethods();

                    for (int i = 0, icount = ma.length; i < icount; i++) {
                        // skip over synthetic methods

                        if (!isMethodCallable(ma[i]))
                            continue;

                        if (Modifier.isStatic(ma[i].getModifiers()) == staticMethods) {
                            List ml = (List) result.get(ma[i].getName());

                            if (ml == null)
                                result.put(ma[i].getName(), ml = new ArrayList());

                            ml.add(ma[i]);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static final boolean indexMethodCheck(List methods) {
        boolean result = false;

        if (methods.size() > 0) {
            Method fm = (Method) methods.get(0);
            Class[] fmpt = getParameterTypes(fm);
            int fmpc = fmpt.length;
            Class lastMethodClass = fm.getDeclaringClass();

            result = true;
            for (int i = 1; result && (i < methods.size()); i++) {
                Method m = (Method) methods.get(i);
                Class c = m.getDeclaringClass();

                // Check to see if more than one method implemented per class
                if (lastMethodClass == c) {
                    result = false;
                } else {
                    Class[] mpt = getParameterTypes(fm);
                    int mpc = fmpt.length;

                    if (fmpc != mpc) {
                        result = false;
                    }
                    for (int j = 0; j < fmpc; j++) {
                        if (fmpt[j] != mpt[j]) {
                            result = false;
                            break;
                        }
                    }
                }
                lastMethodClass = c;
            }
        }
        return result;
    }

    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        return (PropertyDescriptor[]) internalGetPropertyDescriptors(source.getClass()).values().toArray(EMPTY_PROPERTY_DESCRIPTORS_ARRAY);
    }
}
