/*
 * $Id$
 *
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
package org.apache.struts2.jquery.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.jquery.JQueryPluginConstants;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@StrutsTag(
        name = "datepicker",
        tldTagClass = "org.apache.struts2.jquery.views.jsp.ui.JQueryDatepickerTag",
        description = "Renders a date picker",
        allowDynamicAttributes = true)
public class JQueryDatepicker extends JQueryTextField {
     final protected static Logger LOG = LoggerFactory.getLogger(JQueryDatepicker.class);

    private static final String TEMPLATE = "datepicker";
    final private static String RFC3339_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    final private static String RFC3339_PATTERN = "{0,date," + RFC3339_FORMAT + "}";
    
    //see http://docs.jquery.com/UI/Datepicker/%24.datepicker.formatDate
    private String displayFormat;

    public JQueryDatepicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateParams() {
        super.evaluateParams();

        if (displayFormat != null)
            addParameter("displayFormat", findString(displayFormat));

        if(parameters.containsKey("value")) {
            addParameter("nameValue", parameters.get("value"));
        } else {
            if(parameters.containsKey("name")) {
                addParameter("nameValue", format(findValue((String)parameters.get("name"))));
            }
        }
    }

    private String format(Object obj) {
        if(obj == null)
            return null;

        if(obj instanceof Date) {
            return MessageFormat.format(RFC3339_PATTERN, (Date) obj);
        } else if(obj instanceof Calendar) {
            return MessageFormat.format(RFC3339_PATTERN, ((Calendar) obj).getTime());
        }
        else {
            // try to parse a date
            String dateStr = obj.toString();
            if(dateStr.equalsIgnoreCase("today"))
                return MessageFormat.format(RFC3339_PATTERN, new Date());


            Date date = null;
            //formats used to parse the date
            List<DateFormat> formats = new ArrayList<DateFormat>();
            formats.add(new SimpleDateFormat(RFC3339_FORMAT));
            formats.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.SHORT));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.FULL));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.LONG));
            if (this.displayFormat != null) {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat(
                        (String) getParameters().get("displayFormat"));
                    formats.add(displayFormat);
                } catch (Exception e) {
                }
            }

            for (DateFormat format : formats) {
                try {
                    date = format.parse(dateStr);
                    if (date != null)
                        return MessageFormat.format(RFC3339_PATTERN, date);
                } catch (Exception e) {
                    //keep going
                }
            }

           // last resource, assume already in correct/default format
           if (LOG.isDebugEnabled())
               LOG.debug("Unable to parse date " + dateStr);
           return dateStr;
        }
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    @Inject(JQueryPluginConstants.DEFAULT_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

    @StrutsTagAttribute(description = "Format use to display the selected date", defaultValue = "mm/dd/yy")
    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }
}
