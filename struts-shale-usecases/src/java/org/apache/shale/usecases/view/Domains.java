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

package org.apache.shale.usecases.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.shale.util.Messages;

/**
 * <p>Utility class to return locale-specific domains (lists of selection items)
 * based on the current <code>Locale</code> of this request.  An instance of
 * this class will typically be defined as an application scope managed bean,
 * so that it is instantiated on demand.</p>
 *
 * $Id$
 */
public class Domains {
    

    // -------------------------------------------------------- Static Variables


    /**
     * <p>Localized messages for this class.</p>
     */
    private static Messages messages =
      new Messages("org.apache.shale.usecases.view.Bundle");


    // ------------------------------------------------------ Instance Variables


    /**
     * <p><code>Map</code> containing arrays of <code>SelectItem</code>s
     * representing the supported message categories for this application,
     * keyed by the Locale in which the descriptions have been localized.</p>
     */
    private Map categories = new HashMap();


    /**
     * <p><code>Map</code> containing arrays of <code>SelectItem</code>s
     * representing the locales supported by this application, keyed by
     * the Locale in which the descriptions have been localized.</p>
     */
    private Map locales = new HashMap();


    // -------------------------------------------------------------- Properties


    /**
     * <p>Return an array of selection items representing the message categories
     * supported by this application, with the labels localized based on the
     * <code>Locale</code> of the current request.</p>
     */
    public SelectItem[] getSupportedCategories() {

        // Return any previously cached array for this request locale
        Locale locale =
          FacesContext.getCurrentInstance().getViewRoot().getLocale();
        SelectItem items[] = null;
        synchronized (categories) {
            items = (SelectItem[]) categories.get(locale);
            if (items != null) {
                return items;
            }
        }

        // Construct and cache a new array, before returning it
        SelectItem item = null;
        List list = new ArrayList();
        int id = 0;
        String label = null;
        while (true) {
            label = messages.getMessage("category." + id);
            if (label == null) {
                break;
            }
            list.add(new SelectItem(new Integer(id), label));
            id++;
        }
        items = (SelectItem[]) list.toArray(new SelectItem[list.size()]);
        synchronized(categories) {
            categories.put(locale, items);
        }
        return items;

    }


    /**
     * <p>Return an array of selection items representing the locales supported
     * by this application, with the labels localized based on the
     * <code>Locale</code> of the current request.</p>
     */
    public SelectItem[] getSupportedLocales() {

        // Return any previously cached array for this request locale
        Locale locale =
          FacesContext.getCurrentInstance().getViewRoot().getLocale();
        SelectItem items[] = null;
        synchronized (locales) {
            items = (SelectItem[]) locales.get(locale);
            if (items != null) {
                return items;
            }
        }

        // Construct and cache a new array, before returning it
        SelectItem item = null;
        List list = new ArrayList();
        Iterator supporteds =
          FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (supporteds.hasNext()) {
            Locale supported = (Locale) supporteds.next();
            item = new SelectItem(supported.toString(),
                                  messages.getMessage("locale." + supported.toString()));
            list.add(item);
        }
        items = (SelectItem[]) list.toArray(new SelectItem[list.size()]);
        synchronized(locales) {
            locales.put(locale, items);
        }
        return items;

    }

    
}
