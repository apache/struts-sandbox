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

package org.apache.shale.usecases.logon;

import org.apache.shale.usecases.view.BaseViewController;

/**
 * <p><code>ViewController</code> for the logon page.</p>
 *
 * <p>Technically, all of the event handlers required by this view (and others)
 * could be placed on the {@link Dialog} class instead of here.  Maintaining
 * the 1:1 correspondence between a view and a corresponding view controller,
 * however, makes long term maintenance easier.</p>
 *
 * $Id$
 */
public class Logon extends BaseViewController {
    

    // -------------------------------------------------------------- Properties


    // ---------------------------------------------------------- Event Handlers


    /**
     * <p>Request creation of a new user profile.</p>
     */
    public String create() {
        return ((Dialog) getBean(Dialog.DIALOG_BEAN)).create();
    }


    /**
     * <p>Validate the specified credentials.</p>
     */
    public String logon() {
        return ((Dialog) getBean(Dialog.DIALOG_BEAN)).logon();
    }


}
