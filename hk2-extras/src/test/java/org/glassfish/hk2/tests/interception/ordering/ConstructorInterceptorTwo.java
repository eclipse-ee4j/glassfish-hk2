/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.interception.ordering;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.extras.interception.Interceptor;
import org.jvnet.hk2.annotations.ContractsProvided;

/**
 * @author jwells
 *
 */
@Singleton @Record @Interceptor @Rank(15)
@ContractsProvided(ConstructorInterceptor.class)
public class ConstructorInterceptorTwo extends BaseInterceptor implements ConstructorInterceptor {

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.ConstructorInterceptor#construct(org.aopalliance.intercept.ConstructorInvocation)
     */
    @Override
    public Object construct(ConstructorInvocation arg0) throws Throwable {
        register();
        
        return arg0.proceed();
    }

}
