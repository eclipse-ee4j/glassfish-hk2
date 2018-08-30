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

import javax.inject.Singleton;

import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.Validator;

/**
 * @author jwells
 *
 */
@Singleton
public class UnbindValidator implements Validator {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Validator#validate(org.glassfish.hk2.api.Operation, org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean validate(ValidationInformation info) {
        switch (info.getOperation()) {
        case LOOKUP:
        case BIND:
            return true;
        }
        
        if (info.getCandidate().getAdvertisedContracts().contains(NeverUnbindMeService.class.getName())) {
            return false;
        }
        
        return true;
    }

}
