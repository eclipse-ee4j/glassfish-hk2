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

package org.glassfish.hk2.tests.locator.perlookup;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
@Singleton
public class NullInterfaceFactory implements Factory<NullInterface> {
    private boolean disposeCalled = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     * This method intentionally returns null
     */
    @Override @PerLookup
    public NullInterface provide() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(NullInterface instance) {
        if (instance != null) {
            throw new AssertionError("dispose called with a non-null value " + instance);
        }

        disposeCalled = true;
    }
    
    public boolean getDisposeCalled() {
        return disposeCalled;
    }

}
