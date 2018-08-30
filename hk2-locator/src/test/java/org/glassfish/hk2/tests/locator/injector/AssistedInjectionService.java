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

package org.glassfish.hk2.tests.locator.injector;

import javax.inject.Singleton;

import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.jvnet.hk2.annotations.Optional;

/**
 * @author jwells
 *
 */
@Singleton
public class AssistedInjectionService {
    private Event event;
    private SpecialService special;
    private SimpleService simple;
    private double foo;
    private UnknownService unknown = new UnknownService();
    
    public void aMethod(@SubscribeTo Event event, @Special SpecialService special, SimpleService simple, double foo, @Optional UnknownService unknown) {
        this.event = event;
        this.special = special;
        this.simple = simple;
        this.foo = foo;
        this.unknown = unknown;
    }
    
    public Event getEvent() {
        return event;
    }

    public SpecialService getSpecial() {
        return special;
    }

    public SimpleService getSimple() {
        return simple;
    }

    public double getFoo() {
        return foo;
    }
    
    public UnknownService getUnknown() {
        return unknown;
    }

}
