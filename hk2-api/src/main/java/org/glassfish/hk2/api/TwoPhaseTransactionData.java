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

package org.glassfish.hk2.api;

import java.util.List;

/**
 * @author jwells
 *
 */
public interface TwoPhaseTransactionData {
    /**
     * Gets all ActiveDescriptors that will be added in this transaction
     * 
     * @return A non-null but possibly empty list of descriptors that will be added
     */
    public List<ActiveDescriptor<?>> getAllAddedDescriptors();
    
    /**
     * Gets all ActiveDescriptors that will be removed by this transaction
     * 
     * @return Null prior to commit being invoked and a non-null but possibly empty list of descriptors that will
     * be removed after commit being invoked
     */
    public List<ActiveDescriptor<?>> getAllRemovedDescriptors();

}
