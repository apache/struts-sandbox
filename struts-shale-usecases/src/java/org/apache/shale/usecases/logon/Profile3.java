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
 * <p><code>ViewController</code> for the third page of the user profile.</p>
 *
 * $Id$
 */
public class Profile3 extends BaseViewController {
    

    // -------------------------------------------------------------- Properties


    // ---------------------------------------------------------- Event Handlers


    /**
     * <p>Respond to pressing the <em>Cancel</em> button.</p>
     */
    public String cancel() {
        Dialog dialog = (Dialog) getBean(Dialog.DIALOG_BEAN);
        return dialog.cancel();
    }


    /**
     * <p>Respond to pressing the <em>Finish</em> button.</p>
     */
    public String finish() {
        Dialog dialog = (Dialog) getBean(Dialog.DIALOG_BEAN);
        return dialog.finish();
    }


    /**
     * <p>Respond to pressing the <em>Next</em> button.</p>
     */
    public String next() {
        return Dialog.PROFILE3;
    }


    /**
     * <p>Respond to pressing the <em>Previous</em> button.</p>
     */
    public String previous() {
        return Dialog.PROFILE2;
    }


    // --------------------------------------------------------- Private Methods


}
