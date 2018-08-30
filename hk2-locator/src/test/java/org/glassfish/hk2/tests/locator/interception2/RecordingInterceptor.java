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

package org.glassfish.hk2.tests.locator.interception2;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;

/**
 * @author jwells
 *
 */
@Singleton
public class RecordingInterceptor implements ConstructorInterceptor {
    private final List<Constructor<?>> constructorsCalled = new LinkedList<Constructor<?>>();
    private final Map<Constructor<?>, Object> constructorOutCall = new LinkedHashMap<Constructor<?>, Object>();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object construct(ConstructorInvocation invocation) throws Throwable {
        constructorsCalled.add(invocation.getConstructor());
        
        Object retVal;
        try {
            retVal = invocation.proceed();
        }
        catch (Throwable th) {
            constructorOutCall.put(invocation.getConstructor(), th);
            throw th;
        }
        
        constructorOutCall.put(invocation.getConstructor(), retVal);
        return retVal;
    }
    
    public List<Constructor<?>> getConstructorsCalled() {
        return constructorsCalled;
    }
    
    public Map<Constructor<?>, Object> getConstructorOutCall() {
        return constructorOutCall;
    }

}
