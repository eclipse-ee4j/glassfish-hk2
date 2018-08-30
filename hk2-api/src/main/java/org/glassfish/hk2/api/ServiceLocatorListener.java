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

package org.glassfish.hk2.api;

import java.util.Set;

/**
 * This is added to the {@link ServiceLocatorFactory} in order to listen on service locators
 * coming and going.  Implementations of this interface must be able to be stored in a HashMap
 * 
 * @author jwells
 *
 */
public interface ServiceLocatorListener {
    /**
     * This method returns the complete list of named service
     * locators at the time that this listener is registered.  The list
     * may be empty.  This method will NOT pass any unnamed
     * ServiceLocators, as they are not tracked by the system
     * <p>
     * Any exceptions thrown from this method will be logged
     * and ignored.  If an exception is thrown from
     * this method then this listener will NOT be added
     * to the set of listeners notified by the system
     * 
     * @param initialLocators The set of named locators available when
     * the listener is registered
     */
    public void initialize(Set<ServiceLocator> initialLocators);
    
    /**
     * This method is called whenever a ServiceLocator has been
     * added to the set of ServiceLocators.  This method
     * WILL be passed unnamed ServiceLocators when they are added
     * <p>
     * Any exceptions thrown from this method will be logged
     * and ignored
     * 
     * @param added The non-null ServiceLocator that is to be added
     */
    public void locatorAdded(ServiceLocator added);
    
    /**
     * This method is called whenever a ServiceLocator will be
     * removed from the set of ServiceLocators.  This method WILL
     * be passed unnamed ServiceLocators when they are destroyed
     * <p>
     * Any exceptions thrown from this method will be logged
     * and ignored
     * 
     * @param destroyed The non-null ServiceLocator that is to be destroyed
     */
    public void locatorDestroyed(ServiceLocator destroyed);

}
