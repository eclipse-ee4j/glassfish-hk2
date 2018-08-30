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

package org.glassfish.examples.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * @author jwells
 *
 */
@Singleton
public class AlternateInjectResolver implements InjectionResolver<AlternateInject> {
    @Inject @Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
    private InjectionResolver<Inject> systemResolver;
    
    @Inject
    private HttpRequest request;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        AnnotatedElement parent = injectee.getParent();
        if (!(parent instanceof Method)) {
            throw new AssertionError("The AlternateInjectionResolver only works on methods (for now)");
        }
        
        Method method = (Method) parent;
        
        Annotation annotations[] = method.getParameterAnnotations()[injectee.getPosition()];
        HttpParameter httpParam = getHttpParameter(annotations);
        if (httpParam == null) {
            return systemResolver.resolve(injectee, root);
        }
        
        int index = httpParam.value();
        String fromRequest = request.getPathElement(index);
        if (fromRequest == null) {
            throw new AssertionError("There should have been a value at index " + index);
        }
        
        Class<?> injecteeType = method.getParameterTypes()[injectee.getPosition()];
        if (int.class.equals(injecteeType)) {
            return Integer.parseInt(fromRequest);
        }
        if (long.class.equals(injecteeType)) {
            return Long.parseLong(fromRequest);
        }
        if (String.class.equals(injecteeType)) {
            return fromRequest;
        }
        
        throw new AssertionError("Unknown type conversion: " + injecteeType);
    }
    
    private static HttpParameter getHttpParameter(Annotation annotations[]) {
        for (Annotation anno : annotations) {
            if (HttpParameter.class.equals(anno.annotationType())) {
                return (HttpParameter) anno;
            }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
     */
    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
     */
    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

}
