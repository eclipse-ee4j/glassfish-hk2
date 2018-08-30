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

package org.glassfish.hk2.tests.locator.interception1;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author jwells
 */
@Singleton
public class RecordingInterceptor implements MethodInterceptor {
    private final List<String> methodInCall = new LinkedList<String>();
    private final Map<String, Object> methodOutCall = new LinkedHashMap<String, Object>();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        methodInCall.add(methodName);
        
        Object retVal;
        try {
            retVal = invocation.proceed();
        }
        catch (Throwable th) {
            methodOutCall.put(methodName, th);
            throw th;
        }
        
        methodOutCall.put(methodName, retVal);
        return retVal;
    }
    
    public List<String> getCalledInMethod() {
        return methodInCall;
    }
    
    public Map<String, Object> getCalledOutMethod() {
        return methodOutCall;
    }

}
