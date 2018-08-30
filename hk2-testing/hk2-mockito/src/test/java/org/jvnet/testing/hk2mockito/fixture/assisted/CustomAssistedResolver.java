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

package org.jvnet.testing.hk2mockito.fixture.assisted;

import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.inject.Named;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.jvnet.hk2.annotations.Service;

/**
 *
 * @author Sharmarke Aden
 */
@Rank(50)
@Service
public class CustomAssistedResolver implements InjectionResolver<Inject> {

    private final InjectionResolver<Inject> systemResolver;

    @Inject
    CustomAssistedResolver(@Named(SYSTEM_RESOLVER_NAME) InjectionResolver systemResolver) {
        this.systemResolver = systemResolver;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        Type type = injectee.getRequiredType();

        if (type instanceof Class && CustomService.class.isAssignableFrom((Class) type)) {
            return new CustomService();
        }

        return systemResolver.resolve(injectee, root);
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

}
