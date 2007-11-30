/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// This Java class is based on the "org.apache.mailreaderjpa" class  
// and has been edited to fit this MailReader implementation.   
package entity.protocol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import entity.UuidEntity;

/**
 * <p>
 * Describes an email server protocol, such as POP or SMTP.
 * </p>
 * <p>
 * JPA entity class for the <code>APP_PROTOCOL</code> table. This class
 * contains sufficient detail to regenerate the database schema (top-down
 * development). The annotation mode is by field.
 * </p>
 */
@Entity(name = "APP_PROTOCOL")
@NamedQueries( {
        @NamedQuery(name = Protocol.COUNT, query = Protocol.COUNT_QUERY),
        @NamedQuery(name = Protocol.FIND_ALL, query = Protocol.FIND_ALL_QUERY) })
public class Protocol extends UuidEntity implements Serializable {

    // ---- STATICS ----

    /**
     * <p>
     * Named query for counting <code>Protocol</code> entities.
     * </p>
     */
    public static final String COUNT = "Protocol.COUNT";

    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM APP_PROTOCOL";

    /**
     * <p>
     * Named query for selecting all <code>Protocol</code> instances.
     * </p>
     */
    public static final String FIND_ALL = "Protocol.FIND_ALL";

    private static final String FIND_ALL_QUERY = "SELECT p FROM APP_PROTOCOL p";

    // ---- FIELDS ----

    @Column(nullable = false)
    private String description;

    // ---- PROPERTIES ----

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ---- METHODS ----

    /**
     * <p>
     * Instantiate a default <code>Protocol</code> object.
     * </p>
     */
    public Protocol() {
        super();
    }

    /**
     * <p>
     * Instantiate a default <code>Protocol</code> object, and load values.
     * </p>
     */
    public Protocol(String description) {
        super();
        setDescription(description);
    }

}
