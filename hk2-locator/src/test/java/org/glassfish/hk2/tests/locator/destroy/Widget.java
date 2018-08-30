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

package org.glassfish.hk2.tests.locator.destroy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
@PerLookup
public class Widget {
    private boolean destroyCalled = false;
    
    @Inject
    private Sprocket sprocket;
    
    /**
     * Uses the socket to ensure it has been properly constructed before I have
     */
    @PostConstruct
    public void postConstruct() {
        sprocket.useMe();
    }
    
    /**
     * Uses the sprocket to ensure it has NOT been destroyed yet
     */
    @PreDestroy
    public void preDestroy() {
        sprocket.useMe();
        destroyCalled = true;
    }
    
    /**
     * Called by the test to be sure the destruction happened
     * @return true if the destroy has been called
     */
    public boolean isDestroyed() {
        return destroyCalled;
    }
    
    /**
     * This method should throw IllegalStateException since it will be
     * called by the test after destruction
     */
    public void badUse() {
        sprocket.useMe();
    }
    
    public SprocketFactory getSprocketFactory() {
        return sprocket.getCreator();
        
    }
}
