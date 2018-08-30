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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.jvnet.hk2.annotations.Contract;

/**
 * Implementations of this service will be called whenever
 * an Immediate scoped service fails
 * 
 * @author jwells
 *
 */
@Contract
public interface ImmediateErrorHandler {
    /**
     * This is called whenever an immediate service fails in its constructor or
     * postConstruct method.  Any exceptions from the implementation of
     * this method are ignored
     * 
     * @param immediateService The descriptor of the immediate scope service that failed
     * @param exception The exception that was thrown
     */
    public void postConstructFailed(ActiveDescriptor<?> immediateService, Throwable exception);
    
    /**
     * This is called whenever an immediate service fails in its preDestroy method.
     * Any exceptions from the implementation of this method are ignored
     * 
     * @param immediateService The descriptor of the immediate scope service that failed
     * @param exception The exception that was thrown
     */
    public void preDestroyFailed(ActiveDescriptor<?> immediateService, Throwable exception);

}
