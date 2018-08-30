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

package org.glassfish.hk2.noproxy2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class ConstructorInterceptionService implements InterceptionService {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getDescriptorFilter()
     */
    @Override
    public Filter getDescriptorFilter() {
        return BuilderHelper.createContractFilter(ConstructorSingletonService.class.getName());
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getMethodInterceptors(java.lang.reflect.Method)
     */
    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getConstructorInterceptors(java.lang.reflect.Constructor)
     */
    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        List<ConstructorInterceptor> retVal = Collections.<ConstructorInterceptor>singletonList(new ConstructorInterceptorImpl());
        return retVal;
    }
    
    private static class ConstructorInterceptorImpl implements ConstructorInterceptor {

        /* (non-Javadoc)
         * @see org.aopalliance.intercept.ConstructorInterceptor#construct(org.aopalliance.intercept.ConstructorInvocation)
         */
        @Override
        public Object construct(ConstructorInvocation arg0) throws Throwable {
            throw new AssertionError("not called");
        }
        
    }

}
