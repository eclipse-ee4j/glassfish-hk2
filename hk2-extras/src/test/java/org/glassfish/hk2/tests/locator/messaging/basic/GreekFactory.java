/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.messaging.basic;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.messaging.MessageReceiver;

/**
 * @author jwells
 *
 */
@Singleton
public class GreekFactory implements Factory<Greek> {
    private final static int MAX = 3;
    
    private int cycler = MAX - 1;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override @PerLookup @MessageReceiver
    public Greek provide() {
        cycler = ((cycler + 1) % MAX);
        
        switch (cycler) {
        case 0:
            return new Alpha();
        case 1:
            return new Beta();
        case 2:
            return new Gamma();
        default:
            throw new IllegalStateException("Unknown cycle " + cycler);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(Greek instance) {
      // Do nothing
    }

}
