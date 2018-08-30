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
 * This service can be used to add validation points to Descriptors.
 * <p>
 * An implementation of ValidationService must be in the Singleton scope.
 * Implementations of ValidationService will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of ValidationService
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the ValidationService is created
 *
 * @author jwells
 *
 */
@Contract
public interface ValidationService {
    /**
     * This filter will be run at least once per descriptor at the point that the descriptor
     * is being looked up, either with the {@link ServiceLocator} API or due to
     * an &#64;Inject resolution.  The decision made by this filter will be cached and
     * used every time that Descriptor is subsequently looked up.  No validation checks
     * should be done in the returned filter, it is purely meant to limit the
     * {@link Descriptor}s that are passed into the validator.
     * <p>
     * Descriptors passed to this filter may or may not be reified.  The filter should try as
     * much as possible to do its work without reifying the descriptor.  
     * <p>
     * The filter may be run more than once on a descriptor if some condition caused
     * the cache of results per descriptor to become invalidated.
     * 
     * @return The filter to be used to determine if the validators associated with this
     * service should be called when the passed in {@link Descriptor} is looked up
     */
    public Filter getLookupFilter();
    
    /**
     * Returns the {@link Validator} that will be run whenever
     * a {@link Descriptor} that passed the filter is to be looked up with the API
     * or injected into an injection point, or on any bind or unbind operation.
     * If this method returns false then the operation will not proceed.
     * 
     * @return A non-null validator
     */
    public Validator getValidator();
}
