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

package org.glassfish.hk2.runlevel;

import java.util.List;

import org.glassfish.hk2.api.ServiceHandle;
import org.jvnet.hk2.annotations.Contract;


/**
 * Contract for sorting descriptors for run level services.
 * It should be noted that sorting the handles in a
 * multi-threaded environment is a heuristic in any case,
 * as the threads are scheduled randomly.  Sorting can
 * only be guaranteed in a single threaded or no-threaded
 * case.
 *
 * @author tbeerbower, jwells
 */
@Contract
public interface Sorter {

    /**
     * Sort the given list of run level service handles.  This
     * method will only be called when the run-level is going
     * up in value.  When going down in value services are always
     * stopped in the reverse order from which they were started
     *
     * @param descriptors the list descriptors to be sorted
     * @return The list as sorted.  If this returns null then
     * the list as passed in will be used.  If any of the
     * service handles returned are not in the run-level
     * being processed they will be ignored.  This list
     * may add or remove handles to the list
     */
    public List<ServiceHandle<?>> sort(List<ServiceHandle<?>> descriptors);
}
