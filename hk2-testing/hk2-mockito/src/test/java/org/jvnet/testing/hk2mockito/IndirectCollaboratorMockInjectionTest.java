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

package org.jvnet.testing.hk2mockito;

import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.DeepService;
import org.jvnet.testing.hk2testng.HK2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * This test implies the ability to build integration tests and mock some of the indirect dependencies.
 *
 * @author Attila Houtkooper
 */
@HK2
public class IndirectCollaboratorMockInjectionTest {

    /**
     * Incidentally this test also helps verify that the order in which the components are declared does not affect the
     * outcome.
     */
    @MC
    @Inject
    BasicGreetingService indirectCollaborator;

    @SUT
    @Inject
    DeepService sut;

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(indirectCollaborator).isNotNull();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(indirectCollaborator).isSpy()).isFalse();
        assertThat(mockingDetails(indirectCollaborator).isMock()).isTrue();
    }

    @BeforeMethod
    public void init() {
        reset(sut, indirectCollaborator);
    }

    @Test
    public void callToGreetShouldCallCollboratorGreet() {
        String greeting = "Hi!";
        given(indirectCollaborator.greet()).willReturn(greeting);

        String result = sut.greet();

        assertThat(result).isEqualTo(greeting);
        verify(indirectCollaborator).greet();
    }

}
