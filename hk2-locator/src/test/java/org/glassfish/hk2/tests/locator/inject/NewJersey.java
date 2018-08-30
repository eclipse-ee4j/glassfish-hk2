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

/**
 * @author jwells
 *
 */
public class NewJersey extends UnitedStates {
    @Inject
    public TrackerService fieldTracker;
    
    private TrackerService methodTracker;
    
    private TrackerService overriddenButPrivate;
    
    private TrackerService overriddenPublic;
    
    @SuppressWarnings("unused")
    @Inject
    private void setNewJerseyMethodTracker(TrackerService methodTracker) {
        this.methodTracker = methodTracker;
    }
    
    public TrackerService getNewJerseyFieldTracker() {
        return fieldTracker;
    }
    
    public TrackerService getNewJerseyMethodTracker() {
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
     * This method is truly overriden, and hence should NOT get called by the system
     * @param methodTracker
     */
    @Inject
    public void setOverriddenPublicMethodTracker(TrackerService methodTracker) {
        this.overriddenPublic = methodTracker;
    }
    
    public TrackerService getNewJerseyOverridenButPrivateMethodTracker() {
        return overriddenButPrivate;
    }
    
    public TrackerService getNewJerseyOverridenPublicMethodTracker() {
        return overriddenPublic;
    }

}
