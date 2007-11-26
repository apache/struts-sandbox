/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package entity.protocol;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

/**
 * <p>
 * Type converter for a <code>Protocol</code> entity.
 * </p>
 */
@SuppressWarnings("unchecked")
public class ProtocolTypeConverter extends StrutsTypeConverter {

    /**
     * Given a String ID, retrieve the corresponding <code>Protocol</code>
     * entity from the persistence database.
     */
    public Object convertFromString(Map context, String[] values, Class toClass) {
        ProtocolHelper manager = new ProtocolHelperImpl();
        String id = values[0];
        Protocol target = manager.find(id);
        return target;
    }

    /**
     * Provide the String ID for a <code>Protocol</code> object.
     */
    public String convertToString(Map context, Object o) {
        Protocol value = (Protocol) o;
        String id = value.getId();
        return id;
    }
}
