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
package org.apache.ti.script;

import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.ExpressionLanguageConfig;
import org.apache.ti.util.config.bean.ExpressionLanguagesConfig;
import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Get an expression evaluator.
 */
public class ExpressionEvaluatorFactory {
    private static final Logger _logger = Logger.getInstance(ExpressionEvaluatorFactory.class);
    private static final HashMap FACTORY_MAP = new HashMap();
    private static ExpressionEngineFactory DEFAULT_FACTORY;

    static {
        try {
            DEFAULT_FACTORY = initialize(FACTORY_MAP);
        } catch (Exception e) {
            DEFAULT_FACTORY = null;

            if (_logger.isErrorEnabled()) {
                _logger.error("An exception occurred loading the expression evaluator configuration.  Cause: " + e, e);
            }
        }
    }

    /**
     * Get the default instance of an expression evaluator.
     *
     * @return an {@link ExpressionEvaluator}
     */
    public final static ExpressionEvaluator getInstance() {
        return getInstance(null);
    }

    /**
     * Get an {@link ExpressionEvaluator} named <code>name</code>.
     *
     * @return an ExpressionEvaluator matching the given name.
     * @throws IllegalArgumentException if an ExpressionEvaluator matching the name is not found
     */
    public final static ExpressionEvaluator getInstance(String name) {
        assert DEFAULT_FACTORY != null;
        assert FACTORY_MAP != null;

        if (name == null) {
            return DEFAULT_FACTORY.getInstance();
        } else if (FACTORY_MAP.containsKey(name)) {
            return ((ExpressionEngineFactory) FACTORY_MAP.get(name)).getInstance();
        }

        String msg = "An ExpressionEvaluator named \"" + name + "\" is not available.";

        if (_logger.isErrorEnabled()) {
            _logger.error(msg);
        }

        throw new IllegalArgumentException(msg);
    }

    private static final ExpressionEngineFactory initialize(Map factoryMap) {
        assert factoryMap != null;

        NetUIConfig config = ConfigUtil.getConfig();

        ExpressionLanguagesConfig elConfig = config.getExpressionLanguages();
        assert elConfig != null;

        ExpressionLanguageConfig[] els = elConfig.getExpressionLanguages();
        assert els != null;

        if (els != null) {
            for (int i = 0; i < els.length; i++) {
                String name = els[i].getName();
                String className = els[i].getFactoryClass();

                ExpressionEngineFactory factory = null;

                try {
                    Class type = Class.forName(className);
                    factory = (ExpressionEngineFactory) type.newInstance();
                } catch (ClassNotFoundException cnf) {
                    if (_logger.isWarnEnabled()) {
                        _logger.warn("Could not create an ExpressionEngineFactory for type \"" + className +
                                     "\" because the implementation class could not be found.");
                    }

                    continue;
                } catch (Exception ex) {
                    assert ex instanceof IllegalAccessException || ex instanceof InstantiationException;

                    if (_logger.isWarnEnabled()) {
                        _logger.warn("Could not create an ExpressionEngineFactory for type \"" + className +
                                     "\" because an error occurred creating the factory.  Cause: " + ex, ex);
                    }

                    continue;
                }

                if (factoryMap.containsKey(name)) {
                    if (_logger.isWarnEnabled()) {
                        _logger.warn("Overwriting a previously defined ExpressionEngineFactory named \"" + name +
                                     "\" with a new ExpressionEngineFactory of type \"" + className + "\"");
                    } else {
                        _logger.info("Adding an ExpressionEngineFactory named \"" + name + "\" with implementation \"" +
                                     className + "\"");
                    }
                }

                factoryMap.put(name, factory);
            }
        }

        ExpressionEngineFactory defaultEngineFactory = null;
        String defaultLanguage = elConfig.getDefaultLanguage();

        if (defaultLanguage != null) {
            defaultEngineFactory = (ExpressionEngineFactory) factoryMap.get(defaultLanguage);

            if (defaultEngineFactory != null) {
                if (_logger.isInfoEnabled()) {
                    _logger.info("Using a default expression evaluator of type \"" +
                                 factoryMap.get(defaultLanguage).getClass().getName() + "\"");
                }
            } else {
                String msg = "The default ExpressionEvaluator named \"" + defaultLanguage +
                             "\" was specified, but the ExpressionEngineFactory could not be found.";

                if (_logger.isWarnEnabled()) {
                    _logger.warn(msg);
                }

                throw new RuntimeException(msg);
            }
        } else {
            String msg = "There is no default expression engine specified.";

            if (_logger.isErrorEnabled()) {
                _logger.error(msg);
            }

            throw new RuntimeException(msg);
        }

        return defaultEngineFactory;
    }
}
