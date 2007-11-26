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
package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

@MappedSuperclass
/**
 * <p>
 * Define a POJO that utilizes an ID property based on a universally unique
 * identifier (UUID).
 * </p>
 * <p>
 * UUIDs are particulary useful in RESTful application designs, since an object
 * instance can assign its own primary key without interacting with a
 * persistence unit. This base class is intended as a convenience only, and the
 * class type should not be referred by the API. Instances of this class should
 * be treated as ordinary POJOs (which they are!).
 * </p>
 */
public class UuidEntity implements Serializable {

    @Transient
    private UUID uuid;

    @Id
    @Column(length = 36)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    @Version()
    private Timestamp last_update;

    public Timestamp getLastUpdate() {
        return last_update;
    }

    public void setLastUpdate(Timestamp value) {
        last_update = value;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof UuidEntity) && (getId() != null)) {
            return getId().equals(((UuidEntity) obj).getId());
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (getId() != null) {
            if (uuid == null)
                uuid = UUID.fromString(id);
            return uuid.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public String toString() {
        return "entity.UuidEntity[id=" + getId() + "]";
    }

    public UuidEntity() {
        String id = UUID.randomUUID().toString();
        setId(id);
    }
}
