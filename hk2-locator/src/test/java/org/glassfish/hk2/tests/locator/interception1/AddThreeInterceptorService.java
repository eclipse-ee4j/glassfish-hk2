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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class AddThreeInterceptorService implements InterceptionService {

    @Override
    public Filter getDescriptorFilter() {
        return BuilderHelper.allFilter();
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.getName().equals("addOne")) {
            LinkedList<MethodInterceptor> retVal = new LinkedList<MethodInterceptor>();
            
            retVal.add(new AddOneInterceptor());
            retVal.add(new AddOneInterceptor());
            retVal.add(new AddOneInterceptor());
            
            return retVal;
        }
        
        return null;
    }
    
    private static class AddOneInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            int inputValue = ((Integer) invocation.getArguments()[0]);
            invocation.getArguments()[0] = new Integer(inputValue + 1);
            
            int outputValue = ((Integer) invocation.proceed());
            
            return new Integer(outputValue + 1);
        }
        
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        return null;
    }

}
