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
package org.apache.ti.util.type;

import org.apache.ti.util.Bundle;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.config.bean.TypeConverterConfig;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.io.InputStream;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.text.DateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 *
 *
 */
public final class TypeUtils {
    private static final Logger LOGGER = Logger.getInstance(TypeUtils.class);
    private static final String TYPE_CONVERTER_PROPERTIES = "/properties/netui-typeconverter.properties";
    private static final String EMPTY_STRING = "";
    private static final HashMap /*<Class, BaseTypeConverter>*/ TYPE_CONVERTERS = new HashMap /*<Class, BaseTypeConverter>*/();
    private static String TO_STRING = null;

    static {
        // initialize the default type converters
        initialize();

        Map /*<String, String>*/ map = readFromConfig();

        if (map != null) {
            load(map);
            map.clear();
        }

        map = readFromProperties();

        if (map != null) {
            load(map);
        }

        LOGGER.info(registeredConvertersToString());
    }

    /* do not construct */
    private TypeUtils() {
    }

    /**
     * Convert an object from a String to the given type.
     *
     * @param value the String to convert
     * @param type  the type to which to convert the String
     * @return the Object result of converting the String to the type.
     * @throws TypeConverterNotFoundException if a TypeConverter for the target type can not be found.
     * @deprecated
     */
    public static final Object convertToObject(String value, Class type) {
        return convertToObject(value, type, null);
    }

    /**
     * Convert an object from a String to the given type using the specified {@link java.util.Locale}.
     * <p/>
     * The locale is optionally used depending on the implementation of the {@link TypeConverter} that is used.
     *
     * @param value  the String to convert
     * @param type   the type to which to convert the String
     * @param locale the locale to use during conversion
     * @return the Object result of converting the String to the type.
     * @throws TypeConverterNotFoundException if a TypeConverter for the target type can not be found.
     */
    public static final Object convertToObject(String value, Class type, Locale locale) {
        BaseTypeConverter converter = lookupTypeConverter(type);
        assert converter != null;

        return converter.convertToObject(type, value, locale);
    }

    public static final byte convertToByte(String value) {
        return ((Byte) convertToObject(value, byte.class, null)).byteValue();
    }

    public static final boolean convertToBoolean(String value) {
        return ((Boolean) convertToObject(value, boolean.class, null)).booleanValue();
    }

    public static final char convertToChar(String value) {
        return ((Character) convertToObject(value, char.class, null)).charValue();
    }

    public static final double convertToDouble(String value) {
        return ((Double) convertToObject(value, double.class, null)).doubleValue();
    }

    public static final float convertToFloat(String value) {
        return ((Float) convertToObject(value, float.class, null)).floatValue();
    }

    public static final int convertToInt(String value) {
        return ((Integer) convertToObject(value, int.class, null)).intValue();
    }

    public static final long convertToLong(String value) {
        return ((Long) convertToObject(value, long.class, null)).longValue();
    }

    public static final short convertToShort(String value) {
        return ((Short) convertToObject(value, short.class, null)).shortValue();
    }

    public static final Byte convertToByteObject(String value) {
        return (Byte) convertToObject(value, Byte.class, null);
    }

    public static final Boolean convertToBooleanObject(String value) {
        return (Boolean) convertToObject(value, Boolean.class, null);
    }

    public static final Character convertToCharacterObject(String value) {
        return (Character) convertToObject(value, Character.class, null);
    }

    public static final Double convertToDoubleObject(String value) {
        return (Double) convertToObject(value, Double.class, null);
    }

    public static final Float convertToFloatObject(String value) {
        return (Float) convertToObject(value, Float.class, null);
    }

    public static final Integer convertToIntegerObject(String value) {
        return (Integer) convertToObject(value, Integer.class, null);
    }

    public static final Long convertToLongObject(String value) {
        return (Long) convertToObject(value, Long.class, null);
    }

    public static final Short convertToShortObject(String value) {
        return (Short) convertToObject(value, Short.class, null);
    }

    /**
     * Internal method used to lookup a {@link BaseTypeConverter}.
     *
     * @param type the target conversion type
     * @return a {@link BaseTypeConverter} to use for conversion
     * @throws TypeConverterNotFoundException if a TypeConverter for the target type can not be found.
     */
    private static final BaseTypeConverter lookupTypeConverter(Class type) {
        BaseTypeConverter converter = (BaseTypeConverter) TYPE_CONVERTERS.get(type);

        if (converter == null) {
            String msg = "Could not find a TypeConverter for converting a String to an object of type \"" +
                         ((type != null) ? type.getName() : null) + "\"";
            TypeConverterNotFoundException tcn = new TypeConverterNotFoundException(msg);

            if (type == null) {
                msg = Bundle.getErrorString("TypeUtils_nullType");
            } else {
                msg = Bundle.getErrorString("TypeUtils_noConverterForType", new Object[] { type.getName() });
            }

            tcn.setLocalizedMessage(msg);
            LOGGER.error(msg);

            throw tcn;
        }

        return converter;
    }

    private static String registeredConvertersToString() {
        if (TO_STRING != null) {
            return TO_STRING;
        }

        InternalStringBuilder sb = new InternalStringBuilder(256);
        sb.append(TypeUtils.class.getName() + " regestered converters (class name, TypeConverter implementation):\n");
        sb.append(":::::\n");

        Iterator iterator = TYPE_CONVERTERS.keySet().iterator();

        while (iterator.hasNext()) {
            Class key = (Class) iterator.next();
            String keyName = key.getName();
            String value = ((TYPE_CONVERTERS.get(key) != null) ? TYPE_CONVERTERS.get(key).getClass().getName() : "null");
            sb.append(keyName);
            sb.append(", ");
            sb.append(value);
            sb.append("\n");
        }

        sb.append(":::::\n");
        TO_STRING = sb.toString();

        return TO_STRING;
    }

    private static Map /*<String, String>*/ readFromProperties() {
        Properties props = null;
        InputStream is = null;

        try {
            is = (TypeUtils.class).getClassLoader().getResourceAsStream(TYPE_CONVERTER_PROPERTIES);

            LOGGER.debug("found type converter InputStream at " + TYPE_CONVERTER_PROPERTIES + " " +
                         ((is != null) ? "true" : "false"));

            if (is == null) {
                return null;
            }

            props = new Properties();
            props.load(is);
        } catch (Exception e) {
            LOGGER.warn("Error occurred reading type converter properties file", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception ignore) {
            }
        }

        LinkedHashMap /*<String, String>*/ map = new LinkedHashMap /*<String, String>*/();
        Enumeration e = props.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            map.put(key, props.getProperty(key));
        }

        return map;
    }

    private static final Map /*<String, String>*/ readFromConfig() {
        NetUIConfig config = ConfigUtil.getConfig();

        if (config == null) {
            return null;
        }

        TypeConverterConfig[] typeConverters = config.getTypeConverters();

        if (typeConverters != null) {
            LinkedHashMap /*<String, String>*/ map = new LinkedHashMap /*<String, String>*/();

            for (int i = 0; i < typeConverters.length; i++) {
                map.put(typeConverters[i].getType(), typeConverters[i].getConverterClass());
            }

            return map;
        } else {
            return null;
        }
    }

    private static void load(Map /*<String, String>*/ map) {
        // load the properties and continue to populate the map
        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String className = (String) map.get(key);

            if (((key == null) || key.equals(EMPTY_STRING)) || ((className == null) || className.equals(EMPTY_STRING))) {
                LOGGER.warn("Could not create a TypeConverter for type \"" + key + "\" and TypeConverter \"" + className + "\"");

                continue;
            }

            Class targetClazz = null;

            /* attempt to load the "convert-to" class */
            try {
                targetClazz = Class.forName(key);
            } catch (ClassNotFoundException cnf) {
                LOGGER.warn("Could not create a TypeConverter for type \"" + key +
                            "\" because the \"convert-to\" type could not be found.");

                continue;
            }

            Class tcClazz = null;
            BaseTypeConverter tc = null;

            // try to find the TypeConverter implementation
            try {
                tcClazz = Class.forName(className);

                Object obj = tcClazz.newInstance();

                // this supports existing TypeConverter implementations
                // but allows TypeUtils make calls against the BaseTypeConverter
                // API, which supports Locale-based conversion
                if (obj instanceof TypeConverter) {
                    tc = new DelegatingTypeConverter((TypeConverter) obj);
                } else if (obj instanceof BaseTypeConverter) {
                    tc = (BaseTypeConverter) obj;
                } else {
                    throw new IllegalStateException("Attempt to load illegal type converter type: " + tcClazz);
                }
            } catch (ClassNotFoundException cnf) {
                LOGGER.warn("Could not create a TypeConverter for type \"" + key +
                            "\" because the TypeConverter implementation class \"" +
                            ((tcClazz != null) ? tcClazz.getName() : null) + "\" could not be found.");

                continue;
            } catch (Exception e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Could not create a TypeConverter for type \"" + key + "\" because the implementation class \"" +
                                ((tcClazz != null) ? tcClazz.getName() : null) + "\" could not be instantiated.");
                }

                continue;
            }

            /* found two type converters for the same class -- warn */
            if (TYPE_CONVERTERS.containsKey(targetClazz)) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Overwriting a previously defined TypeConverter named \"" + targetClazz +
                                "\" with a new TypeConverter implementation of type \"" + className + "\"");
                }
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding a type converter; target type=\"" + targetClazz.getName() +
                            "\" TypeConverter implementation=\"" + tc.getClass().getName() + "\"");
            }

            TYPE_CONVERTERS.put(targetClazz, tc);
        }
    }

    /**
     * Create converters that take an Object representing a value and convert
     * that value, based on its type, to a String.  Includes all primitive types,
     * primitive wrappers, String, and BigDecimal.  Types like BigDecimal are included
     * because JDBC uses these complex types to map SQL types to Java objects.
     */
    private static void initialize() {
        TYPE_CONVERTERS.put(byte.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    return (((value == null) || value.equals(EMPTY_STRING)) ? new Byte((byte) 0) : new Byte(value.trim()));
                }
            });
        TYPE_CONVERTERS.put(Byte.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, byte.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(boolean.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return Boolean.FALSE;
                    }

                    value = value.toLowerCase().trim();

                    if (value.equals("on") || value.equals("true")) {
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                }
            });
        TYPE_CONVERTERS.put(Boolean.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, boolean.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(char.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Character('\u0000');
                    } else {
                        return new Character(value.charAt(0));
                    }
                }
            });
        TYPE_CONVERTERS.put(Character.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, char.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(double.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Double(0.0);
                    } else {
                        return new Double(value.trim());
                    }
                }
            });
        TYPE_CONVERTERS.put(Double.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, double.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(float.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Float(0.0);
                    } else {
                        return new Float(value.trim());
                    }
                }
            });
        TYPE_CONVERTERS.put(Float.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, float.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(int.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Integer(0);
                    } else {
                        return new Integer(value.trim());
                    }
                }
            });
        TYPE_CONVERTERS.put(Integer.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, int.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(long.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Long(0);
                    } else {
                        return new Long(value.trim());
                    }
                }
            });
        TYPE_CONVERTERS.put(Long.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, long.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(short.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return new Short((short) 0);
                    } else {
                        return new Short(value.trim());
                    }
                }
            });
        TYPE_CONVERTERS.put(Short.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return TypeUtils.convertToObject(value, short.class, null);
                    }
                }
            });

        TYPE_CONVERTERS.put(String.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if (value == null) {
                        return null;
                    } else {
                        return value;
                    }
                }
            });

        TYPE_CONVERTERS.put(java.math.BigDecimal.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return new BigDecimal(value.trim());
                    }
                }
            });

        TYPE_CONVERTERS.put(java.math.BigInteger.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return new BigInteger(value.trim());
                    }
                }
            });

        TYPE_CONVERTERS.put(byte[].class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        return value.getBytes();
                    }
                }
            });

        TYPE_CONVERTERS.put(Byte[].class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    } else {
                        byte[] bytes = value.getBytes();
                        Byte[] wBytes = new Byte[bytes.length];

                        for (int i = 0; i < bytes.length; i++) {
                            wBytes[i] = new Byte(bytes[i]);
                        }

                        return wBytes;
                    }
                }
            });

        TYPE_CONVERTERS.put(Date.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    }

                    try {
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }

                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);

                        return df.parse(value);
                    } catch (java.text.ParseException pe) {
                        String msg = "Caugnt an error converting a String to a DateFormat.SHORT formatted Date";
                        LOGGER.warn(msg, pe);

                        TypeConversionException tce = new TypeConversionException(msg, pe);
                        tce.setLocalizedMessage(Bundle.getString("TypeUtils_javaUtilDateConvertError",
                                                                 new Object[] { pe.getMessage() }));
                        throw tce;
                    }
                }
            });

        /* http://java.sun.com/j2se/1.4.1/docs/api/java/sql/Date.html */
        TYPE_CONVERTERS.put(java.sql.Date.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    }

                    try {
                        return java.sql.Date.valueOf(value);
                    } catch (Exception e) {
                        String msg = "Caught an error converting a String to a java.sql.Date";
                        LOGGER.error(msg, e);

                        TypeConversionException tce = new TypeConversionException(msg, e);
                        tce.setLocalizedMessage(Bundle.getString("TypeUtils_javaSqlDateConvertError",
                                                                 new Object[] { e.getMessage() }));
                        throw tce;
                    }
                }
            });

        /* http://java.sun.com/j2se/1.4.1/docs/api/java/sql/Timestamp.html */
        TYPE_CONVERTERS.put(java.sql.Timestamp.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    }

                    try {
                        return java.sql.Timestamp.valueOf(value);
                    } catch (Exception e) {
                        String msg = "Caught an error converting a String to a java.sql.Timestamp";
                        LOGGER.error(msg, e);

                        TypeConversionException tce = new TypeConversionException(msg, e);
                        tce.setLocalizedMessage(Bundle.getString("TypeUtils_javaSqlTimestampConvertError",
                                                                 new Object[] { e.getMessage() }));
                        throw tce;
                    }
                }
            });

        /* http://java.sun.com/j2se/1.4.1/docs/api/java/sql/Time.html */
        TYPE_CONVERTERS.put(java.sql.Time.class,
                            new BaseTypeConverter() {
                public Object convertToObject(Class type, String value, Locale locale) {
                    if ((value == null) || value.equals(EMPTY_STRING)) {
                        return null;
                    }

                    try {
                        return java.sql.Time.valueOf(value);
                    } catch (Exception e) {
                        String msg = "Caught an error converting a String to a java.sql.Time";
                        LOGGER.error(msg, e);

                        TypeConversionException tce = new TypeConversionException(msg, e);
                        tce.setLocalizedMessage(Bundle.getString("TypeUtils_javaSqlTimeConvertError",
                                                                 new Object[] { e.getMessage() }));
                        throw tce;
                    }
                }
            });
    }
}
