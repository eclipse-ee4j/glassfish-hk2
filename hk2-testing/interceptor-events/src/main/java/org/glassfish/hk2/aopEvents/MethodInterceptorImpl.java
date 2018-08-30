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

package org.glassfish.hk2.aopEvents;

import java.util.HashSet;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service @ContractsProvided({MethodInterceptorImpl.class, MethodInterceptor.class})
public class MethodInterceptorImpl implements MethodInterceptor {
    private final HashSet<String> intercepted = new HashSet<String>();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation arg0) throws Throwable {
        intercepted.add(arg0.getThis().getClass().getSuperclass().getName());
        return arg0.proceed();
    }

    public HashSet<String> clearAndGetIntercepted() {
        HashSet<String> retVal = new HashSet<String>(intercepted);
        intercepted.clear();
        return retVal;
    }
}
