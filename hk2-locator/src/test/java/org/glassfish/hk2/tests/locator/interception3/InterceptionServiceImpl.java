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

package org.glassfish.hk2.tests.locator.interception3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class InterceptionServiceImpl implements InterceptionService {
    private final static Filter FILTER = BuilderHelper.createContractFilter(InterceptedService.class.getName());

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getDescriptorFilter()
     */
    @Override
    public Filter getDescriptorFilter() {
        return FILTER;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getMethodInterceptors(java.lang.reflect.Method)
     */
    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        List<MethodInterceptor> retVal = new LinkedList<MethodInterceptor>();
        
        retVal.add(new InterceptorOne());
        retVal.add(new InterceptorTwo());
        retVal.add(new InterceptorThree());
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getConstructorInterceptors(java.lang.reflect.Constructor)
     */
    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        List<ConstructorInterceptor> retVal = new LinkedList<ConstructorInterceptor>();
        
        retVal.add(new CInterceptorOne());
        retVal.add(new CInterceptorTwo());
        retVal.add(new CInterceptorThree());
        
        return retVal;
    }

}
