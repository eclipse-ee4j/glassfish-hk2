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

package org.jvnet.hk2.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.TwoPhaseTransactionData;

/**
 * @author jwells
 *
 */
public class TwoPhaseTransactionDataImpl implements TwoPhaseTransactionData {
    private final List<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
    private final List<ActiveDescriptor<?>> removed = new LinkedList<ActiveDescriptor<?>>();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.TwoPhaseTransactionData#getAllAddedDescriptors()
     */
    @Override
    public List<ActiveDescriptor<?>> getAllAddedDescriptors() {
        return Collections.unmodifiableList(new ArrayList<ActiveDescriptor<?>>(added));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.TwoPhaseTransactionData#getAllRemovedDescriptors()
     */
    @Override
    public List<ActiveDescriptor<?>> getAllRemovedDescriptors() {
        return Collections.unmodifiableList(new ArrayList<ActiveDescriptor<?>>(removed));
    }
    
    /* package */ void toAdd(ActiveDescriptor<?> addMe) {
        added.add(addMe);
    }
    
    /* package */ void toRemove(ActiveDescriptor<?> removeMe) {
        removed.add(removeMe);
    }
    
    @Override
    public String toString() {
        return "TwoPhaseTransactionalDataImpl(" + System.identityHashCode(this) + ")";
    }

}
