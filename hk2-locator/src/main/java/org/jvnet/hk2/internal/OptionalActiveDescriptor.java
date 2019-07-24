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
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 *
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
        Annotation[] optionalAdded = qualifierAnnotations.toArray(new Annotation[qualifierAnnotations.size()]);

        ServiceHandle<T> handle = locator.getServiceHandle(requiredType, optionalAdded);
        if (handle == null) {
            return Optional.empty();
        }
        T service = handle.getService();
        return Optional.ofNullable(service);
    }
    
}
