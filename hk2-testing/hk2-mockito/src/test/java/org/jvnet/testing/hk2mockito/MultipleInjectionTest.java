/*
 * Copyright (c) 2019 Roughdot.nl and/or its affiliates. All rights reserved.
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

package org.jvnet.testing.hk2mockito;

import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.NamedGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.MultipleConstructorInjectionService;
import org.jvnet.testing.hk2testng.HK2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockingDetails;

/**
 *
 * @author Attila Houtkooper
 */
@HK2
public class MultipleInjectionTest {

    @SUT
    @Inject
    private MultipleConstructorInjectionService sut;

    @MC
    @Inject
    private BasicGreetingService collaborator1;

    @MC
    @Inject
    private NamedGreetingService collaborator2;

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(collaborator1).isNotNull();
        assertThat(collaborator2).isNotNull();
        assertThat(mockingDetails(sut).isMock()).isTrue();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator1).isMock()).isTrue();
        assertThat(mockingDetails(collaborator2).isMock()).isTrue();
        assertThat(mockingDetails(collaborator1).isSpy()).isFalse();
        assertThat(mockingDetails(collaborator2).isSpy()).isFalse();
    }

    @Test
    public void injectedServicesEqualSutProperties() {
        assertThat(sut.basicGreetingService).isEqualTo(collaborator1);
        assertThat(sut.namedGreetingService).isEqualTo(collaborator2);
    }

}
