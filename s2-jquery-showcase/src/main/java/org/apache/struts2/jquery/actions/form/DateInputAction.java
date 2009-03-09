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
package org.apache.struts2.jquery.actions.form;

import org.apache.struts2.convention.annotation.Namespace;
import com.opensymphony.xwork2.ActionSupport;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

@Namespace("/form")
public class DateInputAction extends ActionSupport {
    private Date date;
    private Calendar calendar;
    private String shortFormat;

    @Override
    public String execute() throws Exception {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        date = calendar.getTime();

        shortFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(date);

        return super.execute();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getShortFormat() {
        return shortFormat;
    }

    public Date getDate() {
        return date;
    }
}
