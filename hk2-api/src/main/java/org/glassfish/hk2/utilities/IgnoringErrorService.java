/*
 * Copyright (c) 2019 Payara Services and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.utilities;

import jakarta.inject.Singleton;

import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.MultiException;


/**
 * This is an implementation of {@link ErrorService} that simply swallows
 * the exception caught.
 * <p>
 * Use this service in secure applications where callers to lookup
 * should not be given the information that they do NOT have access
 * to a service.
 *
 * @author David Matejcek
 */
@Singleton
public class IgnoringErrorService implements ErrorService {

    @Override
    public void onFailure(ErrorInformation errorInformation) throws MultiException {
        // completely ignores all errors
    }
}
