/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.hk2bridge;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author jwells
 *
 */
public abstract class BaseMethodInterceptor implements MethodInterceptor {
    public static final String METHOD_NAME = "tracker";
    
    public abstract String getIdentifier();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Method method = mi.getMethod();
        if (!METHOD_NAME.equals(method.getName())) return mi.proceed();
        
        List<String> arg = (List<String>) mi.getArguments()[0];
        
        arg.add(getIdentifier());
        
        return mi.proceed();
    }

}
