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

package org.glassfish.hk2.utilities;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;

/**
 * This is a greedy resolve that will add in any class
 * that has failed to be resolved.  It uses {@link ServiceLocatorUtilities#addClasses(org.glassfish.hk2.api.ServiceLocator, Class...)}
 * in order to add classes, and hence will use the default
 * class analyzer to discover injection points and constructors.
 * <p>
 * If the injected class is an interface the interface may use
 * the {@link GreedyDefaultImplementation} in order to specify
 * the class that should be used when another implementation
 * of this interface cannot be found.
 * <p>
 * WARNING: This resolve should be used with care as it
 * could cause unexpected class files to be instantiated
 * by hk2
 * 
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class GreedyResolver implements JustInTimeInjectionResolver {
    private final ServiceLocator locator;
    
    @Inject
    private GreedyResolver(ServiceLocator locator) {
        this.locator = locator;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        Type type = failedInjectionPoint.getRequiredType();
        if (type == null) return false;
        
        Class<?> clazzToAdd = null;
        if (type instanceof Class) {
            clazzToAdd = (Class<?>) type;
        }
        else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                clazzToAdd = (Class<?>) rawType;
            }
        }
        
        if (clazzToAdd == null) return false;
        if (clazzToAdd.isInterface()) {
            GreedyDefaultImplementation gdi = clazzToAdd.getAnnotation(GreedyDefaultImplementation.class);
            if (gdi != null) {
                clazzToAdd = gdi.value();
            }
            else {
                return false;
            }
        }
        
        ServiceLocatorUtilities.addClasses(locator, clazzToAdd);
        return true;
    }

}
