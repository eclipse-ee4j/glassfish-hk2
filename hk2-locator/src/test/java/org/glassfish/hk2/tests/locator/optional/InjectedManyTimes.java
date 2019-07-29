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

package org.glassfish.hk2.tests.locator.optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.glassfish.hk2.api.IterableProvider;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Optional;

/**
 * @author jwells
 *
 */
@Singleton
public class InjectedManyTimes {
    @Inject @Optional
    private SimpleService simpleByField;
    
    @Inject @Optional
    private OptionalService optionalByField;
    
    @Inject @Optional
    private Provider<OptionalService> optionalByProvider;
    
    @Inject @Optional
    private Iterable<OptionalService> optionalByIterable;
    
    @Inject @Optional
    private IterableProvider<OptionalService> optionalByIterableProvider;
    
    @Inject
    private java.util.Optional<OptionalService> optionalByContained;
    
    @Inject
    private java.util.Optional<SimpleService> simpleByContained;
    
    @Inject
    private java.util.Optional<java.util.Optional<SimpleService>> simpleTwiceOptional;
    
    @Inject
    private java.util.Optional<Provider<OptionalService>> optionalByProviderContained;
    
    @Inject
    private java.util.Optional optionalProvided;
    
    @Inject
    private Widget nullWidget;
    
    @Inject
    private java.util.Optional<Widget> optionalWidget;
    
    private final SimpleService simpleByConstructor;
    private final OptionalService optionalByConstructor;
    
    private SimpleService simpleByMethod;
    private OptionalService optionalByMethod;
    
    private boolean isValid = false;
    
    @Inject
    private InjectedManyTimes(@Optional SimpleService simpleByConstructor,
            @Optional OptionalService optionalByConstructor) {
        this.simpleByConstructor = simpleByConstructor;
        this.optionalByConstructor = optionalByConstructor;
    }
    
    @Inject
    private void viaMethod(@Optional OptionalService optionalByMethod,
            @Optional SimpleService simpleByMethod) {
        this.simpleByMethod = simpleByMethod;
        this.optionalByMethod = optionalByMethod;
        
    }
    
    @SuppressWarnings("unused")
    private void postConstruct() {
        Assert.assertNotNull(simpleByField);
        Assert.assertNull(optionalByField);
        
        Assert.assertNotNull(simpleByConstructor);
        Assert.assertNull(optionalByConstructor);
        
        Assert.assertNotNull(simpleByMethod);
        Assert.assertNull(optionalByMethod);
        
        Assert.assertNotNull(optionalByIterable);
        int lcv = 0;
        for (OptionalService os : optionalByIterable) {
            lcv++;
        }
        Assert.assertEquals(0, lcv);
        
        Assert.assertNotNull(optionalByIterableProvider);
        Assert.assertNull(optionalByIterableProvider.getHandle());
        Assert.assertNull(optionalByIterableProvider.get());
        
        lcv = 0;
        for (OptionalService os : optionalByIterableProvider) {
            lcv++;
        }
        Assert.assertEquals(0, lcv);
        
        Assert.assertNotNull(optionalByProvider);
        Assert.assertNull(optionalByProvider.get());
        
        Assert.assertFalse(optionalByContained.isPresent());
        Assert.assertTrue(simpleByContained.isPresent());
        Assert.assertTrue(simpleTwiceOptional.get().isPresent());
        
        Assert.assertTrue(optionalByProviderContained.isPresent());
        Assert.assertNull(optionalByProviderContained.get().get());
        Assert.assertEquals("testvalue", optionalProvided.get());
        
        Assert.assertNull(nullWidget);
        Assert.assertFalse(optionalWidget.isPresent());
        
        isValid = true;
    }
    
    public boolean isValid() {
        return isValid;
    }

}
