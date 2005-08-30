/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.script.el.util;

import org.apache.ti.script.el.ExpressionParseException;
import org.apache.ti.script.el.NetUIVariableResolver;
import org.apache.ti.script.el.ParsedExpression;
import org.apache.ti.script.el.parser.NetUIELParser;
import org.apache.ti.script.el.parser.ParseException;
import org.apache.ti.script.el.parser.TokenMgrError;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.internal.cache.PropertyCache;
import org.apache.ti.util.logging.Logger;
import org.apache.ti.util.type.TypeUtils;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 */
public class ParseUtils {

    private static final Logger LOGGER = Logger.getInstance(ParseUtils.class);

    private static final HashMap/*<String, ParsedExpression>*/ PARSED_CACHE = new HashMap/*<String, ParsedExpression>*/();

    /* do not construct */
    private ParseUtils() {
    }

    public static final ParsedExpression parse(String exprStr) {
        ParsedExpression pe = (ParsedExpression) PARSED_CACHE.get(exprStr);

        if (pe != null)
            return pe;

        try {
            NetUIELParser learn = new NetUIELParser(new StringReader(exprStr));

            ParsedExpression expr = learn.parse();
            expr.seal();

            /* infrequent; this should only happen when there is a cache miss */
            synchronized (PARSED_CACHE) {
                PARSED_CACHE.put(exprStr, expr);
            }

            return expr;
        } catch (ParseException e) {
            String msg = "Error occurred parsing expression \"" + exprStr + "\".";
            LOGGER.error(msg, e);
            throw new ExpressionParseException(msg, e);
        } catch (TokenMgrError tm) {
            String msg = "Error occurred parsing expression \"" + exprStr + "\".";
            LOGGER.error(msg, tm);
            throw new ExpressionParseException(msg, tm);
        }
    }

    public static final Object evaluate(String exprStr, NetUIVariableResolver vr) {
        ParsedExpression expr = parse(exprStr);
        assert expr != null;
        return expr.evaluate(vr);
    }

    public static final void update(String exprStr, Object value, NetUIVariableResolver vr) {
        ParsedExpression expr = parse(exprStr);
        assert expr != null;
        expr.update(value, vr);
    }

    public static final Class getPropertyType(Object value, String name, PropertyCache cache) {
        assert value != null;
        assert cache != null;

        Class type = value.getClass();

        Method m = cache.getPropertySetter(type, name);
        if (m == null) {
            String msg = "Can not find setter method for property \"" + name + "\" on object of type \"" + value.getClass() + "\".";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
        // PropertyCache guarantees that props are found and match JavaBean naming rules
        else {
            assert m.getParameterTypes().length == 1;
            return m.getParameterTypes()[0];
        }
    }

    public static final Object getProperty(Object value, String name, PropertyCache cache) {
        assert value != null;
        assert cache != null;

        Class type = value.getClass();

        Method m = cache.getPropertyGetter(type, name);
        if (m != null) {
            try {
                return m.invoke(value, (Object[]) null);
            } catch (Exception e) {
                String msg = "An error occurred invoking a getter for the property  \"" + name + "\" on an object of type \"" + type + "\".";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

        String msg = "Could not find JavaBean property named \"" + name + "\" on object of type \"" + type + "\"";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
    }

    public static final Object convertType(Object value, Class toType) {
        assert toType != null;

        try {
            boolean sourceIsArray = false;

            /* only convert String types; other Object types are already assumed to be in their target types. */
            if (value != null && !(value instanceof String || (sourceIsArray = (value instanceof String[]))))
                return value;

            /* for a String[], convert each item in the array into its target type and return the resulting array. */
            if (toType.isArray()) {
                if (value == null)
                    return null;
                else {
                    Class compType = toType.getComponentType();

                    String[] strs = null;
                    if (value.getClass().isArray())
                        strs = (String[]) value;
                    else
                        strs = new String[]{(String) value};

                    Object tgt = Array.newInstance(compType, strs.length);

                    for (int i = 0; i < strs.length; i++) {
                        Object o = null;
                        try {
                            /* todo: support getting the Locale here in an ExpressionContext object */
                            o = TypeUtils.convertToObject(strs[i], compType);
                        } catch (IllegalArgumentException e) {
                            String msg = "Can not set Object types via expressions that are not supported by the set of registered type converters.  Cause: " + e;
                            LOGGER.error(msg, e);
                            throw new RuntimeException(msg, e);
                        }

                        Array.set(tgt, i, o);
                    }

                    return tgt;
                }
            }
            // convert the String into its target type and return the result
            else {
                // If the "value" is multi-valued (String[]), it needs to be converted into a single-valued object.
                // There is no policy defined for how we do this right now, so the first one will always win when 
                // multiple expressions reference the same property.  When that property is a String type, the result
                // is an HttpServletRequest that contains a String[], and here, we'll always the String[0].
                if (sourceIsArray) {
                    assert value instanceof String[];
                    assert Array.getLength(value) > 0 && Array.getLength(value) - 1 >= 0;

                    value = Array.get(value, Array.getLength(value) - 1);
                }

                try {
                    assert value == null || value instanceof String;

                    return TypeUtils.convertToObject((String) value, toType);
                } catch (IllegalArgumentException e) {
                    String msg = "The type \"" + toType.getName() + "\" can not be set through the NetUI expression language.";
                    LOGGER.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            }
        } catch (Exception e) {
            String msg = "Unable to convert a value of type \"" + value.getClass() + "\" to the array element type of \"" + toType + "\".  Cause: " + e;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public static String getContextString(String[] contexts) {
        InternalStringBuilder builder = new InternalStringBuilder();
        builder.append("[");
        if (contexts != null) {
            for (int i = 0; i < contexts.length; i++) {
                if (i > 0) builder.append(", ");
                builder.append(contexts[i]);
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static Throwable getRootCause(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null)
            root = root.getCause();
        return root;
    }
}
