/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.justintime;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
@Singleton
public class IterableProviderService {
    @Inject
    private IterableProvider<SimpleService4> provider;
    
    @Inject
    private Provider<UnimplementedContract> unimplementedContract;
    
    /* package */ void checkGet() {
        Assert.assertNotNull(provider.get());
        
    }
    
    /* package */ void checkUnimplementedGet() {
        Assert.assertNull(unimplementedContract.get());
    }
    
    /* package */ void checkGetHandle() {
        ServiceHandle<SimpleService4> handle = provider.getHandle();
        Assert.assertNotNull(handle);
        Assert.assertNotNull(handle.getService());
        
    }
    
    /* package */ void checkIterator() {
        boolean found = false;
        for (SimpleService4 ss4 : provider) {
            Assert.assertFalse(found);
            found = true;
        }
        
        Assert.assertTrue(found);
        
    }
    
    /* package */ void checkSize() {
        Assert.assertEquals(1, provider.getSize());
        
    }
    
    /* package */ void checkHandleIterator() {
        boolean found = false;
        for (ServiceHandle<SimpleService4> ss4Handle : provider.handleIterator()) {
            Assert.assertFalse(found);
            found = true;
            Assert.assertNotNull(ss4Handle.getService());
        }
        
        Assert.assertTrue(found);
        
    }

}
