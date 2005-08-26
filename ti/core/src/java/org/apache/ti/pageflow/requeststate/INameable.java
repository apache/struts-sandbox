/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.pageflow.requeststate;

/**
 * This interface defines what it means to be a namable object.  The name
 * service will name an INameable object which will be unique for the life
 * time of the session.
 */
public interface INameable {

    /**
     * Set the ObjectName of the INameable object.  This should only
     * be set once.  If it is called a second time an IllegalStateException
     * should be thrown
     *
     * @param name the Object's name.
     * @throws IllegalStateException if this method is called more than once for an object
     */
    public void setObjectName(String name);

    /**
     * Returns the ObjectName of the INameable object.
     *
     * @return the ObjectName.
     */
    public String getObjectName();
}
