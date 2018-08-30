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

package org.glassfish.hk2.tests.locator.injector;

import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.PreDestroy;

/**
 * @author jwells
 *
 */
public class ImplementsLifecycleInterfaces implements PostConstruct, PreDestroy {
    private boolean postCalled = false;
    private boolean preCalled = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.PostConstruct#postConstruct()
     */
    @Override
    public void postConstruct() {
        postCalled = true;

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.PreDestroy#preDestroy()
     */
    @Override
    public void preDestroy() {
        preCalled = true;
        
    }

    /**
     * @return the postCalled
     */
    public boolean isPostCalled() {
        return postCalled;
    }

    /**
     * @return the preCalled
     */
    public boolean isPreCalled() {
        return preCalled;
    }
    
    

}
