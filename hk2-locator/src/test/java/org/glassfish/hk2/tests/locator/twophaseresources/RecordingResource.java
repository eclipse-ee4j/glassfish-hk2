/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.twophaseresources;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.TwoPhaseTransactionData;

/**
 * @author jwells
 *
 */
public class RecordingResource implements TwoPhaseResource {
    private final List<TwoPhaseTransactionData> prepares = new ArrayList<TwoPhaseTransactionData>();
    private final List<TwoPhaseTransactionData> commits = new ArrayList<TwoPhaseTransactionData>();
    private final List<TwoPhaseTransactionData> rollbacks = new ArrayList<TwoPhaseTransactionData>();
    
    private final boolean failInPrepare;
    private final boolean failInActivate;
    private final boolean failInRollback;
    
    public RecordingResource(boolean failInPrepare, boolean failInActivate, boolean failInRollback) {
        this.failInPrepare = failInPrepare;
        this.failInActivate = failInActivate;
        this.failInRollback = failInRollback;
    }
    
    public List<TwoPhaseTransactionData> getPrepares() {
        return prepares;
    }
    
    public List<TwoPhaseTransactionData> getCommits() {
        return commits;
    }
    
    public List<TwoPhaseTransactionData> getRollbacks() {
        return rollbacks;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.TwoPhaseResource#prepareDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
     */
    @Override
    public synchronized void prepareDynamicConfiguration(
            TwoPhaseTransactionData dynamicConfiguration) throws MultiException {
        prepares.add(dynamicConfiguration);
        if (failInPrepare) {
            throw new MultiException(new IllegalStateException("Was told to fail in prepare"));
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.TwoPhaseResource#activateDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
     */
    @Override
    public void activateDynamicConfiguration(
            TwoPhaseTransactionData dynamicConfiguration) {
        commits.add(dynamicConfiguration);
        if (failInActivate) {
            throw new MultiException(new IllegalStateException("Was told to fail in activate"));
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.TwoPhaseResource#rollbackDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
     */
    @Override
    public void rollbackDynamicConfiguration(
            TwoPhaseTransactionData dynamicConfiguration) {
        rollbacks.add(dynamicConfiguration);
        if (failInRollback) {
            throw new MultiException(new IllegalStateException("Was told to fail in rollback"));
        }

    }

}
