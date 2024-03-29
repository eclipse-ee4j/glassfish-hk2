/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ImmediateController;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Operation;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.utilities.ImmediateContext;

/**
 * The implementation of the immediate context.  This should NOT be added
 * automatically, and hence is not annotated with {@link org.jvnet.hk2.annotations.Service}
 * <p>
 * This implementation uses a lot of facilities of hk2, so lets explain each one.
 * <p>
 * The thing that makes Immediate services immediate is that they are started as soon as
 * they are noticed.  This is done by implementing the DynamicConfigurationListener, as it
 * will get notified when a configuration has changed.  However, since the creation of user
 * services can take an arbitrarily long time it is better to do that work on a separate
 * thread, which is why we implement Runnable.  The run method is the method that will pull
 * from the queue of work and instantiate (and destroy) the immediate services.
 * <p>
 * However, there is also a desire to be highly efficient.  This means we should not be
 * creating this thread if there is no work for the thread to do.  For this to work
 * we need to know which configuration changes have added or removed an Immediate service.
 * To know this we have implemented the Validation service and Validator.  The validation
 * service records the thread id of a configuration operation that is adding or removing
 * services.  This thread id goes into a map.  But we have to be sure that the map does not
 * grow without bound. To do this we clear the map both when the configuration service has
 * succeeded (in the DynamicConfigurationListener) and when the configuration service has failed
 * (in the ErrorService).  This is why we have implemented the ErrorService.
 * 
 * @author jwells
 */
@Singleton @Visibility(DescriptorVisibility.LOCAL)
public class ImmediateHelper implements DynamicConfigurationListener, Runnable,
    ValidationService, ErrorService, Validator, ImmediateController {
    private static final ThreadFactory THREAD_FACTORY = new ImmediateThreadFactory();
    
    private static final Executor DEFAULT_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(true),
            THREAD_FACTORY);
    
    private final ServiceLocator locator;
    private final ImmediateContext immediateContext;
    
    private final HashSet<Long> tidsWithWork = new HashSet<Long>();
    
    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition condition = queueLock.newCondition();
    private boolean threadAvailable;
    private boolean outstandingJob;
    private boolean waitingForWork;
    private boolean firstTime = true;
    private ImmediateServiceState currentState = ImmediateServiceState.SUSPENDED;
    private Executor currentExecutor = DEFAULT_EXECUTOR;
    private long decayTime = 20 * 1000;
    
    @Inject
    private ImmediateHelper(ServiceLocator serviceLocator, ImmediateContext immediateContext) {
        this.locator = serviceLocator;
        this.immediateContext = immediateContext;
    }
    
    /**
     * Must have queueLock held
     * @return true if there is work to be done
     */
    private boolean hasWork() {
        long tid = Thread.currentThread().getId();
        
        boolean wasFirst = firstTime;
        firstTime = false;
            
        boolean retVal = tidsWithWork.contains(tid);
        tidsWithWork.remove(tid);
        
        if (retVal || !wasFirst) return retVal;
        
        // OK, we added no Immediate services BUT this is the
        // first DynamicConfigurationService callback, which means
        // that there may already be immediate services in the
        // service locator
        
        List<ActiveDescriptor<?>> immediates = getImmediateServices();
        return !immediates.isEmpty();
    }
    
    /**
     * Must have queueLock held
     */
    private void doWorkIfWeHaveSome() {
        if (!hasWork()) {
            return;
        }
        
        outstandingJob = true;
            
        if (!threadAvailable) {
            threadAvailable = true;
                
            currentExecutor.execute(this);
        }
        else if (waitingForWork) {
            condition.signal();
        }
    }
    
    @Override
    public void configurationChanged() {
        queueLock.lock();
        try {
            if (currentState.equals(ImmediateServiceState.SUSPENDED)) return;
            
            doWorkIfWeHaveSome();
        } finally {
            queueLock.unlock();
        }
    }
    
    @Override
    public Filter getLookupFilter() {
        return immediateContext.getValidationFilter();
    }

    @Override
    public Validator getValidator() {
        return this;
    }

    @Override
    public void onFailure(ErrorInformation errorInformation)
            throws MultiException {
        if (!(ErrorType.DYNAMIC_CONFIGURATION_FAILURE.equals(errorInformation.getErrorType()))) {
            // Only interested in dynamic configuration failures
            long tid = Thread.currentThread().getId();
            queueLock.lock();
            try {
                tidsWithWork.remove(tid);
            } finally {
                queueLock.unlock();
            }
            
            return;
        }
        
    }

    @Override
    public boolean validate(ValidationInformation info) {
        if (info.getOperation().equals(Operation.BIND) ||
                info.getOperation().equals(Operation.UNBIND)) {
            long tid = Thread.currentThread().getId();
            
            queueLock.lock();
            try {
                tidsWithWork.add(tid);
            } finally {
                queueLock.unlock();
            }
        }
        
        return true;
    }
    
    /**
     * This thread will wait twenty seconds for new work to come in, and then
     * kill itself
     */
    @Override
    public void run() {
        for(;;) {
            queueLock.lock();
            try {
                long decayTime = this.decayTime;
                
                while (currentState.equals(ImmediateServiceState.RUNNING) &&
                        !outstandingJob &&
                        (decayTime > 0L)) {
                    waitingForWork = true;
                    long currentTime = System.currentTimeMillis();
                    try {
                        condition.await(decayTime, TimeUnit.MILLISECONDS);
                    }
                    catch (InterruptedException ie) {
                        threadAvailable = false;
                        waitingForWork = false;
                        return;
                    }
                    
                    long elapsedTime = System.currentTimeMillis() - currentTime;
                    decayTime -= elapsedTime;
                }
                waitingForWork = false;
                
                if (!outstandingJob || currentState.equals(ImmediateServiceState.SUSPENDED)) {
                    threadAvailable = false;
                    return;
                }
                
                outstandingJob = false;
            } finally {
                queueLock.unlock();
            }
            
            immediateContext.doWork();
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#getExecutor()
     */
    @Override
    public Executor getExecutor() {
        queueLock.lock();
        try {
            return currentExecutor;
        } finally {
            queueLock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#setExecutor(java.util.concurrent.Executor)
     */
    @Override
    public void setExecutor(Executor executor) throws IllegalStateException {
        queueLock.lock();
        try {
            if (currentState.equals(ImmediateServiceState.RUNNING)) {
                throw new IllegalStateException("ImmediateSerivce attempt made to change executor while in RUNNING state");
            }
            
            currentExecutor = (executor == null) ? DEFAULT_EXECUTOR : executor ;
        } finally {
            queueLock.unlock();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#getThreadInactivityTimeout()
     */
    @Override
    public long getThreadInactivityTimeout() {
        queueLock.lock();
        try {
            return decayTime;
        } finally {
            queueLock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#setThreadInactivityTimeout(long)
     */
    @Override
    public void setThreadInactivityTimeout(long timeInMillis)
            throws IllegalStateException {
        queueLock.lock();
        try {
            if (timeInMillis < 0) {
                throw new IllegalArgumentException();
            }
            
            decayTime = timeInMillis;
        } finally {
            queueLock.unlock();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#getImmediateState()
     */
    @Override
    public ImmediateServiceState getImmediateState() {
        queueLock.lock();
        try {
            return currentState;
        } finally {
            queueLock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ImmediateController#setImmediateState(org.glassfish.hk2.api.ImmediateController.ImmediateServiceState)
     */
    @Override
    public void setImmediateState(ImmediateServiceState state) {
        queueLock.lock();
        try {
            if (state == null) throw new IllegalArgumentException();
            
            if (state == currentState) return;
            currentState = state;
            
            if (currentState.equals(ImmediateServiceState.RUNNING)) {
                doWorkIfWeHaveSome();
            }
        } finally {
            queueLock.unlock();
        }
        
    }
    
    private List<ActiveDescriptor<?>> getImmediateServices() {
        List<ActiveDescriptor<?>> inScopeAndInThisLocator;
        try {
            inScopeAndInThisLocator = locator.getDescriptors(immediateContext.getValidationFilter());
        }
        catch (IllegalStateException ise) {
            // locator has been shutdown
            inScopeAndInThisLocator = Collections.emptyList();
        }
        
        return inScopeAndInThisLocator;
    }
    
    private static class ImmediateThread extends Thread {
        private ImmediateThread(Runnable r) {
            super(r);
            setDaemon(true);
            setName(getClass().getSimpleName() + "-"
                    + System.currentTimeMillis());
        }
    }
    
    private static class ImmediateThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread activeThread = new ImmediateThread(runnable);
                
            return activeThread;
        }
    }
}
