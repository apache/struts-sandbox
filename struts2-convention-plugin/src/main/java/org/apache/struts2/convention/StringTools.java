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
package org.apache.struts2.convention;

/**
 * <p>
 * This class is a String helper.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class StringTools {
    public static boolean isTrimmedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String lastToken(String str, String s) {
        int index = str.lastIndexOf(s);
        if (index >= 0) {
            return str.substring(index + 1);
        }

        return str;
    }

    public static String upToLastToken(String str, String s) {
        int index = str.lastIndexOf(s);
        if (index >= 0) {
            return str.substring(0, index);
        }

        return "";
    }
}