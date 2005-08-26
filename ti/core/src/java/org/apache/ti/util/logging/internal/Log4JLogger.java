/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.ti.util.logging.internal;

import org.apache.commons.logging.Log;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p/>
 * Logging abstraction used to pipe log messages to Log4J.  This class is used for
 * NetUI backwards compatability so that previous {@link org.apache.beehive.netui.util.logging.Logger}
 * clients continue to log through the usual Log4J channels.
 * </p>
 *
 * @deprecated
 */
public final class Log4JLogger
        implements Log {

    private static final String STRUTS_APPENDER = "commons-logging";

    static {
        // Need to get rid of the appender that Struts adds so
        // that we don't spam the console with all messages
        Category root = Category.getRoot();

        if (root.getAppender(STRUTS_APPENDER) != null)
            root.removeAppender(STRUTS_APPENDER);
    }

    private Logger _logInstance;

    public static Log getInstance(Class clazz) {
        return new Log4JLogger(clazz);
    }

    private Log4JLogger(Class clazz) {
        this(clazz.getName());
    }

    private Log4JLogger(String className) {
        _logInstance = Logger.getLogger(className);
    }

    public boolean isDebugEnabled() {
        return _logInstance.isEnabledFor(Level.DEBUG);
    }

    public boolean isErrorEnabled() {
        return _logInstance.isEnabledFor(Level.ERROR);
    }

    public boolean isFatalEnabled() {
        return _logInstance.isEnabledFor(Level.FATAL);
    }

    public boolean isInfoEnabled() {
        return _logInstance.isEnabledFor(Level.INFO);
    }

    public boolean isTraceEnabled() {
        return _logInstance.isEnabledFor(Level.DEBUG);
    }

    public boolean isWarnEnabled() {
        return _logInstance.isEnabledFor(Level.WARN);
    }

    public void debug(Object message) {
        if (_logInstance.isEnabledFor(Level.DEBUG))
            _logInstance.debug(message);
    }

    public void debug(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.DEBUG))
            _logInstance.debug(format(message, t));
    }

    public void trace(Object message) {
        if (_logInstance.isEnabledFor(Level.DEBUG))
            _logInstance.debug(message);
    }

    public void trace(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.DEBUG))
            _logInstance.debug(format(message, t));
    }

    public void info(Object message) {
        if (_logInstance.isEnabledFor(Level.INFO))
            _logInstance.info(message);
    }

    public void info(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.INFO))
            _logInstance.info(format(message, t));
    }

    public void warn(Object message) {
        if (_logInstance.isEnabledFor(Level.WARN))
            _logInstance.warn(message);
    }

    public void warn(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.WARN))
            _logInstance.warn(format(message, t));
    }

    public void error(Object message) {
        if (_logInstance.isEnabledFor(Level.ERROR))
            _logInstance.error(message);
    }

    public void error(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.ERROR))
            _logInstance.error(format(message, t));
    }

    public void fatal(Object message) {
        if (_logInstance.isEnabledFor(Level.FATAL))
            _logInstance.fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        if (_logInstance.isEnabledFor(Level.FATAL))
            _logInstance.fatal(format(message, t));
    }

    private String format(Object m, Throwable t) {
        if (t == null)
            return m.toString();

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));

        return m + "\n\n" + "Throwable: " + t.toString() + "\nStack Trace:\n" + sw.toString();
    }
}
