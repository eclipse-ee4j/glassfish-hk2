/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.messaging.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class EventHeaderJIT implements JustInTimeInjectionResolver {
    @Inject
    private ServiceLocator locator;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        AnnotatedElement ae = failedInjectionPoint.getParent();
        if (!(ae instanceof Method)) return false;
        
        int paramIndex = failedInjectionPoint.getPosition();
        if (paramIndex < 0) return false;
        
        Method method = (Method) ae;
        
        if (!String.class.equals(method.getParameterTypes()[paramIndex])) return false;
        
        Annotation paramAnnotations[] = method.getParameterAnnotations()[paramIndex];
        EventHeader header = null;
        for (int lcv = 0; lcv < paramAnnotations.length; lcv++) {
            Annotation paramAnnotation = paramAnnotations[lcv];
            
            if (paramAnnotation.annotationType().equals(EventHeader.class)) {
                header = (EventHeader) paramAnnotation;
                break;
            }
        }
        
        if (header == null) return false;
        
        // Well, we actually have it now, create a descriptor and add it
        AbstractActiveDescriptor<?> descriptor = BuilderHelper.createConstantDescriptor(header.value(), null, String.class);
        descriptor.addQualifierAnnotation(header);
        
        ServiceLocatorUtilities.addOneDescriptor(locator, descriptor, false);
        
        return true;
    }

}
