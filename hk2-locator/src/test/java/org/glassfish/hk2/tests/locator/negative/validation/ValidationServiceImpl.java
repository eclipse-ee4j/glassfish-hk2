/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.validation;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class ValidationServiceImpl implements ValidationService {
    private final Validator VALIDATOR = new ValidatorImpl(this);
    
    private boolean throwFromValidate = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getLookupFilter()
     */
    @Override
    public Filter getLookupFilter() {
        return BuilderHelper.createContractFilter(SimpleService.class.getName());
    }
    
    public void setThrowFromValidate(boolean throwFromValidate) {
        this.throwFromValidate = throwFromValidate;
    }
    
    private boolean getThrowFromValidate() {
        return throwFromValidate;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getValidator()
     */
    @Override
    public Validator getValidator() {
        return VALIDATOR;
    }
    
    private static class ValidatorImpl implements Validator {
        private final ValidationServiceImpl parent;
        
        private ValidatorImpl(ValidationServiceImpl parent) {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.Validator#validate(org.glassfish.hk2.api.ValidationInformation)
         */
        @Override
        public boolean validate(ValidationInformation info) {
            if (parent.getThrowFromValidate()) {
                throw new AssertionError(ValidateThrowsTest.EXPECTED_EXCEPTION);
            }
            
            return true;
        }
        
    }

}
