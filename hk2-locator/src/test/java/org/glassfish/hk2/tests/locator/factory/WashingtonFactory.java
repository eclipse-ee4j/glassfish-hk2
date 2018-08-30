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

package org.glassfish.hk2.tests.locator.factory;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;

/**
 * @author jwells
 *
 */
@Singleton
public class WashingtonFactory implements Factory<President> {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override @Singleton @Named(FactoryTest.WASHINGTON_NAME)
    public President provide() {
        return new President() {

            @Override
            public int getNumber() {
                return FactoryTest.WASHINGTON_NUMBER;
            }
            
        };
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(President instance) {
        throw new AssertionError("not called");
        
    }

}
