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

package org.glassfish.hk2.tests.locator.negative.injector;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * @author jwells
 * @param <T> This is invalid, an implementation must provide a real type
 *
 */
public class TypeVariableInjectionResolver<T> implements InjectionResolver<T> {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
     */
    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
     */
    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

}
