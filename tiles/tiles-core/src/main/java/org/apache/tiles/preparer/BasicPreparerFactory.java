/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tiles.preparer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesException;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.util.ClassUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the {@link PreparerFactory}.
 * This factory provides no contextual configuration.  It
 * simply instantiates the named preparerInstance and returns it.
 *
 * @version $Rev$
 * @since Tiles 2.0
 */
public class BasicPreparerFactory implements PreparerFactory {

    private static final Log LOG =
        LogFactory.getLog(BasicPreparerFactory.class);

    private Map<String, ViewPreparer> preparers;

    public BasicPreparerFactory() {
        this.preparers = new HashMap<String, ViewPreparer>();
    }


    /**
     * Create a new instance of the named preparerInstance.  This factory
     * expects all names to be qualified class names.
     *
     * @param name    the named preparerInstance
     * @param context current context
     * @return ViewPreparer instance
     * @throws NoSuchPreparerException
     */
    public ViewPreparer getPreparer(String name, TilesRequestContext context)
        throws TilesException {

        if (!preparers.containsKey(name)) {
            preparers.put(name, createPreparer(name));
        }

        return preparers.get(name);
    }

    private ViewPreparer createPreparer(String name) throws TilesException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating ViewPreparer '" + name + "' . . .");
        }

        Object instance = ClassUtil.instantiate(name, true);
        LOG.debug("ViewPreparer created successfully");
        return (ViewPreparer) instance;

    }
}
