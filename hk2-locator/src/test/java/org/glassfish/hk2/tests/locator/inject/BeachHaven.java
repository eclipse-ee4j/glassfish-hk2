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

package org.glassfish.hk2.tests.locator.inject;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class BeachHaven extends NewJersey {
    @Inject
    private TrackerService fieldTracker;
    
    private TrackerService methodTracker;
    
    private TrackerService overriddenButPrivate;
    
    private TrackerService overriddenPublic;
    
    @SuppressWarnings("unused")
    @Inject
    private void setBeachHavenMethodTracker(TrackerService methodTracker) {
        this.methodTracker = methodTracker;
    }
    
    public TrackerService getBeachHavenFieldTracker() {
        return fieldTracker;
    }
    
    public TrackerService getBeachHavenMethodTracker() {
        return methodTracker;
    }
    
    /**
     * This method is overridden, but since it is private it should get called anyway
     * @param methodTracker
     */
    @SuppressWarnings("unused")
    @Inject
    private void setOverriddenButPrivateMethodTracker(TrackerService methodTracker) {
        this.overriddenButPrivate = methodTracker;
    }
    
    /**
     * This method is the final overrider, and hence should be the one to get called
     * 
     * @param methodTracker
     */
    @Inject
    public void setOverriddenPublicMethodTracker(TrackerService methodTracker) {
        this.overriddenPublic = methodTracker;
    }
    
    public TrackerService getBeachHavenOverridenButPrivateMethodTracker() {
        return overriddenButPrivate;
    }
    
    public TrackerService getBeachHavenOverridenPublicMethodTracker() {
        return overriddenPublic;
    }

}
