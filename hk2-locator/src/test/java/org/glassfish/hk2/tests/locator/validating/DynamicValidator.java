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

package org.glassfish.hk2.tests.locator.validating;

import java.util.HashSet;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.Validator;

/**
 * @author jwells
 *
 */
@Singleton
public class DynamicValidator implements Validator {
    private final HashSet<String> badGuys = new HashSet<String>();
    
    /**
     * Adds an implementation class that should not work
     * 
     * @param badGuy the bad guy to disallow
     */
    public void addBadGuy(String badGuy) {
        if (badGuy == null) return;
        synchronized (badGuys) {
            badGuys.add(badGuy);
        }
    }
    
    /**
     * Removes an implementation class that should not work (i.e.,
     * this implementation class should work again)
     * 
     * @param badGuy
     */
    public void removeBadGuy(String badGuy) {
        if (badGuy == null) return;
        synchronized (badGuys) {
            badGuys.remove(badGuy);
        }
    }

    @Override
    public boolean validate(ValidationInformation info) {
        ActiveDescriptor<?> ad = info.getCandidate();
        synchronized (badGuys) {
            if (badGuys.contains(ad.getImplementation())) return false;
        }
        
        return true;
    }

}
