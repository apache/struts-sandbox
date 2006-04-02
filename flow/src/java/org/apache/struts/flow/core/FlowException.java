/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.flow.core;

import java.util.List;

import org.apache.struts.flow.core.location.LocatedException;
import org.apache.struts.flow.core.location.LocatedRuntimeException;
import org.apache.struts.flow.core.location.Location;
import org.apache.struts.flow.core.location.MultiLocatable;

/**
 * This Exception is thrown every time there is a problem in processing
 * a request.
 *
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation)
 * @version CVS $Id: FlowException.java 280632 2005-09-13 19:35:46Z sylvain $
 */
public class FlowException extends LocatedRuntimeException implements MultiLocatable {
    
    /**
     * Construct a new <code>FlowException</code> instance.
     */
    public FlowException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>FlowException</code> instance.
     *
     * @param ex an <code>Exception</code> value
     */
    public FlowException(Exception ex) {
        super(ex.getMessage(), ex);
    }
    
    /**
     * Construct a new <code>FlowException</code> that references
     * a parent Exception.
     */
    public FlowException(String message, Throwable t) {
        super(message, t);
    }
    
    /**
     * Construct a new <code>FlowException</code> that has an associated location.
     */
    public FlowException(String message, Location location) {
        super(message, location);
    }
    
    /**
     * Construct a new <code>FlowException</code> that has a parent exception
     * and an associated location.
     * <p>
     * This constructor is protected to enforce the use of {@link #throwLocated(String, Throwable, Location)}
     * which limits exception nesting as far as possible.
     */
    protected FlowException(String message, Throwable t, Location location) {
        super(message, t, location);
    }
    
    /**
     * Throw a located exception given an existing exception and the location where
     * this exception was catched.
     * <p>
     * If the exception is already a <code>FlowException</code> or a {@link LocatedRuntimeException},
     * the location is added to the original exception's location chain and the original exception
     * is rethrown (<code>description</code> is ignored) to limit exception nesting. Otherwise, a new
     * <code>FlowException</code> is thrown, wrapping the original exception.
     * <p>
     * Note: this method returns an exception as a convenience if you want to keep the <code>throw</code>
     * semantics in the caller code, i.e. write<br>
     * <code>&nbsp;&nbsp;throw FlowException.throwLocated(...);</code><br>
     * instead of<br>
     * <code>&nbsp;&nbsp;FlowException.throwLocated(...);</code><br>
     * <code>&nbsp;&nbsp;return;</code>
     * 
     * @param message a message (can be <code>null</code>)
     * @param thr the original exception (can be <code>null</code>)
     * @param location the location (can be <code>null</code>)
     * @return a (fake) located exception
     * @throws FlowException or <code>LocatedRuntimeException</code>
     */
    public static FlowException throwLocated(String message, Throwable thr, Location location) {
        if (thr instanceof FlowException) {
            FlowException pe = (FlowException)thr;
            pe.addLocation(location);
            throw pe;

        } else if (thr instanceof LocatedRuntimeException) {
            LocatedRuntimeException re = (LocatedRuntimeException)thr;
            re.addLocation(location);
            // Rethrow
            throw re;
        }
        
        throw new FlowException(message, thr, location);
    }
    
    /**
     * Throw a located exception given an existing exception and the locations where
     * this exception was catched.
     * <p>
     * If the exception is already a <code>FlowException</code> or a {@link LocatedRuntimeException},
     * the locations are added to the original exception's location chain and the original exception
     * is rethrown (<code>description</code> is ignored) to limit exception nesting. Otherwise, a new
     * <code>FlowException</code> is thrown, wrapping the original exception.
     * <p>
     * Note: this method returns an exception as a convenience if you want to keep the <code>throw</code>
     * semantics in the caller code, i.e. write<br>
     * <code>&nbsp;&nbsp;throw FlowException.throwLocated(...);</code><br>
     * instead of<br>
     * <code>&nbsp;&nbsp;FlowException.throwLocated(...);</code><br>
     * <code>&nbsp;&nbsp;return;</code>
     * 
     * @param message a message (can be <code>null</code>)
     * @param thr the original exception (can be <code>null</code>)
     * @param locations the locations (can be <code>null</code>)
     * @return a (fake) located exception
     * @throws FlowException or <code>LocatedRuntimeException</code>
     */
    public static FlowException throwLocated(String message, Throwable thr, List locations) throws FlowException {
        MultiLocatable multiloc;
        if (thr instanceof FlowException) {
            multiloc = (FlowException)thr;
        } else if (thr instanceof LocatedRuntimeException) {
            multiloc = (LocatedRuntimeException)thr;
        } else {
            multiloc = new FlowException(message, thr);
        }
        
        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                multiloc.addLocation((Location)locations.get(i));
            }
        }
        
        if (multiloc instanceof LocatedRuntimeException) {
            throw (LocatedRuntimeException)multiloc;
        } else {
            throw (FlowException)multiloc;
        }
    }
}
