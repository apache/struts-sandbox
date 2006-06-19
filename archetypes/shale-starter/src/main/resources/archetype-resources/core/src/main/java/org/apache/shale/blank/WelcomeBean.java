/*
 * Copyright 2006 The Apache Software Foundation.
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
 *
 */

package org.apache.shale.blank;

import java.util.Date;
import org.apache.shale.view.AbstractViewController;

/**
 * <p>Sample <code>ViewController</code> class for <code>/welcome.jsp</code>.</p>
 */
public class WelcomeBean extends AbstractViewController {
    

    // -------------------------------------------------------------- Properties


    /**
     * <p>The current date and time value.</p>
     */
    private Date timestamp = null;


    /**
     * <p>Return the current date and time value.</p>
     */
    public Date getTimestamp() {

        return this.timestamp;

    }


    /**
     * <p>Set the current date and time value.</p>
     *
     * @param timestamp The new date and time value
     */
    public void setTimestamp(Date timestamp) {

        this.timestamp = timestamp;

    }


    // --------------------------------------------------- ViewControler Methods


    /**
     * <p>Just before rendering occurs, set the <code>timestamp</code>
     * property to the current date and time.</p>
     */
    public void prerender() {

        setTimestamp(new Date());

    }


}
