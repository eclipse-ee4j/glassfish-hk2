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

package org.jvnet.testing.hk2testng.service.provider;

import org.jvnet.testing.hk2testng.service.ProvidedService;
import org.jvnet.testing.hk2testng.service.SecondaryService;
import javax.inject.Inject;
import javax.inject.Provider;
import org.glassfish.hk2.api.Factory;
import org.jvnet.hk2.annotations.Service;

/**
 *
 * @author saden
 */
@Service
public class ProvidedServiceProvider implements Factory<ProvidedService> {

    private final Provider<SecondaryService> secondaryServiceProvider;

    @Inject
    ProvidedServiceProvider(Provider<SecondaryService> secondaryServiceProvider) {
        this.secondaryServiceProvider = secondaryServiceProvider;
    }

    @Override
    public ProvidedService provide() {
        return new ProvidedServiceImpl(secondaryServiceProvider.get());
    }

    @Override
    public void dispose(ProvidedService providedService) {
        providedService = null;
    }
}
