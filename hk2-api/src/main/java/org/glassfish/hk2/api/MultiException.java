/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This exception can contain multiple other exceptions.
 * However, it will also have the causal chain of the
 * first exception added to the list of exceptions
 *
 * @author jwells
 *
 */
public class MultiException extends HK2RuntimeException {
    /**
     * For serialization
     */
    private static final long serialVersionUID = 2112432697858621044L;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Throwable> throwables = new LinkedList<Throwable>();
    private boolean reportToErrorService = true;

    /**
     * Creates an empty MultiException
     */
    public MultiException() {
        super();
    }

    /**
     * This list must have at least one element in it.
     * The first element of the list will become the
     * cause of this exception, and its message will become
     * the message of this exception
     *
     * @param ths A non-null, non-empty list of exceptions
     */
    public MultiException(List<Throwable> ths) {
        super(ths.get(0).getMessage(), ths.get(0));

        for (Throwable th : ths) {
            if (th instanceof MultiException) {
                MultiException me = (MultiException) th;
                
                throwables.addAll(me.throwables);
            }
            else {
                throwables.add(th);
            }
        }
    }

    /**
     * This allows for construction of a MultiException
     * with one element in its list
     *
     * @param th May not be null
     */
    public MultiException(Throwable th, boolean reportToErrorService) {
        super(th.getMessage(), th);

        if (th instanceof MultiException) {
            MultiException me = (MultiException) th;
            
            throwables.addAll(me.throwables);
        }
        else {
            throwables.add(th);
        }
        
        this.reportToErrorService = reportToErrorService;
    }
    
    /**
     * This allows for construction of a MultiException
     * with one element in its list
     *
     * @param th May not be null
     */
    public MultiException(Throwable th) {
        this(th, true);
    }

    /**
     * Gets all the errors associated with this MultiException
     *
     * @return All the errors associated with this MultiException. Will
     * not return null, but may return an empty object
     */
    public List<Throwable> getErrors() {
        lock.lock();
        try {
            return new LinkedList<Throwable>(throwables);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds an error to an existing exception
     *
     * @param error The exception to add
     */
    public void addError(Throwable error) {
        lock.lock();
        try {
            throwables.add(error);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets the message associated with this exception
     */
    public String getMessage() {
        List<Throwable> listCopy = getErrors();
        StringBuffer sb = new StringBuffer("A MultiException has " + listCopy.size() + " exceptions.  They are:\n");
        
        int lcv = 1;
        for (Throwable th : listCopy) {
            sb.append(lcv++ + ". " + th.getClass().getName() + ((th.getMessage() != null) ? ": " + th.getMessage() : "" ) + "\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Prints the stack trace of this exception to the given PrintStream
     */
    public void printStackTrace(PrintStream s) {
        List<Throwable> listCopy = getErrors();
        
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);
            return;
        }
        
        int lcv = 1;
        for (Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
        }
    }
    
    /**
     * Prints the stack trace of this exception to the given PrintWriter
     */
    public void printStackTrace(PrintWriter s) {
        List<Throwable> listCopy = getErrors();
        
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);
            return;
        }
        
        int lcv = 1;
        for (Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
        }
    }
    
    /**
     * Returns true if this exception should be reported
     * to the error service when thrown during a creation
     * or deletion of a service
     * 
     * @return true if this exception should be reported to
     * the error service when creating or deleting a service
     */
    public boolean getReportToErrorService() {
        return reportToErrorService;
    }
    
    /**
     * Sets if this exception should be reported
     * to the error service when thrown during a creation
     * or deletion of a service
     * 
     * @param report true if this exception should be reported to
     * the error service when creating or deleting a service
     */
    public void setReportToErrorService(boolean report) {
        reportToErrorService = report;
    }

    @Override
    public String toString() {
        return getMessage();
    }

}
