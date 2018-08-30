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

import org.glassfish.hk2.api.Descriptor;

/**
 * @author jwells
 *
 */
public class DescriptorComparator implements Comparator<Descriptor>, Serializable {

    /**
     * Added for serialization
     */
    private static final long serialVersionUID = 4454509124508404602L;

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Descriptor o1, Descriptor o2) {
        int o1Ranking = o1.getRanking();
        int o2Ranking = o2.getRanking();
        
        if (o1Ranking < o2Ranking) return 1;
        if (o1Ranking > o2Ranking) return -1;
        
        long o1LocatorId = o1.getLocatorId().longValue();
        long o2LocatorId = o2.getLocatorId().longValue();
        
        if (o1LocatorId < o2LocatorId) return 1;
        if (o1LocatorId > o2LocatorId) return -1;
        
        long o1ServiceId = o1.getServiceId().longValue();
        long o2ServiceId = o2.getServiceId().longValue();
        
        if (o1ServiceId > o2ServiceId) return 1;
        if (o1ServiceId < o2ServiceId) return -1;
        
        return 0;
    }

}
