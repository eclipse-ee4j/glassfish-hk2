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

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This guy notifies people of stuff
 * 
 * @author jwells
 *
 */
@Singleton
public class Notifier {
    public final static String DEFAULT_NAME = "Alice";
    
    private final HashSet<Notifyee> notifyTheseGuys = new HashSet<Notifyee>();
    private final String name;
    
    @Inject
    public Notifier() {
        this(DEFAULT_NAME);
    }
    
    public Notifier(String name) {
        this.name = name;
    }
    
    public void addNotifyee(Notifyee hi) {
        notifyTheseGuys.add(hi);
    }
    
    public void removeNotifyee(Notifyee bye) {
        notifyTheseGuys.remove(bye);
    }
    
    public void notify(String message) {
        for (Notifyee notifyee : notifyTheseGuys) {
            notifyee.notifyMe(name, message);
        }
    }

}
