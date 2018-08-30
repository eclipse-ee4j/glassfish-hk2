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

package org.glassfish.hk2.configuration.internal;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Operation;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.api.Visibility;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ConfiguredValidator implements Validator {
    private boolean validateLookup(ValidationInformation info) {
        ActiveDescriptor<?> candidate = info.getCandidate();
        if (candidate.getName() != null) {
            // Any named 
            return true;
        }
        
        if (info.getInjectee() != null) {
            // May not be injected anywhere
            return false;
        }
        
        Filter f = info.getFilter();
        if ((f != null) && (f instanceof NoNameTypeFilter)) {
            // OK, we are getting this internally
            return true;
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Validator#validate(org.glassfish.hk2.api.ValidationInformation)
     */
    @Override
    public boolean validate(ValidationInformation info) {
        if (Operation.LOOKUP.equals(info.getOperation())) {
            return validateLookup(info);
        }
        
        if (Operation.BIND.equals(info.getOperation())) {
            return true;
            
        }
        
        if (Operation.UNBIND.equals(info.getOperation())) {
            return true;
        }
        
        // Unknown operation, I guess it is ok
        return true;
    }

}
