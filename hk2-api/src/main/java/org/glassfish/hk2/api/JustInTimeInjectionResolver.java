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

package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * The JustInTimeInjectionResolver is called when an injection point
 * cannot find anything to inject.  It allows a third party systems
 * to dynamically add descriptors to the system whenever
 * an injection point would have failed to resolve (or an Optional
 * injection point found no service definitions).
 * <p>
 * All injection resolvers registered with the system will be called
 * in a random order.  Resolvers should therefore not rely on the ordering
 * of installed injection resolvers.  Any injection resolvers added as a
 * result of this callback will NOT be called until the next injection
 * resolution failure.
 * <p>
 * Implementations of this interface are placed into the registry like
 * any other service.  One use-case would be to inject the
 * {@link DynamicConfigurationService} into the implementation in order
 * to add services if this resolver can do so.  Another option would
 * be to inject a {@link ServiceLocator} and use one of the methods
 * in {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities} in order
 * to add services to the registry
 * <p>
 * If any of the registered injection resolvers commits a dynamic change
 * then the system will try one more time to resolve the injection before
 * failing (or returning null if the injection point is Optional).
 * 
 * @author jwells
 */
@Contract
public interface JustInTimeInjectionResolver {
    /**
     * This method will be called whenever an injection point cannot be resolved.  If this
     * method adds anything to the configuration it should return true.  Otherwise it
     * should return false.  The injection point that failed to be resolved is given
     * in failedInjectionPoint.
     * <p>
     * If this method throws an exception that exception will be added to the set of
     * exceptions in the MultiException that may be thrown from the injection resolver.
     * <p>
     * This method can be called on multiple threads with different or the same
     * {@link Injectee}.  Therefore care must be taken in this method to not add
     * the same descriptor more than once
     *
     * @param failedInjectionPoint The injection point that failed to resolve
     * @return true if this method has added a descriptor to the {@link ServiceLocator}
     * which may be used to resolve the {@link Injectee}.  False if this method
     * did not add a descriptor to the {@link ServiceLocator} that might help
     * resolve the injection point
     */
    public boolean justInTimeResolution(Injectee failedInjectionPoint);

}
