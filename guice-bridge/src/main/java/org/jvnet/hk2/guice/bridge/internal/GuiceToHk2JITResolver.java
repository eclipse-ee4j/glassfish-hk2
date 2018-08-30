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

package org.jvnet.hk2.guice.bridge.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author jwells
 *
 */
@Singleton
public class GuiceToHk2JITResolver implements JustInTimeInjectionResolver {
    private final ServiceLocator locator;
    private final Injector guiceInjector;

    /* package */ GuiceToHk2JITResolver(ServiceLocator locator,
            Injector guiceInjector) {
        this.locator = locator;
        this.guiceInjector = guiceInjector;
    }

    /**
     * This tries every qualifier in the injectee
     * @param injectee The injectee to look for a binding for
     * @return The binding found, or null if none could be found
     */
    private Binding<?> findBinding(Injectee injectee) {
        if (injectee.getRequiredQualifiers().isEmpty()) {
            Key<?> key = Key.get(injectee.getRequiredType());

            try {
                return guiceInjector.getBinding(key);
            } catch (ConfigurationException ce) {
                return null;
            }
        }

        if (injectee.getRequiredQualifiers().size() > 1) {
            return null;
        }

        for (Annotation annotation : injectee.getRequiredQualifiers()) {
            Key<?> key = Key.get(injectee.getRequiredType(), annotation);

            Binding<?> retVal = null;
            try {
                retVal = guiceInjector.getBinding(key);
            } catch (ConfigurationException ce) {
                return null;
            }

            if (retVal != null) return retVal;
        }

        return null;
    }

    /**
     * Gets the class from the given type
     *
     * @param type The type to find the class from
     * @return The class associated with this type, or null
     * if the class cannot be found
     */
    public static Class<?> getClassFromType(Type type) {
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;

            return (Class<?>) pt.getRawType();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        if (failedInjectionPoint.getParent() == null) {
            // Jersey looks things up expecting to find only
            // hk2 versions of things, which sometimes confuses
            // the bridge.  The new feature added to JIT resolvers
            // which allows for JIT to work for lookups as well
            // as for Injection points breaks the jersey-guice
            // bridge, so it has been disabled to go back to the
            // old behavior.  It would be nice to go to Jersey
            // and have them fix this since having guice lookup
            // is a good feature
            return false;
        }

        Class<?> implClass = getClassFromType(failedInjectionPoint.getRequiredType());
        if (implClass == null) return false;

        Binding<?> binding = findBinding(failedInjectionPoint);
        if (binding == null) return false;

        HashSet<Type> contracts = new HashSet<Type>();
        contracts.add(failedInjectionPoint.getRequiredType());

        Set<Annotation> qualifiers = new HashSet<Annotation>(failedInjectionPoint.getRequiredQualifiers());

        GuiceServiceHk2Bean guiceBean = new GuiceServiceHk2Bean(contracts, qualifiers, implClass, binding);

        ServiceLocatorUtilities.addOneDescriptor(locator, guiceBean);

        return true;
    }

}
