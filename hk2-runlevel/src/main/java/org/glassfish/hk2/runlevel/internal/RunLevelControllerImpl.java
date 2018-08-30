/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.runlevel.CurrentlyRunningException;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

/**
 * This is the implementation of the RunLevelController
 * 
 * @author jwells
 */
@Service
@ContractsProvided(RunLevelController.class)
@Visibility(DescriptorVisibility.LOCAL)
public class RunLevelControllerImpl implements RunLevelController {
    @Inject
    private AsyncRunLevelContext context;
     
    @Override
    public void proceedTo(int runLevel) {
        RunLevelFuture future = context.proceedTo(runLevel);
        
        try {
            future.get();
        }
        catch (InterruptedException e) {
            throw new MultiException(e);
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            
            throw new MultiException(cause);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#proceedTo(int)
     */
    @Override
    public RunLevelFuture proceedToAsync(int runLevel)
            throws CurrentlyRunningException, IllegalStateException {
        if (context.getPolicy().equals(ThreadingPolicy.USE_NO_THREADS)) {
            throw new IllegalStateException("Cannot use proceedToAsync if the threading policy is USE_NO_THREADS");
        }
        
        return context.proceedTo(runLevel);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#getCurrentProceeding()
     */
    @Override
    public RunLevelFuture getCurrentProceeding() {
        return context.getCurrentFuture();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#cancel()
     */
    @Override
    public void cancel() {
        RunLevelFuture rlf = getCurrentProceeding();
        if (rlf == null) return;
        
        rlf.cancel(false);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#getCurrentRunLevel()
     */
    @Override
    public int getCurrentRunLevel() {
        return context.getCurrentLevel();
    }

    @Override
    public void setMaximumUseableThreads(int maximumThreads) {
        if (maximumThreads < 1) {
            throw new IllegalArgumentException("maximumThreads must be at least 1, but it is " +
                maximumThreads);
        }
        
        context.setMaximumThreads(maximumThreads);
    }

    @Override
    public int getMaximumUseableThreads() {
        return context.getMaximumThreads();
    }

    @Override
    public void setThreadingPolicy(ThreadingPolicy policy) {
        if (policy == null) throw new IllegalArgumentException();
        context.setPolicy(policy);
        
    }

    @Override
    public ThreadingPolicy getThreadingPolicy() {
        return context.getPolicy();
    }

    @Override
    public void setExecutor(Executor executor) {
        context.setExecutor(executor);
        
    }

    @Override
    public Executor getExecutor() {
        return context.getExecutor();
    }

    @Override
    public long getCancelTimeoutMilliseconds() {
        return context.getCancelTimeout();
    }

    @Override
    public void setCancelTimeoutMilliseconds(long cancelTimeout) {
        if (cancelTimeout < 1L) throw new IllegalArgumentException();
        
        context.setCancelTimeout(cancelTimeout);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#getValidationOverride()
     */
    @Override
    public Integer getValidationOverride() {
        return context.getModeOverride();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelController#setValidationOverride(java.lang.Integer)
     */
    @Override
    public void setValidationOverride(Integer validationMode) {
        if (validationMode != null &&
                validationMode.intValue() != RunLevel.RUNLEVEL_MODE_NON_VALIDATING &&
                validationMode.intValue() != RunLevel.RUNLEVEL_MODE_VALIDATING) {
            throw new IllegalArgumentException("validationMode must either be validating or non validating: " + validationMode);
        }
        
        context.setModeOverride(validationMode);
    }

}
