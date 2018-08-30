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

package org.glassfish.hk2.tests.locator.lifecycle;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
@PerLookup
public class KnownInjecteeNotifyee implements Notifyee {
    @SuppressWarnings("unused")
    @Inject @Named(Notifier.DEFAULT_NAME)
    private Notifier myNotifier;
    
    private final List<String> notifications = new LinkedList<String>();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.lifecycle.Notifyee#notifyMe(java.lang.String, java.lang.String)
     */
    @Override
    public void notifyMe(String nameOfNotifier, String notification) {
        notifications.add(nameOfNotifier + ":" + notification);

    }
    
    public List<String> getNotifications() {
        return notifications;
        
    }

}
