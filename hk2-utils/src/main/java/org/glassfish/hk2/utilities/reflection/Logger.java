/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.utilities.reflection;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

/**
 * A logger for HK2.  Currently implemented over the JDK logger
 * 
 * @author jwells
 */
public class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final String HK2_LOGGER_NAME = "org.jvnet.hk2.logger";
    private static final boolean STDOUT_DEBUG = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.parseBoolean(
                System.getProperty("org.jvnet.hk2.logger.debugToStdout", "false"));
        }
            
    });
    
    private final java.util.logging.Logger jdkLogger;
    
    private Logger() {
        jdkLogger = java.util.logging.Logger.getLogger(HK2_LOGGER_NAME);
    }
    
    /**
     * Gets the singleton instance of the Logger
     * @return The singleton logger instance (will not return null)
     */
    public static Logger getLogger() {
        return INSTANCE;
    }

    /**
     * Sends this message to the Debug channel (FINER level in JDK parlance)
     * 
     * @param debuggingMessage The non-null message to log to the debug logger
     */
    public void debug(String debuggingMessage) {
        jdkLogger.finer(debuggingMessage);
        if (STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: " + debuggingMessage);
        }
    }
    
    /**
     * Sends this message to the Debug channel (FINER level in JDK parlance)
     * 
     * @param debuggingMessage The non-null message to log to the debug logger
     */
    public void debug(String debuggingMessage, Throwable th) {
        jdkLogger.log(Level.FINER, debuggingMessage, th);
        if (STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: " + debuggingMessage);
            printThrowable(th);
        }
    }
    
    /**
     * Sends this message to the Debug channel (FINER level in JDK parlance)
     * 
     * @param warningMessage The non-null message to log to the debug logger
     */
    public void warning(String warningMessage) {
        jdkLogger.warning(warningMessage);
        if (STDOUT_DEBUG) {
            System.out.println("HK2DEBUG (Warning): " + warningMessage);
        }
    }
    
    /**
     * Sends this message to the Debug channel (FINER level in JDK parlance)
     * 
     * @param warningMessage The non-null message to log to the debug logger
     */
    public void warning(String warningMessage, Throwable th) {
        jdkLogger.log(Level.WARNING, warningMessage, th);
        if (STDOUT_DEBUG) {
            System.out.println("HK2DEBUG (Warning): " + warningMessage);
            printThrowable(th);
        }
    }
    
    /**
     * Prints a throwable to stdout
     * 
     * @param th The throwable to print
     */
    public static void printThrowable(Throwable th) {
        int lcv = 0;
        Throwable cause = th;
        
        while (cause != null) {
            System.out.println("HK2DEBUG: Throwable[" + lcv++ + "] message is " + cause.getMessage());
            cause.printStackTrace(System.out);
            
            cause = cause.getCause();
        }
    }
    
    /**
     * Sends this message to the Debug channel (FINER level in JDK parlance)
     * 
     * @param className The name of the class where this was thrown
     * @param methodName The name of the method where this was thrown
     * @param th The exception to log
     */
    public void debug(String className, String methodName, Throwable th) {
        jdkLogger.throwing(className, methodName, th);
        if (STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: className=" + className + " methodName=" + methodName);
            printThrowable(th);
        }
    }
}
