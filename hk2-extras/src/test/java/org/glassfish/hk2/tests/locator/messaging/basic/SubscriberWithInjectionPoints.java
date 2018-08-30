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

package org.glassfish.hk2.tests.locator.messaging.basic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Self;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Optional;

/**
 * This subscriber has many different kinds of injection points
 * 
 * @author jwells
 */
@Singleton @MessageReceiver
public class SubscriberWithInjectionPoints {
    private boolean singletonInjectionPointCalled = false;
    private boolean perLookupInjectionPointCalled = false;
    private boolean multi1InjectionPointCalled = false;
    private boolean multi2InjectionPointCalled = false;
    private boolean multi3InjectionPointCalled = false;
    private boolean optionalInjectionPointCalled = false;
    private boolean selfInjectionPointCalled = false;
    
    @Inject @Self
    private ActiveDescriptor<?> selfie;
    
    @SuppressWarnings("unused")
    private void singletonSubscriber(@SubscribeTo Foo foo, SingletonService singletonService) {
        if (foo != null && singletonService != null) {
            singletonInjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " singletonService=" + singletonService + " in singletonSubscriber");
    }
    
    protected void perLookupSubscriber(@SubscribeTo Foo foo, PerLookupService perLookupService) {
        if (foo != null && perLookupService != null) {
            perLookupInjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " perLookupService=" + perLookupService + " in perLookupSubscriber");
    }
    
    /* package */ void multi1Subscriber(@SubscribeTo Foo foo, PerLookupService perLookupService, SingletonService singletonService) {
        if (foo != null && perLookupService != null && singletonService != null) {
            multi1InjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " perLookupService=" + perLookupService + " singletonService=" + singletonService + " in multi1Subscriber");
    }
    
    /**
     * Different order than multi1 or multi3
     * 
     * @param perLookupService
     * @param foo
     * @param singletonService
     */
    public void multi2Subscriber(PerLookupService perLookupService, @SubscribeTo Foo foo, SingletonService singletonService) {
        if (foo != null && perLookupService != null && singletonService != null) {
            multi2InjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " perLookupService=" + perLookupService + " singletonService=" + singletonService + " in multi2Subscriber");
    }
    
    /**
     * Different order than multi1 or multi2
     * 
     * @param perLookupService
     * @param singletonService
     * @param foo
     */
    @SuppressWarnings("unused")
    private void multi3Subscriber(PerLookupService perLookupService, SingletonService singletonService, @SubscribeTo Foo foo) {
        if (foo != null && perLookupService != null && singletonService != null) {
            multi3InjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " perLookupService=" + perLookupService + " singletonService=" + singletonService + " in multi3Subscriber");
    }
    
    @SuppressWarnings("unused")
    private void optionalSubscriber(@SubscribeTo Foo foo, @Optional OptionalService optional) {
        if (foo != null && optional == null) {
            optionalInjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " optionalService=" + optional + " in optionalSubscriber");
        
    }
    
    @SuppressWarnings("unused")
    private void selfSubscriber(@SubscribeTo Foo foo, @Self ActiveDescriptor<?> selfie) {
        if (foo != null && selfie != null && this.selfie.equals(selfie)) {
            selfInjectionPointCalled = true;
            return;
        }
        
        Assert.fail("foo=" + foo + " selfie=" + selfie + " this.selfie=" + this.selfie + " in selfSubscriber");
    }
    
    public void check() {
        Assert.assertTrue(singletonInjectionPointCalled);
        Assert.assertTrue(perLookupInjectionPointCalled);
        Assert.assertTrue(multi1InjectionPointCalled);
        Assert.assertTrue(multi2InjectionPointCalled);
        Assert.assertTrue(multi3InjectionPointCalled);
        Assert.assertTrue(optionalInjectionPointCalled);
        Assert.assertTrue(selfInjectionPointCalled);
    }

}
