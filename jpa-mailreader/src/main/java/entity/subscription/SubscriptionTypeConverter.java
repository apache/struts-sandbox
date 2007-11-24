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
package entity.subscription;

import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

/**
 * <p>
 * Retrieve a <code>Subscription</code> entity from by its host name, or
 * return the host name for a <code>Subscription</code> entity.
 * </p>
 * 
 */
@SuppressWarnings("unchecked")
public class SubscriptionTypeConverter extends StrutsTypeConverter {

    /**
     * Given a host name, retrieve the corresponding <code>Subscription</code>
     * entity from the persistence database.
     */
    public Object convertFromString(Map context, String[] values, Class toClass) {
        SubscriptionManagerInterface manager = new SubscriptionManager();
        String name = String.valueOf(values[0]);
        Subscription result = manager.findByName(name);
        return result;
    }

    /**
     * Provide the host name for a <code>Subscription</code> object.
     */
    public String convertToString(Map context, Object o) {
        Subscription value = (Subscription) o;
        return value.getHost();
    }
}
