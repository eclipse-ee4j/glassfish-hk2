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

package org.glassfish.hk2.tests.locator.justintime;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;

import org.glassfish.hk2.utilities.BuilderHelper;

@Singleton
public class EvilJITResolver implements JustInTimeInjectionResolver {
    
    private int numTimesCalled = 0;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Configuration, org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {

      Type injectionType = failedInjectionPoint.getRequiredType();
        if (!(injectionType instanceof Class)) return false;
        
        Class<?> injectionClass = (Class<?>) injectionType;
        if (!UnimplementedContract.class.equals(injectionClass)) return false;
        
        numTimesCalled++;

        // Evil; we didn't actually do anything.
        return true;
    }
    
    /**
     * For use by the test
     * @return The number of times this resolver has been called
     */
    public int getNumTimesCalled() {
        return numTimesCalled;
    }

}
