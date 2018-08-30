/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * @author jwells
 * @author mtaube
 */
public class MethodInterceptorImpl implements MethodHandler {
    private final static String PROXY_MORE_METHOD_NAME = "__make";
    
    private final ServiceLocatorImpl locator;
    private final ActiveDescriptor<?> descriptor;
    /** Original root node, needed for proper destruction */
    private final ServiceHandleImpl<?> root;
    /** Actual injectee, needed for InstantiationService */
    private final WeakReference<Injectee> myInjectee;
    
    /* package */ MethodInterceptorImpl(ServiceLocatorImpl sli,
            ActiveDescriptor<?> descriptor,
            ServiceHandleImpl<?> root,
            Injectee injectee) {
        this.locator = sli;
        this.descriptor = descriptor;
        this.root = root;
        if (injectee != null) {
          this.myInjectee = new WeakReference<Injectee>(injectee);
        }
        else {
            this.myInjectee = null;
        }
    }
    
    private Object internalInvoke(Object target, Method method, Method proceed, Object[] params) throws Throwable {
        Context<?> context;
        Object service;

        context = locator.resolveContext(descriptor.getScopeAnnotation());
        service = context.findOrCreate(descriptor, root);

        if (service == null) {
            throw new MultiException(new IllegalStateException("Proxiable context " +
                    context + " findOrCreate returned a null for descriptor " + descriptor +
                    " and handle " + root));
        }

        if (method.getName().equals(PROXY_MORE_METHOD_NAME)) {
            // We did what we came here to do
            return service;
        }
        
        if (isEquals(method) && (params.length == 1) && (params[0] != null) && (params[0] instanceof ProxyCtl)) {
            ProxyCtl equalsProxy = (ProxyCtl) params[0];
            
            params = new Object[1];
            params[0] = equalsProxy.__make();
        }

        return ReflectionHelper.invoke(service, method, params, locator.getNeutralContextClassLoader());
        
    }

    @Override
    public Object invoke(Object target, Method method, Method proceed, Object[] params) throws Throwable {
        boolean pushed = false;
        if (root != null && myInjectee != null) {
            Injectee ref = myInjectee.get();
            if (ref != null) {
                root.pushInjectee(ref);
                pushed = true;
            }
        }
        
        try {
            return internalInvoke(target, method, proceed, params);
        }
        finally {
            if (pushed) {
                root.popInjectee();
            }
        }

    }
    
    private final static String EQUALS_NAME = "equals";
    
    private static boolean isEquals(Method m) {
        if (!m.getName().equals(EQUALS_NAME)) return false;
        Class<?>[] params = m.getParameterTypes();
        if (params == null || params.length != 1) return false;
        
        if (!Object.class.equals(params[0])) return false;
        return true;
    }
}
