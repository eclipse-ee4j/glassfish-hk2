/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.operation.shutdown;

import java.util.HashSet;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.tests.operation.basic.BasicOperationScope;

/**
 * @author jwells
 *
 */
@Singleton
public class OperationScopeFactory implements Factory<CreatedByFactory> {
    private final HashSet<CreatedByFactory> created = new HashSet<CreatedByFactory>();
    private final HashSet<CreatedByFactory> destroyed = new HashSet<CreatedByFactory>();
    
    public boolean hasBeenDestroyed(CreatedByFactory checkMe) {
        return destroyed.contains(checkMe);
    }
    

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override
    @BasicOperationScope
    public CreatedByFactory provide() {
        return new CreatedByFactory() {

            @Override
            public void createMe() {
                created.add(this);
                
            }

            @Override
            public void disposeMe() {
                if (!created.contains(this)) throw new AssertionError("Destroying an instance that was not added");
                created.remove(this);  // prevents doubles
                destroyed.add(this);
            }
            
        };
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(CreatedByFactory instance) {
        instance.disposeMe();
    }

}
