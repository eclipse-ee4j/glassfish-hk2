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

package org.glassfish.hk2.tests.locator.negative.dynamicconfig;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
@Singleton
public class DynamicConfigErrorService implements ErrorService {
    private MultiException configException;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorService#onFailure(org.glassfish.hk2.api.ErrorInformation)
     */
    @Override
    public void onFailure(ErrorInformation errorInformation)
            throws MultiException {
        if (ErrorType.DYNAMIC_CONFIGURATION_FAILURE.equals(errorInformation.getErrorType())) {
            configException = errorInformation.getAssociatedException();
        }
    }
    
    public MultiException getConfigException() {
        return configException;
    }
}
