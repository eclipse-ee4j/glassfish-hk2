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
 * This interface should be implemented by those who wish to be
 * notified of error conditions that occur within HK2.  These
 * errors are those that might happen during normal processing of
 * HK2 requests
 * <p>
 * An implementation of ErrorService must be in the Singleton scope.
 * Implementations of ErrorService will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of ErrorService
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the ErrorService is created
 * 
 * @author jwells
 *
 */
@Contract
public interface ErrorService {
    /**
     * This method is called when a failure occurs in the system.  This method may
     * use any {@link ServiceLocator} api.  For example, an implementation of this method might want
     * to remove a descriptor from the registry if the error can be determined to be a
     * permanent failure.
     * 
     * @param errorInformation Information about the error that occurred
     * @throws MultiException if this method throws an exception that exception will be thrown back to
     * the caller wrapped in another MultiException if the error is of type {@link ErrorType#FAILURE_TO_REIFY}.
     * If the error is of type {@link ErrorType#DYNAMIC_CONFIGURATION_FAILURE} or {@link ErrorType#SERVICE_CREATION_FAILURE}
     * or {@link ErrorType#SERVICE_DESTRUCTION_FAILURE} then any exception thrown from this
     * method is ignored and the original exception is thrown back to the caller
     */
    public void onFailure(ErrorInformation errorInformation)
        throws MultiException;

}
