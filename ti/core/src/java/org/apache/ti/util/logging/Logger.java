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
package org.apache.ti.util.logging;

import org.apache.commons.logging.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * <p/>
 * Logging abstraction for NetUI.  This class leverages Jakarta commons-logging to create
 * loggers for NetUI messages.  Application developers can provide their own logger
 * implementations by following the instructions for creating new Log / LogFactory instances
 * in commons-logging.
 * </p>
 */
public class Logger
        implements Log {

    /**
     * Factory method for creating NetUI Logger instances.
     *
     * @param loggerClient the class whose logger to create
     * @return a {@link Logger} instance for the given class
     */
    public static Logger getInstance(Class loggerClient) {
        return new Logger(org.apache.commons.logging.LogFactory.getLog(loggerClient.getName()));
    }

    private Log _logDelegate = null;

    /**
     * Constructor that returns a Log4J logger.  This method is deprecated
     * in favor of using commons-logging to do logger creation via the
     * {@link #getInstance(Class)} method.
     *
     * @param clientClass
     * @see #getInstance(Class)
     * @deprecated
     */
    public Logger(Class clientClass) {
        _logDelegate = createDefaultLogger(clientClass);
    }

    /**
     * Constructor that returns a Log4J logger.  This method is deprecated
     * in favor of using commons-logging to do logger creation via the
     * {@link #getInstance(Class)} method.
     *
     * @param clientClassName
     * @see #getInstance(Class)
     * @deprecated
     */
    public Logger(String clientClassName) {
        Class clientClass = null;
        try {
            /* create a default log4j logger -- this shouldn't throw a CNF exception */
            clientClass = Class.forName(clientClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not load NetUI logger client class '" + clientClassName + "'");
        }
        _logDelegate = createDefaultLogger(clientClass);
    }

    /**
     * Internal method used by the factory to create a Logger instance.
     *
     * @param logDelegate the commons-logging {@link Log} to which messages should be logged
     */
    private Logger(Log logDelegate) {
        _logDelegate = logDelegate;
    }

    public boolean isDebugEnabled() {
        return _logDelegate.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return _logDelegate.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return _logDelegate.isFatalEnabled();
    }

    public boolean isInfoEnabled() {
        return _logDelegate.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return _logDelegate.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return _logDelegate.isWarnEnabled();
    }

    public void debug(Object message) {
        if (isDebugEnabled())
            _logDelegate.debug(message);
    }

    public void debug(Object message, Throwable t) {
        if (isDebugEnabled())
            _logDelegate.debug(format(message, t));
    }

    public void trace(Object message) {
        if (isTraceEnabled())
            _logDelegate.trace(message);
    }

    public void trace(Object message, Throwable t) {
        if (isTraceEnabled())
            _logDelegate.trace(format(message, t));
    }

    public void info(Object message) {
        if (isInfoEnabled())
            _logDelegate.info(message);
    }

    public void info(Object message, Throwable t) {
        if (isInfoEnabled())
            _logDelegate.info(format(message, t));
    }

    public void warn(Object message) {
        if (isWarnEnabled())
            _logDelegate.warn(message);
    }

    public void warn(Object message, Throwable t) {
        if (isWarnEnabled())
            _logDelegate.warn(format(message, t));
    }

    public void error(Object message) {
        if (isErrorEnabled())
            _logDelegate.error(message);
    }

    public void error(Object message, Throwable t) {
        if (isErrorEnabled())
            _logDelegate.error(format(message, t));
    }

    public void fatal(Object message) {
        if (isFatalEnabled())
            _logDelegate.fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        if (isFatalEnabled())
            _logDelegate.fatal(format(message, t));
    }

    private String format(Object m, Throwable t) {
        if (t == null)
            return m.toString();

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        
        /* note, no reason to close a StringWriter */
        
        return m + "\n\n" + "Throwable: " + t.toString() + "\nStack Trace:\n" + sw.toString();
    }

    /**
     * Internal method used to create the backwards-compat NetUI logger.  This method
     * looks up the {@link org.apache.beehive.netui.util.logging.internal.Log4JLogger}
     * and creates a new instance returning the resulting {@link Log}.
     *
     * @param loggerClient the logger client
     * @return the {@link Log} instance
     */
    private static final Log createDefaultLogger(Class loggerClient) {
        assert loggerClient != null : "Received a null loggerClient Class";

        String className = "org.apache.beehive.netui.util.logging.internal.Log4JLogger";
        try {
            Class logDelegateClass = Logger.class.getClassLoader().loadClass(className);
            Method method = logDelegateClass.getMethod("getInstance", new Class[]{Class.class});
            return (Log) method.invoke(null, new Object[]{loggerClient});
        } catch (Exception e) {
            IllegalStateException ie = new IllegalStateException("Could not create log implementation '" + className +
                    "' for client of type '" + loggerClient.getName() + "'");
            ie.initCause(e);
            throw ie;
        }
    }
}
