/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * This service is added during a lookup operation as opposed to being
 * from an actual injection point
 * 
 * @author jwells
 *
 */
@Singleton
public class SimpleService4JITResolver implements JustInTimeInjectionResolver {
    @Inject
    private ServiceLocator locator;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        if (!SimpleService4.class.equals(failedInjectionPoint.getRequiredType()))
                return false;
        
        Descriptor d = BuilderHelper.link(SimpleService4.class).build();
        ServiceLocatorUtilities.addOneDescriptor(locator, d);
        return true;
    }

}
