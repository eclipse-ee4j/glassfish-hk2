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

import javax.inject.Singleton;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;

/**
 * Here just to ensure that the PerLookupService passed to the subscribe method
 * is in fact destroyed after the subscribe method has been called
 * 
 * @author jwells
 *
 */
@Singleton @MessageReceiver
public class ServiceWithPerLookupSubscription {
    private PerLookupService subscriptionService;
    
    public void subscribe(@SubscribeTo Foo foo, PerLookupService perLookupService) {
        if (perLookupService.getNumTimesDestroyed() != 0) {
            return;
        }
        
        subscriptionService = perLookupService;
    }
    
    public boolean isSubscriptionServiceDead() {
        return subscriptionService.getNumTimesDestroyed() != 0;
    }

}
