/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

package org.apache.shale;

import java.io.Serializable;

/**
 * <p>{@link DialogController} is an interface describing a JavaBean that
 * manages the current state of a dialog (that is, a conversation that
 * requires multiple HTTP requests to interact with the user).  Note that,
 * because {@link DialogController} instances are stored in session scope,
 * all state information that is maintained should be serializable so that
 * your application may operate in a distributed container.</p>
 *
 * <p>A {@link DialogController} instance comes into being when an
 * initiaization method (normally <code>enter()</code>) is called.  This method
 * should cause the dialog instance to be stored in session scope
 * (this is typically automatic if the dialog instance is a managed bean),
 * allocate any needed resources, and return a logical outcome that may be
 * used by the <code>NavigationHandler</code> to select the first view
 * that is part of this dialog.</p>
 *
 * <p>If a particular {@link DialogController} wishes to provide additonal
 * initialization methods for alternate starting conditions, these additional
 * methods should be implemented with the same method signature, and fulfill
 * the same responsibilities.</p>
 *
 * <p>A {@link DialogController} instance completes its work when one of
 * its termination methods is called.  The interface defines two termination
 * methods (<code>exit()</code> and <code>cancel()</code>), indicating
 * "successful" and "cancelled" completion of the dialog, respectively.
 * Such a termination method should persist any transaction represented
 * by the state of this dialog (if termination was successful), release
 * any acquired resources, cause the dialog instance to be removed from
 * session scope, and return a logical outcome that may be used by the
 * <code>NavigationHandler</code> to select the first view that is
 * <strong>not</strong> part of this dialog.</p>
 *
 * <p>If a particular {@link DialogController} wishes to provide additonal
 * termination methods for variations on completion status, these additional
 * methods should be implemented with the same method signature, and fulfill
 * the same responsibilities.</p>
 *
 * <p><strong>WARNING</strong> - this is a very early prototype of what a
 * {@link DialogController} might look like.  The final design might assign
 * responsibilities in a different manner (for example, performing some of
 * the updates to the persistent data inside what are currently defined as
 * methods to provide navigation control).</p>
 *
 * $Id$
 */

public interface DialogController extends Serializable {
    

    // -------------------------------------------------------------- Properties

    
    // ------------------------------------------------------ Navigation Methods


    /**
     * <p>Termination method indicating that this dialog has been completed
     * abnormally.  Any saved state information should be released without
     * impact on the persistent data for the application, any alocated
     * resources should be released, and this {@link DialogController}
     * instance should be removed from session scope.</p>
     *
     * @return Logical outcome used for navigation outside this dialog
     */
    public String cancel();
    
    
    /**
     * <p>Initialization method indicating that this dialog has been entered.
     * Any desired resources should be allocated, and this
     * {@link DialogController} instance should be added to session scope
     * (if this was not already done by virtue of being a managed bean).</p>
     *
     * @return Logical outcome used for navigation to the first
     *  view within this dialog
     */
    public String enter();


    /**
     * <p>Termination method indicating that this dialog has been completed
     * successfully.  Any transaction represented by the saved state
     * information should be persisted, any allocated resources should be
     * released, and this {@link DialogController} instance should be
     * removed from session scope.
     *
     * @return Logical outcome used for navigation outside this dialog
     */
    public String exit();
    
    
    /**
     * <p>Return a logical outcome that will navigate to the first view
     * belonging to this dialog, or <code>null</code> if control should
     * remain on the current view.</p>
     *
     * @exception UnsupportedOperationException if not supported for
     *  this dialog
     */
    // public String first();


    /**
     * <p>Return a logical outcome that will navigate to the last view
     * belonging to this dialog, or <code>null</code> if control should
     * remain on the current view.</p>
     *
     * @exception UnsupportedOperationException if not supported for
     *  this dialog
     */
    // public String last();


    /**
     * <p>Return a logical outcome that will navigate to the next view
     * belonging to this dialog, or <code>null</code> if control should
     * remain on the current view.</p>
     *
     * @exception UnsupportedOperationException if not supported for
     *  this dialog
     */
    // public String next();


    /**
     * <p>Return a logical outcome that will navigate to the previous view
     * belonging to this dialog, or <code>null</code> if control should
     * remain on the current view.</p>
     *
     * @exception UnsupportedOperationException if not supported for
     *  this dialog
     */
    // public String previous();


}
