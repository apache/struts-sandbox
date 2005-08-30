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
package org.apache.ti.core.factory;

import org.apache.ti.schema.config.CustomProperty;
import org.apache.ti.schema.config.PageflowFactory;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.logging.Logger;

/**
 * Utility class for creating application-scoped factories.
 */
public class FactoryUtils {

    private static final Logger _log = Logger.getInstance(FactoryUtils.class);

    public static Factory getFactory(PageflowFactory factoryBean, Class factoryType) {
        if (factoryBean == null) return null;

        String className = factoryBean.getFactoryClass();
        ClassLoader cl = DiscoveryUtils.getClassLoader();

        try {
            Class actualFactoryType = cl.loadClass(className);

            if (!factoryType.isAssignableFrom(actualFactoryType)) {
                _log.error("Factory class " + actualFactoryType.getName() + " is not derived from "
                        + factoryType.getName());
                return null;
            }

            CustomProperty[] props = factoryBean.getCustomPropertyArray();
            FactoryConfig config = new FactoryConfig();

            if (props != null) {
                for (int i = 0; i < props.length; i++) {
                    CustomProperty prop = props[i];
                    config.addCustomProperty(prop.getName(), prop.getValue());
                }
            }

            return getFactory(actualFactoryType, config);
        } catch (ClassNotFoundException e) {
            _log.error("Could not load factory class " + className, e);
        }

        return null;
    }

    public static Factory getFactory(Class factoryType, FactoryConfig config) {
        assert Factory.class.isAssignableFrom(factoryType) : factoryType.getClass().getName();

        try {
            Factory factory = (Factory) factoryType.newInstance();
            factory.init(config);
            factory.onCreate();

            return factory;
        } catch (InstantiationException e) {
            _log.error("Could not instantiate a factory of type " + factoryType.getName(), e);
        } catch (IllegalAccessException e) {
            _log.error("Could not access the default constructor for factory of type " + factoryType.getName(), e);
        }

        return null;
    }
}
