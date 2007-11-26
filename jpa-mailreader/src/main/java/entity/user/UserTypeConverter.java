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
package entity.user;

import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

/**
 * <p>
 * Type convertion for a <code>User</code> entity.
 * </p>
 */
@SuppressWarnings("unchecked")
public class UserTypeConverter extends StrutsTypeConverter {

    /**
     * <p>
     * Retrieve the corresponding <code>User</code> entity from the
     * persistence database, given a <code>username</code>.
     * </p>
     */
    public Object convertFromString(Map context, String[] values, Class toClass) {
        UserHelper manager = new UserHelperImpl();
        String name = values[0];
        User result = manager.findByName(name);
        return result;
    }

    /**
     * <p>
     * Provide the <code>username</code> for a <code>User</code> object.
     * </p>
     */
    public String convertToString(Map context, Object o) {
        User value = (User) o;
        return value.getUsername();
    }
}
