/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.module.bootstrap;

/**
 * Created by IntelliJ IDEA.
 * User: naman
 * Date: 3 Nov, 2010
 * Time: 12:10:21 PM
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;

public class EarlyLogHandler extends Handler {

    private static int MAX_MESSAGES = 200;

    public final static ArrayBlockingQueue<LogRecord> earlyMessages = new ArrayBlockingQueue<LogRecord>(MAX_MESSAGES);

	/*
    * collect the message that are logged before the log service is started
    * The log manager service will print them out when it is started.
	*/
    public void publish(LogRecord record) {

        //log manager service not started yet so we are queuing up the messages
        try {
            earlyMessages.add(record);
        } catch (IllegalStateException ie) {
            // can't add more messages; something terrible is happening.
            // Dump the queue to a file, need to stop queuing messages
        }
    }

	/*
	* Provide method for users to log their messages.
	*/
	public void logMessage(Level level,String message) {
        LogRecord lr = new LogRecord(level,message);
        publish(lr);
	}  

    /**
    * Called to close this log handler.
    */
    public void close() {
	    // not used
    }

    /**
    * Called to flush any cached data that
    * this log handler may contain.
    */
    public void flush() {
        // not used
    }
} 
