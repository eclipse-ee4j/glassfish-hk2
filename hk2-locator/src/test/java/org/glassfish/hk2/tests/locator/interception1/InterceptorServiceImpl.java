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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;

/**
 * 
 * @author jwells
 */
@Singleton
public class InterceptorServiceImpl implements InterceptionService {
    @Inject
    private RecordingInterceptor interceptor;

    @Override
    public Filter getDescriptorFilter() {
        return new Filter() {

            @Override
            public boolean matches(Descriptor d) {
                return SimpleInterceptedService.class.getName().equals(d.getImplementation());
            }
        };
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.getName().equals("callMe")) {
            return Collections.singletonList((MethodInterceptor) interceptor);
        }
        
        return null;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        // TODO Auto-generated method stub
        return null;
    }

}
