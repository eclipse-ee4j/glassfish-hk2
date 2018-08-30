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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class InterceptionServiceImpl implements InterceptionService {
    @Inject
    private MethodInterceptor interceptor;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getDescriptorFilter()
     */
    @Override
    public Filter getDescriptorFilter() {
        return new Filter() {

            @Override
            public boolean matches(Descriptor d) {
                
                if (EventSubscriberService.class.getName().equals(d.getImplementation())) {
                    return true;
                }
                
                if (ProtectedEventSubscriberService.class.getName().equals(d.getImplementation())) {
                    return true;
                }
                
                if (PackageEventSubscriberService.class.getName().equals(d.getImplementation())) {
                    return true;
                }
                
                if (PrivateEventSubscriberService.class.getName().equals(d.getImplementation())) {
                    return true;
                }
                
                return false;
            }
            
        };
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getMethodInterceptors(java.lang.reflect.Method)
     */
    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (!method.getName().equals("subscribeMe")) {
            return null;
        }
        
        return Collections.singletonList(interceptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getConstructorInterceptors(java.lang.reflect.Constructor)
     */
    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        return null;
    }

}
