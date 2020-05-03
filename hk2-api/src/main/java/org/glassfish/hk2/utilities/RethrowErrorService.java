/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Objects;

import jakarta.inject.Singleton;

import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.MultiException;

/**
 * This is an implementation of {@link ErrorService} that simply rethrows
 * the exception caught.
 * <p>
 * Do not use this service in secure applications where callers to lookup
 * should not be given the information that they do NOT have access
 * to a service.
 *
 * @author jwells
 * @author David Matejcek
 */
@Singleton
public class RethrowErrorService implements ErrorService {

    @Override
    public void onFailure(ErrorInformation errorInformation) throws MultiException {
        // this may hide the argument, but it would be a really serious bug.
        Objects.requireNonNull(errorInformation, "errorInformation must not be null!");
        if (ErrorType.FAILURE_TO_REIFY.equals(errorInformation.getErrorType())) {
            final MultiException me = errorInformation.getAssociatedException();
            if (me == null) {
                return;
            }
            throw me;
        }
    }
}
