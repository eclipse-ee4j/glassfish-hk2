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

package org.jvnet.hk2.internal;

import java.io.Serializable;
import java.util.Comparator;

import org.glassfish.hk2.api.ServiceHandle;

/**
 * @author jwells
 *
 */
public class ServiceHandleComparator implements Comparator<ServiceHandle<?>>, Serializable {
    /**
     * For serialization
     */
    private static final long serialVersionUID = -3475592779302344427L;
    
    private final DescriptorComparator baseComparator = new DescriptorComparator();

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ServiceHandle<?> o1, ServiceHandle<?> o2) {
        return baseComparator.compare(o1.getActiveDescriptor(), o2.getActiveDescriptor());
    }

}
