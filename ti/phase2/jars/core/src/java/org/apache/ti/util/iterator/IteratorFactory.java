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
package org.apache.ti.util.iterator;

import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.IteratorFactoryConfig;
import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.logging.Logger;

import java.sql.ResultSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.RowSet;

/**
 * <p/>
 * This class provides a factory that can create an {@link Iterator} for various types
 * of Java objects.  Supported types include:
 * <ul>
 * <li>{@link java.util.Iterator}</li>
 * <li>{@link java.util.Collection}</li>
 * <li>{@link java.util.Map}</li>
 * <li>{@link java.sql.ResultSet}</li>
 * <li>{@link javax.sql.RowSet}</li>
 * <li>{@link java.util.Enumeration}</li>
 * <li>Any Java Object array</li>
 * </ul>
 * <p/>
 * <p/>
 * If an object type not listed above is supplied the object will be wrapped in
 * an iterator that contains only the provided object.
 * </p>
 */
public class IteratorFactory {
    /**
     * Convenience field for accessing an empty {@link Iterator}.
     */
    public static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
    private static final Logger LOGGER = Logger.getInstance(IteratorFactory.class);
    private static final LinkedHashMap ITERATOR_FACTORIES;

    static {
        ITERATOR_FACTORIES = new LinkedHashMap();
        initialize();
    }

    /**
     * @exclude
     */
    public abstract static class IteratorPlant {
        /**
         * If it is possible to create an iterator for this type, do so.
         * Otherwise return null.
         */
        public abstract Iterator createIterator(Object value);
    }

    /**
     * Create a new {@link Iterator} for the supplied object.
     *
     * @param object the object to build an iterator from
     * @return an {@link Iterator} for the <code>object</code> or <code>null</code> if the value is null.
     */
    public static final Iterator createIterator(Object object) {
        LOGGER.debug("Create an iterator for class: " + ((object == null) ? "null" : object.getClass().getName()));

        if (object == null) {
            return null;
        }

        if (object instanceof Iterator) {
            return (Iterator) object;
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;

            return collection.iterator();
        } else if (object instanceof Map) {
            return new MapIterator((Map) object);
        } else if (object.getClass().isArray()) {
            return new ArrayIterator(object);
        } else if (object instanceof Enumeration) {
            return new EnumerationIterator((Enumeration) object);
        } else if (object instanceof ResultSet && !(object instanceof RowSet)) {
            return new ResultSetIterator((ResultSet) object);
        }

        // check to see if there is a registered IteratorPlant that can handle this type
        Iterator ret = null;
        Iterator factories = ITERATOR_FACTORIES.keySet().iterator();

        while (factories.hasNext()) {
            IteratorPlant plant = (IteratorPlant) ITERATOR_FACTORIES.get(factories.next());
            ret = plant.createIterator(object);

            if (ret != null) {
                return ret;
            }
        }

        return new AtomicObjectIterator(object);
    }

    /**
     * Initialize the configuration parameters used to build Iterator objects
     * for custom types.
     */
    private static final void initialize() {
        Map map = readFromConfig();

        if (map != null) {
            loadFactories(map);
        }
    }

    private static final Map readFromConfig() {
        NetUIConfig config = ConfigUtil.getConfig();

        if (config == null) {
            return null;
        }

        IteratorFactoryConfig[] iteratorFactories = config.getIteratorFactories();

        if (iteratorFactories != null) {
            LinkedHashMap map = new LinkedHashMap();

            for (int i = 0; i < iteratorFactories.length; i++) {
                map.put(iteratorFactories[i].getName(), iteratorFactories[i].getFactoryClass());
            }

            return map;
        } else {
            return null;
        }
    }

    private static final void loadFactories(Map factories) {
        Iterator iterator = factories.keySet().iterator();

        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String className = (String) factories.get(name);
            IteratorPlant plant = null;

            try {
                Class type = Class.forName(className);
                plant = (IteratorPlant) type.newInstance();
            } catch (ClassNotFoundException cnf) {
                LOGGER.warn("Could not create an IteratorPlant for type \"" + className +
                            "\" because the implementation class could not be found.");

                continue;
            } catch (Exception e) {
                assert e instanceof InstantiationException || e instanceof IllegalAccessException;
                LOGGER.warn("Could not create an IteratorPlant for type \"" + className +
                            "\" because an error occurred creating the plant.  Cause: " + e, e);

                continue;
            }

            if (ITERATOR_FACTORIES.containsKey(name)) {
                LOGGER.warn("Overwriting a previously defined IteratorPlant named \"" + name +
                            "\" with a new IteratorPlant of type \"" + className + "\"");
            } else {
                LOGGER.info("Adding an IteratorPlant named \"" + name + "\" with implementation \"" + className + "\"");
            }

            ITERATOR_FACTORIES.put(name, plant);
        }
    }
}
