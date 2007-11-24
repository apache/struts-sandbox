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
package entity.user;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import entity.EntitySuperclass;
import entity.subscription.Subscription;

/**
 * <p>
 * Describes an account that maintains zero or more <code>Subscription</code>s.
 * </p>
 * <p>
 * JPA entity class for the <code>APP_USER</code> table. This class is kept
 * simple to allow for easier regeneration.
 * </p>
 */
@Entity(name = "APP_USER")
@NamedQueries( {
        @NamedQuery(name = User.FIND_ALL, query = "SELECT u FROM APP_USER u"),
        @NamedQuery(name = User.FIND_BY_NAME, query = "SELECT u FROM APP_USER u WHERE u.username = :username"), })
public class User extends EntitySuperclass implements Serializable {

    // ---- STATICS ----

    /**
     * <p>
     * Named query for finding a <code>User</code> by username.
     * </p>
     */
    public static final String FIND_ALL = "User.FIND_ALL";

    /**
     * <p>
     * Named query for finding a <code>User</code> by username.
     * </p>
     */
    public static final String FIND_BY_NAME = "User.FIND_BY_USERNAME";

    /**
     * <p>
     * Token representation the "username" attribute.
     * </p>
     */
    public static final String NAME = "username";

    // --- FIELDS ----

    @Column(length = 64)
    private String from_address;

    @Column(length = 64)
    private String full_name;

    @Column(length = 16, nullable = false)
    private String password;

    @Column(length = 64)
    private String reply_to_address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    @Column(length = 16, nullable = false, unique = true)
    private String username;

    // ---- PROPERTIES ----

    public String getFromAddress() {
        return from_address;
    }

    public void setFromAddress(String value) {
        from_address = value;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String value) {
        full_name = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    public String getReplyToAddress() {
        return reply_to_address;
    }

    public void setReplyToAddress(String value) {
        reply_to_address = value;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> value) {
        subscriptions = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        username = value;
    }

    @Transient
    private String password1 = null;

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String value) {
        password1 = value;
    }

    @Transient
    private String password2 = null;

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String value) {
        password2 = value;
    }

    // ---- METHODS ----

    /**
     * <p>
     * Add the specified <code>Subscription</code> to the set of subscriptions
     * associated with this <code>User</code>.
     * </p>
     * <p>
     * A duplicate <code>Subscription</code> is not added but quietly ignored.
     * </p>
     */
    public void addSubscription(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptions();
        if (!subscriptions.contains(subscription)) {
            subscription.setUser(this);
            subscriptions.add(subscription);
        }
    }

    /**
     * <p>
     * Remove the specified <code>Subscription</code> from the set of
     * subscriptions associated with this <code>User</code>.
     * </p>
     * <p>
     * A duplicate <code>Subscription</code> is not added but quietly ignored.
     * </p>
     */
    public void removeSubscription(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptions();
        if (subscriptions.contains(subscription)) {
            subscriptions.remove(subscription);
        }
    }

    /**
     * <p>
     * Instantiate a default <code>User</code> object.
     * </p>
     */
    public User() {
    }

}
