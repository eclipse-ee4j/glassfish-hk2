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

package org.acme.security;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

/**
 * An implementation of the security service that uses permission checks for
 * everything
 * 
 * @author jwells
 *
 */
@Service @Singleton
public class ValidationServiceImpl implements ValidationService {
    private final ValidatorImpl validator = new ValidatorImpl();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getLookupFilter()
     */
    @Override
    public Filter getLookupFilter() {
        return BuilderHelper.allFilter();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getValidator()
     */
    @Override
    public Validator getValidator() {
        return validator;
    }
}
