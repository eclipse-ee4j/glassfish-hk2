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

package org.jvnet.testing.hk2testng;

import org.jvnet.testing.hk2testng.service.ProvidedService;
import org.jvnet.testing.hk2testng.service.provider.ProvidedServiceProvider;
import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

/**
 *
 * @author saden
 */
@HK2
public class ProvidedInjectionTest {

    @Inject
    ProvidedService providedService;

    @Inject
    ProvidedServiceProvider providedServiceProvider;

    @Test
    public void assertProvidedServiceInjection() {
        assertThat(providedService)
                .isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(providedService.getSecondaryService())
                .isNotNull();
    }

    @Test
    public void asserProviderServiceProviderInject() {
        assertThat(providedServiceProvider)
                .isNotNull();
    }
}
