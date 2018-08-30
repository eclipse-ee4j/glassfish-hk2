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

package org.glassfish.hk2.tests.locator.perthread;

import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
@PerLookup
public class ClothingStore {
    @Inject
    private Pants field;
    
    private final Pants constructor;
    
    private Pants method;
    
    @Inject
    private ClothingStore(Pants constructor) {
        this.constructor = constructor;
    }
    
    @SuppressWarnings("unused")
    @Inject
    private void init(Pants method) {
        this.method = method;
    }
    
    /**
     * In a single object, all those per-thread lookups should be the same
     */
    /* package */ Pants check() {
        Assert.assertSame(field, constructor);
        Assert.assertSame(field, method);
        Assert.assertSame(constructor, method);
        
        return field; // Any will do at this point
    }
    
    

}
