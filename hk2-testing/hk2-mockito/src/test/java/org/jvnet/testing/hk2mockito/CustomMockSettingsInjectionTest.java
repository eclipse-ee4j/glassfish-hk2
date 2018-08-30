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

import java.io.Closeable;
import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.ConstructorInjectionGreetingService;
import org.jvnet.testing.hk2testng.HK2;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import org.mockito.internal.util.MockUtil;
import org.mockito.mock.MockCreationSettings;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Sharmarke Aden
 */
@HK2
public class CustomMockSettingsInjectionTest {

    @SUT
    @Inject
    ConstructorInjectionGreetingService sut;
    @MC(name = "customName", answer = Answers.RETURNS_MOCKS, extraInterfaces = Closeable.class)
    @Inject
    BasicGreetingService collaborator;

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(collaborator).isNotNull();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator).isMock()).isTrue();
		MockCreationSettings settings = MockUtil.getMockHandler(collaborator).getMockSettings();

        assertThat(settings.getMockName().toString()).isEqualTo("customName");
        assertThat(settings.getDefaultAnswer()).isEqualTo(Answers.RETURNS_MOCKS.get());
        assertThat(settings.getExtraInterfaces()).containsOnly(Closeable.class);
    }

    @BeforeMethod
    public void init() {
        reset(sut, collaborator);
    }

    @Test
    public void callToGreetShouldCallCollboratorGreet() {
        String greeting = "Hello!";
        given(collaborator.greet()).willReturn(greeting);

        String result = sut.greet();

        assertThat(result).isEqualTo(greeting);
        verify(sut).greet();
        verify(collaborator).greet();
    }

}
