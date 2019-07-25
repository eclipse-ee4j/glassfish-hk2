/*
 * Copyright (c) 2019 Payara Service Ltd. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Provider;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * Descriptor for {@link Optional} to allow for injection of a service if it exists or else
 * {@link Optional#EMPTY}.  It will also allow for injection of an {@link Optional} directly as well.
 * @author jonathan coustick
 */
public class OptionalActiveDescriptor<T> extends AbstractActiveDescriptor<Optional> {
    
    private Injectee injectee;
    private Type requiredType;
    private ServiceLocatorImpl locator;
    
    /**
     * For serialization
     */
    public OptionalActiveDescriptor() {
        super();
    }
    
    /*package-private*/ OptionalActiveDescriptor(Injectee injectee, ServiceLocatorImpl locator, Type requiredType) {
        super(new HashSet<Type>(),
                PerLookup.class,
                null,
                new HashSet<Annotation>(),
                DescriptorType.CLASS,
                DescriptorVisibility.NORMAL,
                0,
                null,
                null,
                locator.getPerLocatorUtilities().getAutoAnalyzerName(injectee.getInjecteeClass()),
                null);
        
        this.requiredType = requiredType;
        this.injectee = injectee;
        this.locator = locator;
    }

    @Override
    public Class<?> getImplementationClass() {
        return Optional.class;
    }

    @Override
    public Type getImplementationType() {
        return Optional.class;
    }

    @Override
    public Optional<T> create(ServiceHandle<?> root) {
        Set<Annotation> qualifierAnnotations = getQualifierAnnotations();
        Annotation[] annotationsArray = qualifierAnnotations.toArray(new Annotation[qualifierAnnotations.size()]);

        ServiceHandle<T> handle = locator.getServiceHandle(requiredType, annotationsArray);
        if (handle == null) {
            Class<?> rawType = ReflectionHelper.getRawClass(requiredType);
            if (Provider.class.equals(rawType) || Iterable.class.equals(rawType) || IterableProvider.class.equals(rawType) || Topic.class.equals(rawType)
                    || Optional.class.equals(rawType)) {
                SystemInjecteeImpl copy = new SystemInjecteeImpl(rawType, injectee.getRequiredQualifiers(), injectee.getPosition(),
                        injectee.getParent(), true, injectee.isSelf(), injectee.getUnqualified(), this);
                ActiveDescriptor descriptor = locator.getInjecteeDescriptor(copy);
                return Optional.of((T) descriptor.create(root));
            }
            ServiceHandle<Optional<T>> optionalHandle = locator.getServiceHandle(injectee.getRequiredType(), annotationsArray);
            if (optionalHandle == null) {
                return Optional.empty();
            } else {
                return optionalHandle.getService();
            }
        }
        T service = handle.getService();
        return Optional.ofNullable(service);
    }
    
}
